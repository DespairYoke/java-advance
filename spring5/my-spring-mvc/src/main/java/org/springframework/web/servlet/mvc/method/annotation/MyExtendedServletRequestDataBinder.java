/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Map;
import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.MyHandlerMapping;

public class MyExtendedServletRequestDataBinder extends ServletRequestDataBinder {


    public MyExtendedServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }


    public MyExtendedServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }


    /**
     * Merge URI variables into the property values to use for data binding.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        String attr = MyHandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map<String, String> uriVars = (Map<String, String>) request.getAttribute(attr);
        if (uriVars != null) {
            uriVars.forEach((name, value) -> {
                if (mpvs.contains(name)) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Skipping URI variable '" + name +
                                "' since the request contains a bind value with the same name.");
                    }
                }
                else {
                    mpvs.addPropertyValue(name, value);
                }
            });
        }
    }

}
