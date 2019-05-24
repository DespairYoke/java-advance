package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public class MyRequestConditionHolder  extends MyAbstractRequestCondition<MyRequestConditionHolder>{


    public MyRequestConditionHolder(@Nullable MyRequestCondition<?> requestCondition) {
        this.condition = (MyRequestCondition<Object>) requestCondition;
    }

    @Nullable
    private final MyRequestCondition<Object> condition;


    @Override
    protected Collection<?> getContent() {
        return (this.condition != null ? Collections.singleton(this.condition) : Collections.emptyList());
    }

    @Override
    public MyRequestConditionHolder combine(MyRequestConditionHolder other) {
        if (this.condition == null && other.condition == null) {
            return this;
        }
        else if (this.condition == null) {
            return other;
        }
        else if (other.condition == null) {
            return this;
        }
        else {
            assertEqualConditionTypes(this.condition, other.condition);
            MyRequestCondition<?> combined = (MyRequestCondition<?>) this.condition.combine(other.condition);
            return new MyRequestConditionHolder(combined);
        }
    }

    @Override
    @Nullable
    public MyRequestConditionHolder getMatchingCondition(HttpServletRequest request) {
        if (this.condition == null) {
            return this;
        }
        MyRequestCondition<?> match = (MyRequestCondition<?>) this.condition.getMatchingCondition(request);
        return (match != null ? new MyRequestConditionHolder(match) : null);
    }

    @Override
    public int compareTo(MyRequestConditionHolder other, HttpServletRequest request) {
        return 0;
    }

    @Nullable
    public MyRequestCondition<?> getCondition() {
        return this.condition;
    }

    private void assertEqualConditionTypes(MyRequestCondition<?> thisCondition, MyRequestCondition<?> otherCondition) {
        Class<?> clazz = thisCondition.getClass();
        Class<?> otherClazz = otherCondition.getClass();
        if (!clazz.equals(otherClazz)) {
            throw new ClassCastException("Incompatible request conditions: " + clazz + " and " + otherClazz);
        }
    }

}
