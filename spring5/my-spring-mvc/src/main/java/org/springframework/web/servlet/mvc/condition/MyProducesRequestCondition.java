package org.springframework.web.servlet.mvc.condition;

import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.MyHeadersRequestCondition.HeaderExpression;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public final class MyProducesRequestCondition  extends MyAbstractRequestCondition<MyProducesRequestCondition> {

    private static final MyProducesRequestCondition PRE_FLIGHT_MATCH = new MyProducesRequestCondition();

    private static final MyProducesRequestCondition EMPTY_CONDITION = new MyProducesRequestCondition();


    private final ContentNegotiationManager contentNegotiationManager;

    private final List<ProduceMediaTypeExpression> expressions;

    public MyProducesRequestCondition(String... produces) {
        this(produces, null, null);
    }


    public MyProducesRequestCondition(String[] produces, @Nullable String[] headers) {
        this(produces, headers, null);
    }

    public MyProducesRequestCondition(String[] produces, @Nullable String[] headers,
                                    @Nullable ContentNegotiationManager manager) {

        this.expressions = new ArrayList<>(parseExpressions(produces, headers));
        Collections.sort(this.expressions);
        this.contentNegotiationManager = (manager != null ? manager : new ContentNegotiationManager());
    }

    private MyProducesRequestCondition(Collection<ProduceMediaTypeExpression> expressions,
                                     @Nullable ContentNegotiationManager manager) {

        this.expressions = new ArrayList<>(expressions);
        Collections.sort(this.expressions);
        this.contentNegotiationManager = (manager != null ? manager : new ContentNegotiationManager());
    }
    @Override
    public MyProducesRequestCondition combine(MyProducesRequestCondition other) {
        return (!other.expressions.isEmpty() ? other : this);
    }

    @Override
    @Nullable
    public MyProducesRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return PRE_FLIGHT_MATCH;
        }
        if (isEmpty()) {
            return this;
        }

        List<MediaType> acceptedMediaTypes;
        try {
            acceptedMediaTypes = getAcceptedMediaTypes(request);
        }
        catch (HttpMediaTypeException ex) {
            return null;
        }

        Set<ProduceMediaTypeExpression> result = new LinkedHashSet<>(this.expressions);
        result.removeIf(expression -> !expression.match(acceptedMediaTypes));
        if (!result.isEmpty()) {
            return new MyProducesRequestCondition(result, this.contentNegotiationManager);
        }
        else if (acceptedMediaTypes.contains(MediaType.ALL)) {
            return EMPTY_CONDITION;
        }
        else {
            return null;
        }
    }

    private List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        return this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
    }

    @Override
    public int compareTo(MyProducesRequestCondition other, HttpServletRequest request) {
        return 0;
    }


    private Set<ProduceMediaTypeExpression> parseExpressions(String[] produces, @Nullable String[] headers) {
        Set<ProduceMediaTypeExpression> result = new LinkedHashSet<>();
        if (headers != null) {
            for (String header : headers) {
                HeaderExpression expr = new HeaderExpression(header);
                if ("Accept".equalsIgnoreCase(expr.name) && expr.value != null) {
                    for (MediaType mediaType : MediaType.parseMediaTypes(expr.value)) {
                        result.add(new ProduceMediaTypeExpression(mediaType, expr.isNegated));
                    }
                }
            }
        }
        for (String produce : produces) {
            result.add(new ProduceMediaTypeExpression(produce));
        }
        return result;
    }

    @Override
    protected Collection<?> getContent() {
        return this.expressions;
    }


    static class ProduceMediaTypeExpression extends MyAbstractMediaTypeExpression {

        ProduceMediaTypeExpression(MediaType mediaType, boolean negated) {
            super(mediaType, negated);
        }

        ProduceMediaTypeExpression(String expression) {
            super(expression);
        }

        public final boolean match(List<MediaType> acceptedMediaTypes) {
            boolean match = matchMediaType(acceptedMediaTypes);
            return (!isNegated() ? match : !match);
        }

        private boolean matchMediaType(List<MediaType> acceptedMediaTypes) {
            for (MediaType acceptedMediaType : acceptedMediaTypes) {
                if (getMediaType().isCompatibleWith(acceptedMediaType)) {
                    return true;
                }
            }
            return false;
        }
    }
}
