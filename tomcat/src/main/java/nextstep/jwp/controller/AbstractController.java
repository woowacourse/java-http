package nextstep.jwp.controller;

import nextstep.jwp.exception.HttpRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    protected static final String TEXT_HTML = "text/html;charset=utf-8";
    protected static final String TEXT_CSS = "text/css;";
    protected static final String INDEX_PAGE = "/index.html";
    protected static final String UNAUTHORIZED_PAGE = "/401.html";
    protected static final String HEADER_LOCATION = "Location";
    protected static final String HEADER_SET_COOKIE = "Set-Cookie";
    protected static final String HEADER_CONTENT_TYPE = "Content-Type";
    protected static final String HEADER_CONTENT_LENGTH = "Content-Length";
    protected static final String HTTP_METHOD_EXCEPTION_MESSAGE = "올바르지 않은 HTTP Method 입니다.";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.getRequestLine().getMethod().equals("POST")) {
            doPost(request, response);
            return;
        }
        if (request.getRequestLine().getMethod().equals("GET")) {
            doGet(request, response);
            return;
        }
        throw new HttpRequestException(HTTP_METHOD_EXCEPTION_MESSAGE);
    }

    protected abstract void doPost(final HttpRequest request, final HttpResponse response) throws IOException;

    protected abstract void doGet(final HttpRequest request, final HttpResponse response) throws IOException;
}
