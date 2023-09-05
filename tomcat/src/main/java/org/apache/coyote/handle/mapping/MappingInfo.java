package org.apache.coyote.handle.mapping;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.request.HttpRequest;

public abstract class MappingInfo {

    protected final String uriPath;
    protected final Map<HttpMethod, Method> methodMapping = new EnumMap<>(HttpMethod.class);

    protected MappingInfo(final String uriPath) {
        this.uriPath = uriPath;
    }

    public abstract boolean support(final HttpRequest httpRequest);

    public Method getMethod(final HttpMethod httpMethod) {
        return methodMapping.get(httpMethod);
    }
}
