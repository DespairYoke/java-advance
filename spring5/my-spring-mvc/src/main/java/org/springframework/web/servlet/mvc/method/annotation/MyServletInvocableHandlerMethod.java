package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.MyView;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-09
 **/
public class MyServletInvocableHandlerMethod extends InvocableHandlerMethod {

    private static final Method CALLABLE_METHOD = ClassUtils.getMethod(Callable.class, "call");

    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    public MyServletInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }


    public MyServletInvocableHandlerMethod(Object handler, Method method) {
        super(handler, method);
    }

    public void setHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }

    MyServletInvocableHandlerMethod wrapConcurrentResult(Object result) {
        return new ConcurrentResultHandlerMethod(result, new ConcurrentResultMethodParameter(result));
    }

    private class ConcurrentResultHandlerMethod extends MyServletInvocableHandlerMethod {

        private final MethodParameter returnType;

        public ConcurrentResultHandlerMethod(final Object result, ConcurrentResultMethodParameter returnType) {
            super((Callable<Object>) () -> {
                if (result instanceof Exception) {
                    throw (Exception) result;
                }
                else if (result instanceof Throwable) {
                    throw new NestedServletException("Async processing failed", (Throwable) result);
                }
                return result;
            }, CALLABLE_METHOD);

            if (MyServletInvocableHandlerMethod.this.returnValueHandlers != null) {
                setHandlerMethodReturnValueHandlers(MyServletInvocableHandlerMethod.this.returnValueHandlers);
            }
            this.returnType = returnType;
        }

        /**
         * Bridge to actual controller type-level annotations.
         */
        @Override
        public Class<?> getBeanType() {
            return MyServletInvocableHandlerMethod.this.getBeanType();
        }

        /**
         * Bridge to actual return value or generic type within the declared
         * async return type, e.g. Foo instead of {@code DeferredResult<Foo>}.
         */
        @Override
        public MethodParameter getReturnValueType(@Nullable Object returnValue) {
            return this.returnType;
        }

        /**
         * Bridge to controller method-level annotations.
         */
        @Override
        public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
            return MyServletInvocableHandlerMethod.this.getMethodAnnotation(annotationType);
        }

        /**
         * Bridge to controller method-level annotations.
         */
        @Override
        public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
            return MyServletInvocableHandlerMethod.this.hasMethodAnnotation(annotationType);
        }
    }

    private class ConcurrentResultMethodParameter extends HandlerMethodParameter {

        @Nullable
        private final Object returnValue;

        private final ResolvableType returnType;

        public ConcurrentResultMethodParameter(Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
            this.returnType = (returnValue instanceof MyReactiveTypeHandler.CollectedValuesList ?
                    ((MyReactiveTypeHandler.CollectedValuesList) returnValue).getReturnType() :
                    ResolvableType.forType(super.getGenericParameterType()).getGeneric(0));
        }

        public ConcurrentResultMethodParameter(ConcurrentResultMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
            this.returnType = original.returnType;
        }

        @Override
        public Class<?> getParameterType() {
            if (this.returnValue != null) {
                return this.returnValue.getClass();
            }
            if (!ResolvableType.NONE.equals(this.returnType)) {
                return this.returnType.resolve(Object.class);
            }
            return super.getParameterType();
        }

        @Override
        public Type getGenericParameterType() {
            return this.returnType.getType();
        }

        @Override
        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            // Ensure @ResponseBody-style handling for values collected from a reactive type
            // even if actual return type is ResponseEntity<Flux<T>>
            return (super.hasMethodAnnotation(annotationType) ||
                    (annotationType == ResponseBody.class &&
                            this.returnValue instanceof MyReactiveTypeHandler.CollectedValuesList));
        }

        @Override
        public ConcurrentResultMethodParameter clone() {
            return new ConcurrentResultMethodParameter(this);
        }
    }


    private void setResponseStatus(ServletWebRequest webRequest) throws IOException {
        HttpStatus status = getResponseStatus();
        if (status == null) {
            return;
        }

        HttpServletResponse response = webRequest.getResponse();
        if (response != null) {
            String reason = getResponseStatusReason();
            if (StringUtils.hasText(reason)) {
                response.sendError(status.value(), reason);
            }
            else {
                response.setStatus(status.value());
            }
        }

        // To be picked up by RedirectView
        webRequest.getRequest().setAttribute(MyView.RESPONSE_STATUS_ATTRIBUTE, status);
    }

    public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
                                Object... providedArgs) throws Exception {

        Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
        setResponseStatus(webRequest);

        if (returnValue == null) {
            if (isRequestNotModified(webRequest) || getResponseStatus() != null || mavContainer.isRequestHandled()) {
                mavContainer.setRequestHandled(true);
                return;
            }
        }
        else if (StringUtils.hasText(getResponseStatusReason())) {
            mavContainer.setRequestHandled(true);
            return;
        }

        mavContainer.setRequestHandled(false);
        Assert.state(this.returnValueHandlers != null, "No return value handlers");
        try {
            this.returnValueHandlers.handleReturnValue(
                    returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
        }
        catch (Exception ex) {
            if (logger.isTraceEnabled()) {
                logger.trace(getReturnValueHandlingErrorMessage("Error handling return value", returnValue), ex);
            }
            throw ex;
        }
    }

    private boolean isRequestNotModified(ServletWebRequest webRequest) {
        return webRequest.isNotModified();
    }

    private String getReturnValueHandlingErrorMessage(String message, @Nullable Object returnValue) {
        StringBuilder sb = new StringBuilder(message);
        if (returnValue != null) {
            sb.append(" [type=").append(returnValue.getClass().getName()).append("]");
        }
        sb.append(" [value=").append(returnValue).append("]");
        return getDetailedErrorMessage(sb.toString());
    }


}
