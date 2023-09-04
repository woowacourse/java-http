package nextstep.jwp.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RegisterPageController implements Controller {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return "/register".equals(httpRequest.getPath())
                && HttpMethod.GET == httpRequest.getHttpMethod();
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final String body = ViewResolver.findView(httpRequest.getPath().substring(1));
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", ContentType.HTML.getContentType());
        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        return new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                headers,
                body
        );
    }
}
