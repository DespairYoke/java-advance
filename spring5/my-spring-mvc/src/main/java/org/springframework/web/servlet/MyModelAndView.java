package org.springframework.web.servlet;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;

import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-28
 **/
public class MyModelAndView {

    @Nullable
    private Object view;

    /** Model Map */
    @Nullable
    private ModelMap model;

    /** Optional HTTP status for the response */
    @Nullable
    private HttpStatus status;

    public MyModelAndView(@Nullable String viewName, @Nullable Map<String, ?> model, @Nullable HttpStatus status) {
        this.view = viewName;
        if (model != null) {
            getModelMap().addAllAttributes(model);
        }
        this.status = status;
    }

    @Nullable
    public HttpStatus getStatus() {
        return this.status;
    }

    public boolean isReference() {
        return (this.view instanceof String);
    }

    public Map<String, Object> getModel() {
        return getModelMap();
    }

    @Nullable
    public MyView getView() {
        return (this.view instanceof MyView ? (MyView) this.view : null);
    }

    public ModelMap getModelMap() {
        if (this.model == null) {
            this.model = new ModelMap();
        }
        return this.model;
    }

    public void setView(@Nullable MyView view) {
        this.view = view;
    }

    @Nullable
    public String getViewName() {
        return (this.view instanceof String ? (String) this.view : null);
    }
    @Nullable
    protected Map<String, Object> getModelInternal() {
        return this.model;
    }

    public void setViewName(@Nullable String viewName) {
        this.view = viewName;
    }

    public MyModelAndView addObject(String attributeName, Object attributeValue) {
        getModelMap().addAttribute(attributeName, attributeValue);
        return this;
    }

}
