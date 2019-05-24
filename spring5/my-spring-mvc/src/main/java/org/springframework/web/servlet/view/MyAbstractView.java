package org.springframework.web.servlet.view;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ContextExposingHttpServletRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.MyView;
import org.springframework.web.servlet.support.MyRequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-13
 **/
public abstract class MyAbstractView extends WebApplicationObjectSupport implements MyView, BeanNameAware {

    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";

    @Nullable
    private String contentType = DEFAULT_CONTENT_TYPE;

    private boolean exposePathVariables = true;

    private boolean exposeContextBeansAsAttributes = false;

    @Nullable
    private String requestContextAttribute;

    @Nullable
    private Set<String> exposedContextBeanNames;

    private final Map<String, Object> staticAttributes = new LinkedHashMap<>();
    @Nullable
    private String beanName;

    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
    }



    public void setRequestContextAttribute(@Nullable String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }

    public void setExposePathVariables(boolean exposePathVariables) {
        this.exposePathVariables = exposePathVariables;
    }

    public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
        this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
    }

    public void setExposedContextBeanNames(String... exposedContextBeanNames) {
        this.exposedContextBeanNames = new HashSet<>(Arrays.asList(exposedContextBeanNames));
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }


    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (logger.isTraceEnabled()) {
            logger.trace("Rendering view with name '" + this.beanName + "' with model " + model +
                    " and static attributes " + this.staticAttributes);
        }

        Map<String, Object> mergedModel = createMergedOutputModel(model, request, response);
        prepareResponse(request, response);
        renderMergedOutputModel(mergedModel, getRequestToExpose(request), response);
    }

    protected HttpServletRequest getRequestToExpose(HttpServletRequest originalRequest) {
        if (this.exposeContextBeansAsAttributes || this.exposedContextBeanNames != null) {
            WebApplicationContext wac = getWebApplicationContext();
            Assert.state(wac != null, "No WebApplicationContext");
            return new ContextExposingHttpServletRequest(originalRequest, wac, this.exposedContextBeanNames);
        }
        return originalRequest;
    }

    protected abstract void renderMergedOutputModel(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        if (generatesDownloadContent()) {
            response.setHeader("Pragma", "private");
            response.setHeader("Cache-Control", "private, must-revalidate");
        }
    }

    protected boolean generatesDownloadContent() {
        return false;
    }


    protected Map<String, Object> createMergedOutputModel(@Nullable Map<String, ?> model,
                                                          HttpServletRequest request, HttpServletResponse response) {

        @SuppressWarnings("unchecked")
        Map<String, Object> pathVars = (this.exposePathVariables ?
                (Map<String, Object>) request.getAttribute(MyView.PATH_VARIABLES) : null);

        // Consolidate static and dynamic model attributes.
        int size = this.staticAttributes.size();
        size += (model != null ? model.size() : 0);
        size += (pathVars != null ? pathVars.size() : 0);

        Map<String, Object> mergedModel = new LinkedHashMap<>(size);
        mergedModel.putAll(this.staticAttributes);
        if (pathVars != null) {
            mergedModel.putAll(pathVars);
        }
        if (model != null) {
            mergedModel.putAll(model);
        }

        // Expose RequestContext?
        if (this.requestContextAttribute != null) {
            mergedModel.put(this.requestContextAttribute, createRequestContext(request, response, mergedModel));
        }

        return mergedModel;
    }

    protected MyRequestContext createRequestContext(
            HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {

        return new MyRequestContext(request, response, getServletContext(), model);
    }

    protected void exposeModelAsRequestAttributes(Map<String, Object> model,
                                                  HttpServletRequest request) throws Exception {

        model.forEach((modelName, modelValue) -> {
            if (modelValue != null) {
                request.setAttribute(modelName, modelValue);
                if (logger.isDebugEnabled()) {
                    logger.debug("Added model object '" + modelName + "' of type [" + modelValue.getClass().getName() +
                            "] to request in view with name '" + getBeanName() + "'");
                }
            }
            else {
                request.removeAttribute(modelName);
                if (logger.isDebugEnabled()) {
                    logger.debug("Removed model object '" + modelName +
                            "' from request in view with name '" + getBeanName() + "'");
                }
            }
        });
    }
}
