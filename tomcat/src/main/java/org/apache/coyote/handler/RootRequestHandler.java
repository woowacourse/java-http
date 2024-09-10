package org.apache.coyote.handler;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;

public class RootRequestHandler extends AbstractRequestHandler {

    private static final String ROOT_RESOURCE = "Hello world!";

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setContentTypeHeader(MimeType.HTML);
        httpResponse.setBody(ROOT_RESOURCE);

    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse response) {
        throw new UncheckedServletException(new UnsupportedOperationException("지원하지 않는 HTTP METHOD 입니다."));
    }
}
