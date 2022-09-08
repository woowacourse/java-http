package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class MainController implements Controller {

    private static final String WELCOME_MESSAGE = "Hello world!";

    @Override
    public HttpResponse service(HttpRequest request) {
        RequestLine requestLine = request.getRequestLine();
        Method method = requestLine.getMethod();

        if (method.isGet()) {
            return doGet(request);
        }

        return doNotFoundRequest(request);
    }

    protected HttpResponse doGet(final HttpRequest request) {
        return new HttpResponse().addProtocol(request.getRequestLine().getProtocol()).addStatus(HttpStatus.OK)
                .addResponseBody(WELCOME_MESSAGE, ContentType.TEXT_HTML_CHARSET_UTF_8);

    }

    private HttpResponse doNotFoundRequest(HttpRequest request) {
        return new HttpResponse().addProtocol(request.getRequestLine().getProtocol()).addStatus(HttpStatus.NOT_FOUND)
                .addResponseBody("페이지를 찾을 수 없습니다.", ContentType.TEXT_HTML_CHARSET_UTF_8);
    }
}
