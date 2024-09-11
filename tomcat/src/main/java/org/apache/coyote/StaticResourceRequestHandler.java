package org.apache.coyote;

import java.nio.charset.StandardCharsets;
import org.apache.ResourceReader;
import org.apache.coyote.http11.MimeType;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        validateRequestMethod(httpRequest);
        String body = ResourceReader.readFile(httpRequest.getRequestURI());
        httpResponse.ok(getContentType(httpRequest), body, StandardCharsets.UTF_8);
    }

    private void validateRequestMethod(HttpRequest httpRequest) {
        if (!httpRequest.isGet()) {
            throw new UnsupportedOperationException("지원하지 않는 Http Method 입니다.");
        }
    }

    private MimeType getContentType(HttpRequest httpRequest) {
        if (!httpRequest.existsAccept()) {
            return MimeType.fromFileName(httpRequest.getRequestURI());
        }
        return httpRequest.getAcceptMimeType();
    }
}
