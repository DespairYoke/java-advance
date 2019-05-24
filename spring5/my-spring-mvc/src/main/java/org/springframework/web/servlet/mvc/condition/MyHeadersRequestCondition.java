package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public class MyHeadersRequestCondition extends MyAbstractRequestCondition<MyHeadersRequestCondition>{

    private static final MyHeadersRequestCondition PRE_FLIGHT_MATCH = new MyHeadersRequestCondition();

    private final Set<HeaderExpression> expressions;

    public MyHeadersRequestCondition(String... headers) {
        this(parseExpressions(headers));
    }

    private MyHeadersRequestCondition(Collection<HeaderExpression> conditions) {
        this.expressions = Collections.unmodifiableSet(new LinkedHashSet<>(conditions));
    }
    @Override
    public MyHeadersRequestCondition combine(MyHeadersRequestCondition other) {
        Set<HeaderExpression> set = new LinkedHashSet<>(this.expressions);
        set.addAll(other.expressions);
        return new MyHeadersRequestCondition(set);
    }

    @Override
    @Nullable
    public MyHeadersRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return PRE_FLIGHT_MATCH;
        }
        for (HeaderExpression expression : expressions) {
            if (!expression.match(request)) {
                return null;
            }
        }
        return this;
    }

    @Override
    protected Collection<HeaderExpression> getContent() {
        return this.expressions;
    }

    @Override
    public int compareTo(MyHeadersRequestCondition other, HttpServletRequest request) {
        return 0;
    }

    private static Collection<HeaderExpression> parseExpressions(String... headers) {
        Set<HeaderExpression> expressions = new LinkedHashSet<>();
        for (String header : headers) {
            HeaderExpression expr = new HeaderExpression(header);
            if ("Accept".equalsIgnoreCase(expr.name) || "Content-Type".equalsIgnoreCase(expr.name)) {
                continue;
            }
            expressions.add(expr);
        }
        return expressions;
    }

    static class HeaderExpression extends MyAbstractNameValueExpression<String> {

        public HeaderExpression(String expression) {
            super(expression);
        }

        @Override
        protected boolean isCaseSensitiveName() {
            return false;
        }

        @Override
        protected String parseValue(String valueExpression) {
            return valueExpression;
        }

        @Override
        protected boolean matchName(HttpServletRequest request) {
            return (request.getHeader(this.name) != null);
        }

        @Override
        protected boolean matchValue(HttpServletRequest request) {
            return ObjectUtils.nullSafeEquals(this.value, request.getHeader(this.name));
        }
    }
}
