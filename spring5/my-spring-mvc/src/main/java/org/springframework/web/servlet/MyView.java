package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface MyView {

    String RESPONSE_STATUS_ATTRIBUTE = MyView.class.getName() + ".responseStatus";

    String PATH_VARIABLES = MyView.class.getName() + ".pathVariables";


    //渲染页面
    void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception;

    //获取网页文件的类型和编码
    @Nullable
    default String getContentType() {
        return null;
    }

}
