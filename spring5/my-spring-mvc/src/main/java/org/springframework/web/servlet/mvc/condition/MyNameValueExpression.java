package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-25
 **/
public interface MyNameValueExpression<T> {

    String getName();

    @Nullable
    T getValue();

    boolean isNegated();
}
