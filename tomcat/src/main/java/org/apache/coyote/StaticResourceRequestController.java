package org.apache.coyote;

import com.techcourse.controller.AbstractRequestController;
import com.techcourse.exception.UncheckedServletException;
import java.nio.charset.StandardCharsets;
import org.apache.ResourceReader;
import org.apache.coyote.http11.MimeType;

public class StaticResourceRequestController extends AbstractRequestController {

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String body = ResourceReader.readFile(httpRequest.getRequestURI());
        httpResponse.ok(getContentType(httpRequest), body, StandardCharsets.UTF_8);
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
