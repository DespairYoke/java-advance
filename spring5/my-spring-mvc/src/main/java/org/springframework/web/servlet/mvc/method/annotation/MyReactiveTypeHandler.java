package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.ResolvableType;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-10
 **/
class MyReactiveTypeHandler {

    static class CollectedValuesList extends ArrayList<Object> {

        private final ResolvableType elementType;

        CollectedValuesList(ResolvableType elementType) {
            this.elementType = elementType;
        }

        public ResolvableType getReturnType() {
            return ResolvableType.forClassWithGenerics(List.class, this.elementType);
        }
    }
}
