package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public abstract class MyAbstractRequestCondition<T extends MyAbstractRequestCondition<T>> implements MyRequestCondition<T>{

    public boolean isEmpty() {
        return getContent().isEmpty();
    }

    protected abstract Collection<?> getContent();

}
