package org.springframework.web.servlet.i18n;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.MyLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-11
 **/
public class MyAcceptHeaderLocaleResolver implements MyLocaleResolver {
    private final List<Locale> supportedLocales = new ArrayList<>(4);

    @Nullable
    private Locale defaultLocale;


    /**
     * Configure supported locales to check against the requested locales
     * determined via {@link HttpServletRequest#getLocales()}. If this is not
     * configured then {@link HttpServletRequest#getLocale()} is used instead.
     * @param locales the supported locales
     * @since 4.3
     */
    public void setSupportedLocales(List<Locale> locales) {
        this.supportedLocales.clear();
        this.supportedLocales.addAll(locales);
    }

    /**
     * Return the configured list of supported locales.
     * @since 4.3
     */
    public List<Locale> getSupportedLocales() {
        return this.supportedLocales;
    }

    /**
     * Configure a fixed default locale to fall back on if the request does not
     * have an "Accept-Language" header.
     * <p>By default this is not set in which case when there is "Accept-Language"
     * header, the default locale for the server is used as defined in
     * {@link HttpServletRequest#getLocale()}.
     * @param defaultLocale the default locale to use
     * @since 4.3
     */
    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * The configured default locale, if any.
     * @since 4.3
     */
    @Nullable
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }


    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale != null && request.getHeader("Accept-Language") == null) {
            return defaultLocale;
        }
        Locale requestLocale = request.getLocale();
        List<Locale> supportedLocales = getSupportedLocales();
        if (supportedLocales.isEmpty() || supportedLocales.contains(requestLocale)) {
            return requestLocale;
        }
        Locale supportedLocale = findSupportedLocale(request, supportedLocales);
        if (supportedLocale != null) {
            return supportedLocale;
        }
        return (defaultLocale != null ? defaultLocale : requestLocale);
    }

    @Nullable
    private Locale findSupportedLocale(HttpServletRequest request, List<Locale> supportedLocales) {
        Enumeration<Locale> requestLocales = request.getLocales();
        Locale languageMatch = null;
        while (requestLocales.hasMoreElements()) {
            Locale locale = requestLocales.nextElement();
            if (supportedLocales.contains(locale)) {
                if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage())) {
                    // Full match: language + country, possibly narrowed from earlier language-only match
                    return locale;
                }
            }
            else if (languageMatch == null) {
                // Let's try to find a language-only match as a fallback
                for (Locale candidate : supportedLocales) {
                    if (!StringUtils.hasLength(candidate.getCountry()) &&
                            candidate.getLanguage().equals(locale.getLanguage())) {
                        languageMatch = candidate;
                        break;
                    }
                }
            }
        }
        return languageMatch;
    }

    @Override
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        throw new UnsupportedOperationException(
                "Cannot change HTTP accept header - use a different locale resolution strategy");
    }
}
