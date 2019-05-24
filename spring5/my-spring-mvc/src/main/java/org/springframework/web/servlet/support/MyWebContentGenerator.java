package org.springframework.web.servlet.support;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-28
 **/
public class MyWebContentGenerator {


    /** HTTP method "GET" */
    public static final String METHOD_GET = "GET";

    /** HTTP method "HEAD" */
    public static final String METHOD_HEAD = "HEAD";

    /** HTTP method "POST" */
    public static final String METHOD_POST = "POST";

    private static final String HEADER_PRAGMA = "Pragma";

    private static final String HEADER_EXPIRES = "Expires";

    protected static final String HEADER_CACHE_CONTROL = "Cache-Control";

    /** Use HTTP 1.0 expires header? */
    private boolean useExpiresHeader = false;

    /** Use HTTP 1.1 cache-control header? */
    private boolean useCacheControlHeader = true;

    /** Use HTTP 1.1 cache-control header value "no-store"? */
    private boolean useCacheControlNoStore = true;

    @Nullable
    private CacheControl cacheControl;

    private int cacheSeconds = -1;

    @Nullable
    private String[] varyByRequestHeaders;

    private boolean alwaysMustRevalidate = false;
    protected final void applyCacheSeconds(HttpServletResponse response, int cacheSeconds) {
        if (this.useExpiresHeader || !this.useCacheControlHeader) {
            // Deprecated HTTP 1.0 cache behavior, as in previous Spring versions
            if (cacheSeconds > 0) {
                cacheForSeconds(response, cacheSeconds);
            }
            else if (cacheSeconds == 0) {
                preventCaching(response);
            }
        }
        else {
            CacheControl cControl;
            if (cacheSeconds > 0) {
                cControl = CacheControl.maxAge(cacheSeconds, TimeUnit.SECONDS);
                if (this.alwaysMustRevalidate) {
                    cControl = cControl.mustRevalidate();
                }
            }
            else if (cacheSeconds == 0) {
                cControl = (this.useCacheControlNoStore ? CacheControl.noStore() : CacheControl.noCache());
            }
            else {
                cControl = CacheControl.empty();
            }
            applyCacheControl(response, cControl);
        }
    }
    @Deprecated
    protected final void cacheForSeconds(HttpServletResponse response, int seconds) {
        cacheForSeconds(response, seconds, false);
    }

    @Deprecated
    protected final void cacheForSeconds(HttpServletResponse response, int seconds, boolean mustRevalidate) {
        if (this.useExpiresHeader) {
            // HTTP 1.0 header
            response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + seconds * 1000L);
        }
        else if (response.containsHeader(HEADER_EXPIRES)) {
            // Reset HTTP 1.0 Expires header if present
            response.setHeader(HEADER_EXPIRES, "");
        }

        if (this.useCacheControlHeader) {
            // HTTP 1.1 header
            String headerValue = "max-age=" + seconds;
            if (mustRevalidate || this.alwaysMustRevalidate) {
                headerValue += ", must-revalidate";
            }
            response.setHeader(HEADER_CACHE_CONTROL, headerValue);
        }

        if (response.containsHeader(HEADER_PRAGMA)) {
            // Reset HTTP 1.0 Pragma header if present
            response.setHeader(HEADER_PRAGMA, "");
        }
    }

    @Deprecated
    protected final void preventCaching(HttpServletResponse response) {
        response.setHeader(HEADER_PRAGMA, "no-cache");

        if (this.useExpiresHeader) {
            // HTTP 1.0 Expires header
            response.setDateHeader(HEADER_EXPIRES, 1L);
        }

        if (this.useCacheControlHeader) {
            // HTTP 1.1 Cache-Control header: "no-cache" is the standard value,
            // "no-store" is necessary to prevent caching on Firefox.
            response.setHeader(HEADER_CACHE_CONTROL, "no-cache");
            if (this.useCacheControlNoStore) {
                response.addHeader(HEADER_CACHE_CONTROL, "no-store");
            }
        }
    }

    protected final void applyCacheControl(HttpServletResponse response, CacheControl cacheControl) {
        String ccValue = cacheControl.getHeaderValue();
        if (ccValue != null) {
            // Set computed HTTP 1.1 Cache-Control header
            response.setHeader(HEADER_CACHE_CONTROL, ccValue);

            if (response.containsHeader(HEADER_PRAGMA)) {
                // Reset HTTP 1.0 Pragma header if present
                response.setHeader(HEADER_PRAGMA, "");
            }
            if (response.containsHeader(HEADER_EXPIRES)) {
                // Reset HTTP 1.0 Expires header if present
                response.setHeader(HEADER_EXPIRES, "");
            }
        }
    }

    protected final void prepareResponse(HttpServletResponse response) {
        if (this.cacheControl != null) {
            applyCacheControl(response, this.cacheControl);
        }
        else {
            applyCacheSeconds(response, this.cacheSeconds);
        }
        if (this.varyByRequestHeaders != null) {
            for (String value : getVaryRequestHeadersToAdd(response, this.varyByRequestHeaders)) {
                response.addHeader("Vary", value);
            }
        }
    }

    private Collection<String> getVaryRequestHeadersToAdd(HttpServletResponse response, String[] varyByRequestHeaders) {
        if (!response.containsHeader(HttpHeaders.VARY)) {
            return Arrays.asList(varyByRequestHeaders);
        }
        Collection<String> result = new ArrayList<>(varyByRequestHeaders.length);
        Collections.addAll(result, varyByRequestHeaders);
        for (String header : response.getHeaders(HttpHeaders.VARY)) {
            for (String existing : StringUtils.tokenizeToStringArray(header, ",")) {
                if ("*".equals(existing)) {
                    return Collections.emptyList();
                }
                for (String value : varyByRequestHeaders) {
                    if (value.equalsIgnoreCase(existing)) {
                        result.remove(value);
                    }
                }
            }
        }
        return result;
    }
}
