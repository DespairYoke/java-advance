package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public interface MyFlashMapManager {

    @Nullable
    MyFlashMap retrieveAndUpdate(HttpServletRequest request, HttpServletResponse response);

    void saveOutputFlashMap(MyFlashMap flashMap, HttpServletRequest request, HttpServletResponse response);

}
