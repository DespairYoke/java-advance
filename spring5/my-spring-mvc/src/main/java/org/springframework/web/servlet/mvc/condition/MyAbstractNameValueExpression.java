package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-25
 **/
abstract class MyAbstractNameValueExpression<T> implements MyNameValueExpression<T>{

    protected final String name;

    @Nullable
    protected final T value;

    protected final boolean isNegated;


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @Nullable
    public T getValue() {
        return this.value;
    }

    @Override
    public boolean isNegated() {
        return this.isNegated;
    }

    MyAbstractNameValueExpression(String expression) {
        int separator = expression.indexOf('=');
        if (separator == -1) {
            this.isNegated = expression.startsWith("!");
            this.name = (this.isNegated ? expression.substring(1) : expression);
            this.value = null;
        }
        else {
            this.isNegated = (separator > 0) && (expression.charAt(separator - 1) == '!');
            this.name = (this.isNegated ? expression.substring(0, separator - 1) : expression.substring(0, separator));
            this.value = parseValue(expression.substring(separator + 1));
        }
    }

    public final boolean match(HttpServletRequest request) {
        boolean isMatch;
        if (this.value != null) {
            isMatch = matchValue(request);
        }
        else {
            isMatch = matchName(request);
        }
        return (this.isNegated ? !isMatch : isMatch);
    }

    protected abstract boolean isCaseSensitiveName();

    protected abstract T parseValue(String valueExpression);

    protected abstract boolean matchName(HttpServletRequest request);

    protected abstract boolean matchValue(HttpServletRequest request);
}
