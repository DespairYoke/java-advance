package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

public interface MyHandlerMapping {

    String URI_TEMPLATE_VARIABLES_ATTRIBUTE = MyHandlerMapping.class.getName() + ".uriTemplateVariables";


    @Nullable
    MyHandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
