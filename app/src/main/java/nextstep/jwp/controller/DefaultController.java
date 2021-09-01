package nextstep.jwp.controller;

import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.response.HttpResponse;
import nextstep.jwp.domain.response.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultController implements Controller {

    private static final String DEFAULT_BODY = "Hello world!";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String DEFAULT_CONTENT_VALUE = "12";
    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    @Override
    public HttpResponse service(HttpRequest request) {
        String responseBody = DEFAULT_BODY;
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE, TEXT_HTML_CHARSET_UTF_8);
        headers.put(CONTENT_LENGTH, DEFAULT_CONTENT_VALUE);
        return new HttpResponse(HttpStatus.OK, headers, responseBody);
    }
}
