package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public final class MyPatternsRequestCondition extends MyAbstractRequestCondition<MyPatternsRequestCondition>{

    private final Set<String> patterns;

    private final UrlPathHelper pathHelper;

    private final PathMatcher pathMatcher;

    private final boolean useSuffixPatternMatch;

    private final boolean useTrailingSlashMatch;

    private final List<String> fileExtensions = new ArrayList<>();

    public MyPatternsRequestCondition(String... patterns) {
        this(Arrays.asList(patterns), null, null, true, true, null);
    }

    public MyPatternsRequestCondition(String[] patterns, @Nullable UrlPathHelper urlPathHelper,
                                    @Nullable PathMatcher pathMatcher, boolean useSuffixPatternMatch,
                                    boolean useTrailingSlashMatch, @Nullable List<String> fileExtensions) {

        this(Arrays.asList(patterns), urlPathHelper, pathMatcher, useSuffixPatternMatch,
                useTrailingSlashMatch, fileExtensions);
    }

    private MyPatternsRequestCondition(Collection<String> patterns, @Nullable UrlPathHelper urlPathHelper,
                                     @Nullable PathMatcher pathMatcher, boolean useSuffixPatternMatch,
                                     boolean useTrailingSlashMatch, @Nullable List<String> fileExtensions) {

        this.patterns = Collections.unmodifiableSet(prependLeadingSlash(patterns));
        this.pathHelper = (urlPathHelper != null ? urlPathHelper : new UrlPathHelper());
        this.pathMatcher = (pathMatcher != null ? pathMatcher : new AntPathMatcher());
        this.useSuffixPatternMatch = useSuffixPatternMatch;
        this.useTrailingSlashMatch = useTrailingSlashMatch;

        if (fileExtensions != null) {
            for (String fileExtension : fileExtensions) {
                if (fileExtension.charAt(0) != '.') {
                    fileExtension = "." + fileExtension;
                }
                this.fileExtensions.add(fileExtension);
            }
        }
    }


    private static Set<String> prependLeadingSlash(Collection<String> patterns) {
        Set<String> result = new LinkedHashSet<>(patterns.size());
        for (String pattern : patterns) {
            if (StringUtils.hasLength(pattern) && !pattern.startsWith("/")) {
                pattern = "/" + pattern;
            }
            result.add(pattern);
        }
        return result;
    }
    @Override
    protected Collection<String> getContent() {
        return this.patterns;
    }


    @Override
    public MyPatternsRequestCondition combine(MyPatternsRequestCondition other) {
        Set<String> result = new LinkedHashSet<>();
        if (!this.patterns.isEmpty() && !other.patterns.isEmpty()) {
            for (String pattern1 : this.patterns) {
                for (String pattern2 : other.patterns) {
                    result.add(this.pathMatcher.combine(pattern1, pattern2));
                }
            }
        }
        else if (!this.patterns.isEmpty()) {
            result.addAll(this.patterns);
        }
        else if (!other.patterns.isEmpty()) {
            result.addAll(other.patterns);
        }
        else {
            result.add("");
        }
        return new MyPatternsRequestCondition(result, this.pathHelper, this.pathMatcher, this.useSuffixPatternMatch,
                this.useTrailingSlashMatch, this.fileExtensions);
    }

    @Override
    @Nullable
    public MyPatternsRequestCondition getMatchingCondition(HttpServletRequest request) {

        if (this.patterns.isEmpty()) {
            return this;
        }

        String lookupPath = this.pathHelper.getLookupPathForRequest(request);
        List<String> matches = getMatchingPatterns(lookupPath);

        return matches.isEmpty() ? null :
                new MyPatternsRequestCondition(matches, this.pathHelper, this.pathMatcher, this.useSuffixPatternMatch,
                        this.useTrailingSlashMatch, this.fileExtensions);
    }

    public List<String> getMatchingPatterns(String lookupPath) {
        List<String> matches = new ArrayList<>();
        for (String pattern : this.patterns) {
            String match = getMatchingPattern(pattern, lookupPath);
            if (match != null) {
                matches.add(match);
            }
        }
        if (matches.size() > 1) {
            matches.sort(this.pathMatcher.getPatternComparator(lookupPath));
        }
        return matches;
    }

    @Nullable
    private String getMatchingPattern(String pattern, String lookupPath) {
        if (pattern.equals(lookupPath)) {
            return pattern;
        }
        if (this.useSuffixPatternMatch) {
            if (!this.fileExtensions.isEmpty() && lookupPath.indexOf('.') != -1) {
                for (String extension : this.fileExtensions) {
                    if (this.pathMatcher.match(pattern + extension, lookupPath)) {
                        return pattern + extension;
                    }
                }
            }
            else {
                boolean hasSuffix = pattern.indexOf('.') != -1;
                if (!hasSuffix && this.pathMatcher.match(pattern + ".*", lookupPath)) {
                    return pattern + ".*";
                }
            }
        }
        if (this.pathMatcher.match(pattern, lookupPath)) {
            return pattern;
        }
        if (this.useTrailingSlashMatch) {
            if (!pattern.endsWith("/") && this.pathMatcher.match(pattern + "/", lookupPath)) {
                return pattern +"/";
            }
        }
        return null;
    }

    @Override
    public int compareTo(MyPatternsRequestCondition other, HttpServletRequest request) {
        return 0;
    }

    public Set<String> getPatterns() {
        return this.patterns;
    }
}
