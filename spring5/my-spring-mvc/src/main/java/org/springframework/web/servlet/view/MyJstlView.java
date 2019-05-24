package org.springframework.web.servlet.view;

import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.MyJstlUtils;
import org.springframework.web.servlet.support.MyRequestContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-13
 **/
public class MyJstlView extends MyInternalResourceView{

    @Nullable
    private MessageSource messageSource;



    public MyJstlView() {
    }


    public MyJstlView(String url) {
        super(url);
    }


    public MyJstlView(String url, MessageSource messageSource) {
        this(url);
        this.messageSource = messageSource;
    }


    @Override
    protected void initServletContext(ServletContext servletContext) {
        if (this.messageSource != null) {
            this.messageSource = MyJstlUtils.getJstlAwareMessageSource(servletContext, this.messageSource);
        }
        super.initServletContext(servletContext);
    }

    @Override
    protected void exposeHelpers(HttpServletRequest request) throws Exception {
        if (this.messageSource != null) {
            MyJstlUtils.exposeLocalizationContext(request, this.messageSource);
        }
        else {
            MyJstlUtils.exposeLocalizationContext(new MyRequestContext(request, getServletContext()));
        }
    }


}
