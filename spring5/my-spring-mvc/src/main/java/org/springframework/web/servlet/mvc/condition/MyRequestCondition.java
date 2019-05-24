package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public interface MyRequestCondition<T> {

    T combine(T other);

    @Nullable
    T getMatchingCondition(HttpServletRequest request);

    int compareTo(T other, HttpServletRequest request);
}
