package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.model.CustomHttpRequest;

import java.io.OutputStream;

public class DefaultRequestProcessor implements RequestProcessor {

    @Override
    public String processResponse(CustomHttpRequest request, OutputStream outputStream) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: http://localhost:8080/login.html ",
                "",
                "");
    }
}
