package org.springframework.web.servlet.mvc.condition;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public class MyRequestMethodsRequestCondition  extends MyAbstractRequestCondition<MyRequestMethodsRequestCondition>{

    private static final MyRequestMethodsRequestCondition GET_CONDITION =
            new MyRequestMethodsRequestCondition(RequestMethod.GET);

    private final Set<RequestMethod> methods;

    public MyRequestMethodsRequestCondition(RequestMethod... requestMethods) {
        this(Arrays.asList(requestMethods));
    }

    private MyRequestMethodsRequestCondition(Collection<RequestMethod> requestMethods) {
        this.methods = Collections.unmodifiableSet(new LinkedHashSet<>(requestMethods));
    }

    @Override
    protected Collection<RequestMethod> getContent() {
        return this.methods;
    }
    @Override
    public MyRequestMethodsRequestCondition combine(MyRequestMethodsRequestCondition other) {
        Set<RequestMethod> set = new LinkedHashSet<>(this.methods);
        set.addAll(other.methods);
        return new MyRequestMethodsRequestCondition(set);
    }

    @Override
    public MyRequestMethodsRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return matchPreFlight(request);
        }

        if (getMethods().isEmpty()) {
            if (RequestMethod.OPTIONS.name().equals(request.getMethod()) &&
                    !DispatcherType.ERROR.equals(request.getDispatcherType())) {

                return null; // No implicit match for OPTIONS (we handle it)
            }
            return this;
        }

        return matchRequestMethod(request.getMethod());
    }

    @Nullable
    private MyRequestMethodsRequestCondition matchPreFlight(HttpServletRequest request) {
        if (getMethods().isEmpty()) {
            return this;
        }
        String expectedMethod = request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
        return matchRequestMethod(expectedMethod);
    }

    @Nullable
    private MyRequestMethodsRequestCondition matchRequestMethod(String httpMethodValue) {
        HttpMethod httpMethod = HttpMethod.resolve(httpMethodValue);
        if (httpMethod != null) {
            for (RequestMethod method : getMethods()) {
                if (httpMethod.matches(method.name())) {
                    return new MyRequestMethodsRequestCondition(method);
                }
            }
            if (httpMethod == HttpMethod.HEAD && getMethods().contains(RequestMethod.GET)) {
                return GET_CONDITION;
            }
        }
        return null;
    }

    @Override
    public int compareTo(MyRequestMethodsRequestCondition other, HttpServletRequest request) {
        return 0;
    }
    public Set<RequestMethod> getMethods() {
        return this.methods;
    }
}
