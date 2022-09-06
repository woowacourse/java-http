package org.apache.coyote.http11.response.generator;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterResponseGenerator implements ResponseGenerator {

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.isRegisterRequest();
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) throws IOException {
        return null;
    }
}
