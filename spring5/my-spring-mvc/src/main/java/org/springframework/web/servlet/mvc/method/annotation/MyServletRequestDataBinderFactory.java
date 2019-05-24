package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;

import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-07
 **/
public class MyServletRequestDataBinderFactory extends InitBinderDataBinderFactory {
    /**
     * Create a new InitBinderDataBinderFactory instance.
     *
     * @param binderMethods {@code @InitBinder} methods
     * @param initializer   for global data binder initialization
     */
    public MyServletRequestDataBinderFactory(List<InvocableHandlerMethod> binderMethods, WebBindingInitializer initializer) {
        super(binderMethods, initializer);
    }
    protected ServletRequestDataBinder createBinderInstance(
            @Nullable Object target, String objectName, NativeWebRequest request) throws Exception  {

        return new MyExtendedServletRequestDataBinder(target, objectName);
    }
}
