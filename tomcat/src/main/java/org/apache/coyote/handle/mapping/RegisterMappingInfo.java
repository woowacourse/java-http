package org.apache.coyote.handle.mapping;

import java.util.Objects;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.handle.handler.RegisterHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RegisterMappingInfo extends MappingInfo {

    public RegisterMappingInfo() {
        super("/register");
        try {
            methodMapping.put(
                    HttpMethod.GET,
                    RegisterHandler.class.getDeclaredMethod("doGet", HttpRequest.class, HttpResponse.class)
            );
            methodMapping.put(
                    HttpMethod.POST,
                    RegisterHandler.class.getDeclaredMethod("doPost", HttpRequest.class, HttpResponse.class)
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
