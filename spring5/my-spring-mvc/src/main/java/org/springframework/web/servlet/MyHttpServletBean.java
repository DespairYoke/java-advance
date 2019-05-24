package org.springframework.web.servlet;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-18
 **/
public abstract class MyHttpServletBean extends HttpServlet implements EnvironmentCapable {

    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private ConfigurableEnvironment environment;


    private final Set<String> requiredProperties = new HashSet<>(4);

    public final void init() throws ServletException {

        logger.info("MyHttpServletBean servletName  " +getServletName());

        logger.info("MyHttpServletBean ServletConfig  " +getServletConfig());

        /**
         * 把web.xml中的init-param中的key value拿到
         */
        PropertyValues pvs = new MyServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
        /**
         * 当<servlet>没有参数不走此if</servlet>
         */
        if (!pvs.isEmpty()) {
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
            ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
            bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
            initBeanWrapper(bw);
            bw.setPropertyValues(pvs, true);
        }

        initServletBean();
    }



    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardServletEnvironment();
    }


    protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
    }

    public String getServletName() {
        return (getServletConfig() != null ? getServletConfig().getServletName() : null);
    }


    protected void initServletBean() throws ServletException {
    }


    private static class MyServletConfigPropertyValues extends MutablePropertyValues {

        public MyServletConfigPropertyValues(ServletConfig config, Set<String> requiredProperties)
                throws ServletException {

            Set<String> missingProps = (!CollectionUtils.isEmpty(requiredProperties) ?
                    new HashSet<>(requiredProperties) : null);

            Enumeration<String> paramNames = config.getInitParameterNames();
            while (paramNames.hasMoreElements()) {
                String property = paramNames.nextElement();
                Object value = config.getInitParameter(property);
                addPropertyValue(new PropertyValue(property, value));
                if (missingProps != null) {
                    missingProps.remove(property);
                }
            }

//            // Fail if we are still missing properties.
//            if (!CollectionUtils.isEmpty(missingProps)) {
//                throw new ServletException(
//                        "Initialization from ServletConfig for servlet '" + config.getServletName() +
//                                "' failed; the following required properties were missing: " +
//                                StringUtils.collectionToDelimitedString(missingProps, ", "));
//            }
        }

    }
}
