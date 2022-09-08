package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class MainController extends AbstractController {

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

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return null;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return new HttpResponse().addProtocol(request.getRequestLine().getProtocol()).addStatus(HttpStatus.OK)
                .addResponseBody(WELCOME_MESSAGE, ContentType.TEXT_HTML_CHARSET_UTF_8);

    }
}
