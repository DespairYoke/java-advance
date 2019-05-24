package org.springframework.web.servlet.view;

import org.springframework.lang.Nullable;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.MyView;
import org.springframework.web.servlet.MyViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-11
 **/
public abstract class MyAbstractCachingViewResolver extends WebApplicationObjectSupport implements MyViewResolver {


    public static final int DEFAULT_CACHE_LIMIT = 1024;

    private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

    private boolean cacheUnresolved = true;


    private final Map<Object, MyView> viewAccessCache = new ConcurrentHashMap<>(DEFAULT_CACHE_LIMIT);

    public boolean isCache() {
        return (this.cacheLimit > 0);
    }

    public int getCacheLimit() {
        return this.cacheLimit;
    }

    private static final MyView UNRESOLVED_VIEW = new MyView() {
        @Override
        @Nullable
        public String getContentType() {
            return null;
        }
        @Override
        public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        }
    };

    private final Map<Object, MyView> viewCreationCache =
            new LinkedHashMap<Object, MyView>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Object, MyView> eldest) {
                    if (size() > getCacheLimit()) {
                        viewAccessCache.remove(eldest.getKey());
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            };

    @Override
    @Nullable
    public MyView resolveViewName(String viewName, Locale locale) throws Exception {
        if (!isCache()) {
            return createView(viewName, locale);
        }
        else {
            Object cacheKey = getCacheKey(viewName, locale);
            MyView view = this.viewAccessCache.get(cacheKey);
            if (view == null) {
                synchronized (this.viewCreationCache) {
                    view = this.viewCreationCache.get(cacheKey);
                    if (view == null) {
                        // Ask the subclass to create the View object.
                        view = createView(viewName, locale);
                        if (view == null && this.cacheUnresolved) {
                            view = UNRESOLVED_VIEW;
                        }
                        if (view != null) {
                            this.viewAccessCache.put(cacheKey, view);
                            this.viewCreationCache.put(cacheKey, view);
                            if (logger.isTraceEnabled()) {
                                logger.trace("Cached view [" + cacheKey + "]");
                            }
                        }
                    }
                }
            }
            return (view != UNRESOLVED_VIEW ? view : null);
        }
    }

    @Nullable
    protected MyView createView(String viewName, Locale locale) throws Exception {
        return loadView(viewName, locale);
    }

    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName + '_' + locale;
    }

    @Nullable
    protected abstract MyView loadView(String viewName, Locale locale) throws Exception;

}
