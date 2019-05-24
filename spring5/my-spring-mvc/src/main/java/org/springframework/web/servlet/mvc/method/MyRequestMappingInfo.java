package org.springframework.web.servlet.mvc.method;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public final class MyRequestMappingInfo implements MyRequestCondition<MyRequestMappingInfo>{

    @Nullable
    private final String name;

    private final MyPatternsRequestCondition patternsCondition;

    private final MyRequestMethodsRequestCondition methodsCondition;

    private final MyParamsRequestCondition paramsCondition;

    private final MyHeadersRequestCondition headersCondition;

    private final MyConsumesRequestCondition consumesCondition;

    private final MyProducesRequestCondition producesCondition;

    private final MyRequestConditionHolder customConditionHolder;






    public MyRequestMappingInfo(@Nullable String name, @Nullable MyPatternsRequestCondition patterns,
                              @Nullable MyRequestMethodsRequestCondition methods, @Nullable MyParamsRequestCondition params,
                              @Nullable MyHeadersRequestCondition headers, @Nullable MyConsumesRequestCondition consumes,
                              @Nullable MyProducesRequestCondition produces, @Nullable MyRequestCondition<?> custom) {

        this.name = (StringUtils.hasText(name) ? name : null);
        this.patternsCondition = (patterns != null ? patterns : new MyPatternsRequestCondition());
        this.methodsCondition = (methods != null ? methods : new MyRequestMethodsRequestCondition());
        this.paramsCondition = (params != null ? params : new MyParamsRequestCondition());
        this.headersCondition = (headers != null ? headers : new MyHeadersRequestCondition());
        this.consumesCondition = (consumes != null ? consumes : new MyConsumesRequestCondition());
        this.producesCondition = (produces != null ? produces : new MyProducesRequestCondition());
        this.customConditionHolder = new MyRequestConditionHolder(custom);
    }
    @Override
    public MyRequestMappingInfo combine(MyRequestMappingInfo other) {
        String name = combineNames(other);
        MyPatternsRequestCondition patterns = this.patternsCondition.combine(other.patternsCondition);
        MyRequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
        MyParamsRequestCondition params = this.paramsCondition.combine(other.paramsCondition);
        MyHeadersRequestCondition headers = this.headersCondition.combine(other.headersCondition);
        MyConsumesRequestCondition consumes = this.consumesCondition.combine(other.consumesCondition);
        MyProducesRequestCondition produces = this.producesCondition.combine(other.producesCondition);
        MyRequestConditionHolder custom = this.customConditionHolder.combine(other.customConditionHolder);

        return new MyRequestMappingInfo(name, patterns,
                methods, params, headers, consumes, produces, custom.getCondition());
    }

    @Nullable
    private String combineNames(MyRequestMappingInfo other) {
        if (this.name != null && other.name != null) {
            String separator = MyRequestMappingInfoHandlerMethodMappingNamingStrategy.SEPARATOR;
            return this.name + separator + other.name;
        }
        else if (this.name != null) {
            return this.name;
        }
        else {
            return other.name;
        }
    }

    @Nullable
    public String getName() {
        return this.name;
    }


    public interface Builder {

        /**
         * Set the path patterns.
         */
        Builder paths(String... paths);

        /**
         * Set the request method conditions.
         */
        Builder methods(RequestMethod... methods);

        /**
         * Set the request param conditions.
         */
        Builder params(String... params);

        /**
         * Set the header conditions.
         * <p>By default this is not set.
         */
        Builder headers(String... headers);

        /**
         * Set the consumes conditions.
         */
        Builder consumes(String... consumes);

        /**
         * Set the produces conditions.
         */
        Builder produces(String... produces);

        /**
         * Set the mapping name.
//         */
        Builder mappingName(String name);
//
//        /**
//         * Set a custom condition to use.
//         */
        Builder customCondition(MyRequestCondition<?> condition);

        /**
         * Provide additional configuration needed for request mapping purposes.
         */
        Builder options(BuilderConfiguration options);

        /**
         * Build the RequestMappingInfo.
         */
        MyRequestMappingInfo build();

    }

    public MyPatternsRequestCondition getPatternsCondition() {
        return this.patternsCondition;
    }

    public static Builder paths(String... paths) {
        return new DefaultBuilder(paths);
    }

    private static class DefaultBuilder implements Builder {

        private String[] params = new String[0];

        private String[] headers = new String[0];

        private String[] consumes = new String[0];

        private String[] produces = new String[0];

        private String[] paths = new String[0];

        private RequestMethod[] methods = new RequestMethod[0];

        @Nullable
        private String mappingName;

        @Nullable
        private MyRequestCondition<?> customCondition;

        private BuilderConfiguration options = new BuilderConfiguration();


        public DefaultBuilder(String... paths) {
            this.paths = paths;
        }

        @Override
        public Builder paths(String... paths) {
            this.paths = paths;
            return this;
        }

        @Override
        public DefaultBuilder methods(RequestMethod... methods) {
            this.methods = methods;
            return this;
        }

        @Override
        public DefaultBuilder params(String... params) {
            this.params = params;
            return this;
        }

        @Override
        public DefaultBuilder headers(String... headers) {
            this.headers = headers;
            return this;
        }

        @Override
        public DefaultBuilder consumes(String... consumes) {
            this.consumes = consumes;
            return this;
        }

        @Override
        public DefaultBuilder produces(String... produces) {
            this.produces = produces;
            return this;
        }

        @Override
        public DefaultBuilder mappingName(String name) {
            this.mappingName = name;
            return this;
        }

        @Override
        public DefaultBuilder customCondition(MyRequestCondition<?> condition) {
            this.customCondition = condition;
            return this;
        }

        @Override
        public Builder options(BuilderConfiguration options) {
            this.options = options;
            return this;
        }


        @Override
        public MyRequestMappingInfo build() {
            ContentNegotiationManager manager = this.options.getContentNegotiationManager();

            MyPatternsRequestCondition patternsCondition = new MyPatternsRequestCondition(
                    this.paths, this.options.getUrlPathHelper(), this.options.getPathMatcher(),
                    this.options.useSuffixPatternMatch(), this.options.useTrailingSlashMatch(),
                    this.options.getFileExtensions());

            return new MyRequestMappingInfo(this.mappingName, patternsCondition,
                    new MyRequestMethodsRequestCondition(this.methods),
                    new MyParamsRequestCondition(this.params),
                    new MyHeadersRequestCondition(this.headers),
                    new MyConsumesRequestCondition(this.consumes, this.headers),
                    new MyProducesRequestCondition(this.produces, this.headers, manager),
                    this.customCondition);
        }


    }

    public static class BuilderConfiguration {

        @Nullable
        private UrlPathHelper urlPathHelper;

        @Nullable
        private PathMatcher pathMatcher;

        private boolean trailingSlashMatch = true;

        private boolean suffixPatternMatch = true;

        private boolean registeredSuffixPatternMatch = false;

        @Nullable
        private ContentNegotiationManager contentNegotiationManager;

        /**
         * Set a custom UrlPathHelper to use for the PatternsRequestCondition.
         * <p>By default this is not set.
         * @since 4.2.8
         */
        public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
            this.urlPathHelper = urlPathHelper;
        }

        /**
         * Return a custom UrlPathHelper to use for the PatternsRequestCondition, if any.
         */
        @Nullable
        public UrlPathHelper getUrlPathHelper() {
            return this.urlPathHelper;
        }

        /**
         * Set a custom PathMatcher to use for the PatternsRequestCondition.
         * <p>By default this is not set.
         */
        public void setPathMatcher(@Nullable PathMatcher pathMatcher) {
            this.pathMatcher = pathMatcher;
        }

        /**
         * Return a custom PathMatcher to use for the PatternsRequestCondition, if any.
         */
        @Nullable
        public PathMatcher getPathMatcher() {
            return this.pathMatcher;
        }

        /**
         * Set whether to apply trailing slash matching in PatternsRequestCondition.
         * <p>By default this is set to 'true'.
         */
        public void setTrailingSlashMatch(boolean trailingSlashMatch) {
            this.trailingSlashMatch = trailingSlashMatch;
        }

        /**
         * Return whether to apply trailing slash matching in PatternsRequestCondition.
         */
        public boolean useTrailingSlashMatch() {
            return this.trailingSlashMatch;
        }

        /**
         * Set whether to apply suffix pattern matching in PatternsRequestCondition.
         * <p>By default this is set to 'true'.
         * @see #setRegisteredSuffixPatternMatch(boolean)
         */
        public void setSuffixPatternMatch(boolean suffixPatternMatch) {
            this.suffixPatternMatch = suffixPatternMatch;
        }

        /**
         * Return whether to apply suffix pattern matching in PatternsRequestCondition.
         */
        public boolean useSuffixPatternMatch() {
            return this.suffixPatternMatch;
        }

        /**
         * Set whether suffix pattern matching should be restricted to registered
         * file extensions only. Setting this property also sets
         * {@code suffixPatternMatch=true} and requires that a
         * {@link #setContentNegotiationManager} is also configured in order to
         * obtain the registered file extensions.
         */
        public void setRegisteredSuffixPatternMatch(boolean registeredSuffixPatternMatch) {
            this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
            this.suffixPatternMatch = (registeredSuffixPatternMatch || this.suffixPatternMatch);
        }

        /**
         * Return whether suffix pattern matching should be restricted to registered
         * file extensions only.
         */
        public boolean useRegisteredSuffixPatternMatch() {
            return this.registeredSuffixPatternMatch;
        }

        /**
         * Return the file extensions to use for suffix pattern matching. If
         * {@code registeredSuffixPatternMatch=true}, the extensions are obtained
         * from the configured {@code contentNegotiationManager}.
         */
        @Nullable
        public List<String> getFileExtensions() {
            if (useRegisteredSuffixPatternMatch() && this.contentNegotiationManager != null) {
                return this.contentNegotiationManager.getAllFileExtensions();
            }
            return null;
        }

        /**
         * Set the ContentNegotiationManager to use for the ProducesRequestCondition.
         * <p>By default this is not set.
         */
        public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
            this.contentNegotiationManager = contentNegotiationManager;
        }

        /**
         * Return the ContentNegotiationManager to use for the ProducesRequestCondition,
         * if any.
         */
        @Nullable
        public ContentNegotiationManager getContentNegotiationManager() {
            return this.contentNegotiationManager;
        }
    }

    @Override
    @Nullable
    public MyRequestMappingInfo getMatchingCondition(HttpServletRequest request) {
        MyRequestMethodsRequestCondition methods = this.methodsCondition.getMatchingCondition(request);
        MyParamsRequestCondition params = this.paramsCondition.getMatchingCondition(request);
        MyHeadersRequestCondition headers = this.headersCondition.getMatchingCondition(request);
        MyConsumesRequestCondition consumes = this.consumesCondition.getMatchingCondition(request);
        MyProducesRequestCondition produces = this.producesCondition.getMatchingCondition(request);

        if (methods == null || params == null || headers == null || consumes == null || produces == null) {
            return null;
        }

        MyPatternsRequestCondition patterns = this.patternsCondition.getMatchingCondition(request);
        if (patterns == null) {
            return null;
        }

        MyRequestConditionHolder custom = this.customConditionHolder.getMatchingCondition(request);
        if (custom == null) {
            return null;
        }

        return new MyRequestMappingInfo(this.name, patterns,
                methods, params, headers, consumes, produces, custom.getCondition());
    }

    @Override
    public int compareTo(MyRequestMappingInfo other, HttpServletRequest request) {
        int result;
        // Automatic vs explicit HTTP HEAD mapping
        if (HttpMethod.HEAD.matches(request.getMethod())) {
            result = this.methodsCondition.compareTo(other.getMethodsCondition(), request);
            if (result != 0) {
                return result;
            }
        }
        result = this.patternsCondition.compareTo(other.getPatternsCondition(), request);
        if (result != 0) {
            return result;
        }
        result = this.paramsCondition.compareTo(other.getParamsCondition(), request);
        if (result != 0) {
            return result;
        }
        result = this.headersCondition.compareTo(other.getHeadersCondition(), request);
        if (result != 0) {
            return result;
        }
        result = this.consumesCondition.compareTo(other.getConsumesCondition(), request);
        if (result != 0) {
            return result;
        }
        result = this.producesCondition.compareTo(other.getProducesCondition(), request);
        if (result != 0) {
            return result;
        }
        // Implicit (no method) vs explicit HTTP method mappings
        result = this.methodsCondition.compareTo(other.getMethodsCondition(), request);
        if (result != 0) {
            return result;
        }
        result = this.customConditionHolder.compareTo(other.customConditionHolder, request);
        if (result != 0) {
            return result;
        }
        return 0;
    }

    public MyRequestMethodsRequestCondition getMethodsCondition() {
        return this.methodsCondition;
    }

    public MyParamsRequestCondition getParamsCondition() {
        return this.paramsCondition;
    }

    public MyHeadersRequestCondition getHeadersCondition() {
        return this.headersCondition;
    }


    public MyConsumesRequestCondition getConsumesCondition() {
        return this.consumesCondition;
    }

    public MyProducesRequestCondition getProducesCondition() {
        return this.producesCondition;
    }
}
