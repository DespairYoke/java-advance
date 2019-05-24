package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.MyRequestCondition;
import org.springframework.web.servlet.mvc.method.MyRequestMappingInfo;
import org.springframework.web.servlet.mvc.method.MyRequestMappingInfoHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public class MyRequestMappingHandlerMapping extends MyRequestMappingInfoHandlerMapping {


    @Nullable
    private StringValueResolver embeddedValueResolver;

    private MyRequestMappingInfo.BuilderConfiguration config = new MyRequestMappingInfo.BuilderConfiguration();

    @Override
    public void afterPropertiesSet() {
//        this.config = new RequestMappingInfo.BuilderConfiguration();
//        this.config.setUrlPathHelper(getUrlPathHelper());
//        this.config.setPathMatcher(getPathMatcher());
//        this.config.setSuffixPatternMatch(this.useSuffixPatternMatch);
//        this.config.setTrailingSlashMatch(this.useTrailingSlashMatch);
//        this.config.setRegisteredSuffixPatternMatch(this.useRegisteredSuffixPatternMatch);
//        this.config.setContentNegotiationManager(getContentNegotiationManager());

        super.afterPropertiesSet();
    }

//    @Override
//    protected boolean isHandler(Class beanType) {
//        return false;
//    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
                AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class));
    }


    @Override
    @Nullable
    protected MyRequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        MyRequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            MyRequestMappingInfo typeInfo = createRequestMappingInfo(handlerType); //handlerTyp 是类 这里为了解决类上的@RequestMapping使用
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }


    @Nullable
    private MyRequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);//判断某方法或者类是否含有@RequestMapping注解
        MyRequestCondition<?> condition = (element instanceof Class ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
    }

    @Nullable
    protected MyRequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return null;
    }

    @Nullable
    protected MyRequestCondition<?> getCustomMethodCondition(Method method) {
        return null;
    }


    protected MyRequestMappingInfo createRequestMappingInfo(
            RequestMapping requestMapping, @Nullable MyRequestCondition<?> customCondition) {

        MyRequestMappingInfo.Builder builder = MyRequestMappingInfo
                .paths(resolveEmbeddedValuesInPatterns(requestMapping.path()))
                .methods(requestMapping.method()) //请求方式get post...
                .params(requestMapping.params()) //
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces())
                .mappingName(requestMapping.name());
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }
        return builder.options(this.config).build();
    }
    protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
        if (this.embeddedValueResolver == null) {
            return patterns;
        }
        else {
            String[] resolvedPatterns = new String[patterns.length];
            for (int i = 0; i < patterns.length; i++) {
                resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
            }
            return resolvedPatterns;
        }
    }
}
