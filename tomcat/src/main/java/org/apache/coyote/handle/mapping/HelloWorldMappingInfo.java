package org.apache.coyote.handle.mapping;

import java.util.Objects;
import org.apache.coyote.handle.handler.HelloWorldHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.common.HttpMethod;

public class HelloWorldMappingInfo extends MappingInfo {

    public HelloWorldMappingInfo() {
        super("/");
        try {
            methodMapping.put(
                    HttpMethod.GET,
                    HelloWorldHandler.class.getDeclaredMethod("doGet", HttpRequest.class, HttpResponse.class)
            );
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return methodMapping.keySet().contains(httpRequest.getHttpMethod())
                && Objects.equals(uriPath, httpRequest.getUriPath());
    }
}
