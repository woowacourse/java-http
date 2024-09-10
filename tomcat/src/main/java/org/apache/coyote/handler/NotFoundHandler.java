package org.apache.coyote.handler;

import com.techcourse.exception.UncheckedServletException;
import java.nio.charset.StandardCharsets;
import org.apache.ResourceReader;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class NotFoundHandler extends AbstractRequestHandler {

    private static final String NOT_FOUND_RESOURCE = "/404.html";

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = ResourceReader.readFile(NOT_FOUND_RESOURCE);
        httpResponse.notFound(body, StandardCharsets.UTF_8);
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UncheckedServletException(new UnsupportedOperationException("지원하지 않는 HTTP METHOD 입니다."));
    }
}
