package org.apache.coyote.handler;

import com.techcourse.exception.UncheckedServletException;
import org.apache.ResourceReader;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.MimeType;

public class StaticResourceRequestHandler extends AbstractRequestHandler {

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = ResourceReader.readFile(httpRequest.getRequestURI());
        httpResponse.ok(getContentType(httpRequest), body);
    }

    private MimeType getContentType(HttpRequest httpRequest) {
        if (!httpRequest.existsAccept()) {
            return MimeType.fromFileName(httpRequest.getRequestURI());
        }
        return httpRequest.getAcceptMimeType();
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UncheckedServletException(new UnsupportedOperationException("지원하지 않는 HTTP METHOD 입니다."));
    }
}
