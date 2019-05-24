package org.springframework.web.servlet.support;

import org.springframework.lang.Nullable;
import org.springframework.ui.Model;

import java.util.Collection;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-10
 **/
public interface MyRedirectAttributes extends Model {

    @Override
    MyRedirectAttributes addAttribute(String attributeName, @Nullable Object attributeValue);

    @Override
    MyRedirectAttributes addAttribute(Object attributeValue);

    @Override
    MyRedirectAttributes addAllAttributes(Collection<?> attributeValues);

    @Override
    MyRedirectAttributes mergeAttributes(Map<String, ?> attributes);

    /**
     * Add the given flash attribute.
     * @param attributeName the attribute name; never {@code null}
     * @param attributeValue the attribute value; may be {@code null}
     */
    MyRedirectAttributes addFlashAttribute(String attributeName, @Nullable Object attributeValue);

    /**
     * Add the given flash storage using a
     * {@link org.springframework.core.Conventions#getVariableName generated name}.
     * @param attributeValue the flash attribute value; never {@code null}
     */
    MyRedirectAttributes addFlashAttribute(Object attributeValue);

    /**
     * Return the attributes candidate for flash storage or an empty Map.
     */
    Map<String, ?> getFlashAttributes();
}
