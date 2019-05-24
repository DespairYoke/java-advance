package org.springframework.web.servlet.mvc.condition;

import org.springframework.http.MediaType;

public interface MyMediaTypeExpression {

    MediaType getMediaType();

    boolean isNegated();
}
