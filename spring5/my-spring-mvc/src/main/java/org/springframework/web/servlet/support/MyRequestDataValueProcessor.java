package org.springframework.web.servlet.support;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-13
 **/
public interface MyRequestDataValueProcessor {

    String processUrl(HttpServletRequest request, String url);
}
