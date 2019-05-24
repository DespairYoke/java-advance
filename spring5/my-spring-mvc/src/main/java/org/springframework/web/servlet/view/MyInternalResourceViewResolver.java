package org.springframework.web.servlet.view;

import org.springframework.util.ClassUtils;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public class MyInternalResourceViewResolver extends MyUrlBasedViewResolver{

    private static final boolean jstlPresent = ClassUtils.isPresent(
            "javax.servlet.jsp.jstl.core.Config", MyInternalResourceViewResolver.class.getClassLoader());

    public MyInternalResourceViewResolver() {
        Class<?> viewClass = requiredViewClass();
        if (MyInternalResourceView.class == viewClass && jstlPresent) {
            viewClass = MyJstlView.class;
        }
        setViewClass(viewClass);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return MyInternalResourceView.class;
    }


}
