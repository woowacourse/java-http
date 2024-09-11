package com.techcourse.handler;

import com.techcourse.exception.UncheckedServletException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.MimeType;

public class RootRequestHandler extends AbstractRequestHandler {

    private static final String ROOT_RESOURCE = "Hello world!";

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.ok(MimeType.HTML, ROOT_RESOURCE, StandardCharsets.UTF_8);
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse response) {
        throw new UncheckedServletException(new UnsupportedOperationException("지원하지 않는 HTTP METHOD 입니다."));
    }
}
