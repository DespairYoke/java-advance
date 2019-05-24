package org.springframework.web.servlet;

import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public final class MyFlashMap extends HashMap<String, Object> implements Comparable<MyFlashMap>{

    @Nullable
    private String targetRequestPath;

    private final MultiValueMap<String, String> targetRequestParams = new LinkedMultiValueMap<>(4);
    @Override
    public int compareTo(MyFlashMap myFlashMap) {
        return 0;
    }

    public void setTargetRequestPath(@Nullable String path) {
        this.targetRequestPath = path;
    }

    public MyFlashMap addTargetRequestParams(@Nullable MultiValueMap<String, String> params) {
        if (params != null) {
            params.forEach((key, values) -> {
                for (String value : values) {
                    addTargetRequestParam(key, value);
                }
            });
        }
        return this;
    }

    public MyFlashMap addTargetRequestParam(String name, String value) {
        if (StringUtils.hasText(name) && StringUtils.hasText(value)) {
            this.targetRequestParams.add(name, value);
        }
        return this;
    }
}
