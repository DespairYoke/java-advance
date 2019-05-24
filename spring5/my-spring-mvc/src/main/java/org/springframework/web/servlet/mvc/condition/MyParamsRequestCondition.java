package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.WebUtils;

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
public class MyParamsRequestCondition extends MyAbstractRequestCondition<MyParamsRequestCondition>{

    private final Set<ParamExpression> expressions;

    public MyParamsRequestCondition(String... params) {
        this(parseExpressions(params));
    }

    private MyParamsRequestCondition(Collection<ParamExpression> conditions) {
        this.expressions = Collections.unmodifiableSet(new LinkedHashSet<>(conditions));
    }

    private static Collection<ParamExpression> parseExpressions(String... params) {
        Set<ParamExpression> expressions = new LinkedHashSet<>();
        for (String param : params) {
            expressions.add(new ParamExpression(param));
        }
        return expressions;
    }


    @Override
    public MyParamsRequestCondition combine(MyParamsRequestCondition other) {
        Set<ParamExpression> set = new LinkedHashSet<>(this.expressions);
        set.addAll(other.expressions);
        return new MyParamsRequestCondition(set);
    }

    @Override
    @Nullable
    public MyParamsRequestCondition getMatchingCondition(HttpServletRequest request) {
        for (ParamExpression expression : expressions) {
            if (!expression.match(request)) {
                return null;
            }
        }
        return this;
    }

    @Override
    protected Collection<ParamExpression> getContent() {
        return this.expressions;
    }

    @Override
    public int compareTo(MyParamsRequestCondition other, HttpServletRequest request) {
        return 0;
    }

    static class ParamExpression extends MyAbstractNameValueExpression<String> {

        ParamExpression(String expression) {
            super(expression);
        }

        @Override
        protected boolean isCaseSensitiveName() {
            return true;
        }

        @Override
        protected String parseValue(String valueExpression) {
            return valueExpression;
        }

        @Override
        protected boolean matchName(HttpServletRequest request) {
            return (WebUtils.hasSubmitParameter(request, this.name) ||
                    request.getParameterMap().containsKey(this.name));
        }

        @Override
        protected boolean matchValue(HttpServletRequest request) {
            return ObjectUtils.nullSafeEquals(this.value, request.getParameter(this.name));
        }
    }
}
