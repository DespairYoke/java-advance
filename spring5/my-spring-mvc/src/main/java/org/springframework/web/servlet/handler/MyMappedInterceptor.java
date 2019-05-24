package org.springframework.web.servlet.handler;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.MyHandlerInterceptor;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-28
 **/
public final class MyMappedInterceptor implements MyHandlerInterceptor {


    @Nullable
    private final String[] includePatterns;

    @Nullable
    private final String[] excludePatterns;

    private final MyHandlerInterceptor interceptor;
    @Nullable
    private PathMatcher pathMatcher;


    public MyMappedInterceptor(@Nullable String[] includePatterns, MyHandlerInterceptor interceptor) {
        this(includePatterns, null, interceptor);
    }

    public MyMappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns,
                             WebRequestInterceptor interceptor) {

        this(includePatterns, excludePatterns, new MyWebRequestHandlerInterceptorAdapter(interceptor));
    }


    public MyMappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns,
                             MyHandlerInterceptor interceptor) {

        this.includePatterns = includePatterns;
        this.excludePatterns = excludePatterns;
        this.interceptor = interceptor;
    }


    public MyMappedInterceptor(@Nullable String[] includePatterns, WebRequestInterceptor interceptor) {
        this(includePatterns, null, interceptor);
    }


    public boolean matches(String lookupPath, PathMatcher pathMatcher) {
        PathMatcher pathMatcherToUse = (this.pathMatcher != null ? this.pathMatcher : pathMatcher);
        if (!ObjectUtils.isEmpty(this.excludePatterns)) {
            for (String pattern : this.excludePatterns) {
                if (pathMatcherToUse.match(pattern, lookupPath)) {
                    return false;
                }
            }
        }
        if (ObjectUtils.isEmpty(this.includePatterns)) {
            return true;
        }
        for (String pattern : this.includePatterns) {
            if (pathMatcherToUse.match(pattern, lookupPath)) {
                return true;
            }
        }
        return false;
    }

    public MyHandlerInterceptor getInterceptor() {
        return this.interceptor;
    }
}
