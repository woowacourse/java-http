package org.apache.coyote.handler;

import com.techcourse.exception.UncheckedServletException;
import org.apache.ResourceReader;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;

public class StaticResourceRequestHandler extends AbstractRequestHandler {

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setContentTypeHeader(getContentType(httpRequest));
        httpResponse.setBody(ResourceReader.readFile(httpRequest.getRequestURI()));
    }

    private String getContentType(HttpRequest httpRequest) {
        if (httpRequest.getHeader("Accept") == null) {
            MimeType mimeType = MimeType.fromFileName(httpRequest.getRequestURI());
            return mimeType.value();
        }
        return httpRequest.getHeader("Accept").split(",")[0];
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UncheckedServletException(new UnsupportedOperationException("지원하지 않는 HTTP METHOD 입니다."));
    }
}
