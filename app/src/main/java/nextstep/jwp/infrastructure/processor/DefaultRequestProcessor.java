package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.model.CustomHttpRequest;
import nextstep.jwp.model.CustomHttpResponse;

import java.io.OutputStream;

public class DefaultRequestProcessor implements RequestProcessor {

    private static final String DEFAULT_URI = "login.html";

    @Override
    public String processResponse(CustomHttpRequest request, OutputStream outputStream) {
        return CustomHttpResponse.found(DEFAULT_URI);
    }
}
