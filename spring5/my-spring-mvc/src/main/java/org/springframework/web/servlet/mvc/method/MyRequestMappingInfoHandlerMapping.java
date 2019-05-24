package org.springframework.web.servlet.mvc.method;

import org.springframework.web.servlet.handler.MyAbstractHandlerMethodMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.Set;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public abstract class MyRequestMappingInfoHandlerMapping extends MyAbstractHandlerMethodMapping<MyRequestMappingInfo> {

    @Override
    protected Set<String> getMappingPathPatterns(MyRequestMappingInfo info) {
        return info.getPatternsCondition().getPatterns();
    }

    @Override
    protected MyRequestMappingInfo getMatchingMapping(MyRequestMappingInfo info, HttpServletRequest request) {
        return info.getMatchingCondition(request);
    }

    @Override
    protected Comparator<MyRequestMappingInfo> getMappingComparator(final HttpServletRequest request) {

        return (info1, info2) -> info1.compareTo(info2, request);
    }
}
