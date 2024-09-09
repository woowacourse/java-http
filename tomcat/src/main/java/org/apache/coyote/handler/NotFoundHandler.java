package org.apache.coyote.handler;

import com.techcourse.exception.UncheckedServletException;
import org.apache.ResourceReader;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class NotFoundHandler extends AbstractRequestHandler {

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.NOT_FOUND);
        httpResponse.setHeader("Content-Type", "text/html;charset=utf-8 ");
        httpResponse.setBody(ResourceReader.readFile("/404.html"));
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UncheckedServletException(new UnsupportedOperationException("지원하지 않는 HTTP METHOD 입니다."));
    }
}
