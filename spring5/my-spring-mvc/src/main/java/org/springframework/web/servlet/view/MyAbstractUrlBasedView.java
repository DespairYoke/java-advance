package org.springframework.web.servlet.view;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-11
 **/
public abstract class MyAbstractUrlBasedView extends MyAbstractView implements InitializingBean {


    protected MyAbstractUrlBasedView() {
    }

    @Nullable
    private String url;

    private final Map<String, Object> staticAttributes = new LinkedHashMap<>();

    protected MyAbstractUrlBasedView(String url) {
        this.url = url;
    }

    public void addStaticAttribute(String name, Object value) {
        this.staticAttributes.put(name, value);
    }

    public void setUrl(@Nullable String url) {
        this.url = url;
    }
    @Nullable
    public String getUrl() {
        return this.url;
    }

    public void setAttributesMap(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            attributes.forEach(this::addStaticAttribute);
        }
    }

    public boolean checkResource(Locale locale) throws Exception {
        return true;
    }





}
