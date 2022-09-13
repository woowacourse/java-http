package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;

public abstract class AbstractController implements Controller {

    protected static final String NOT_FOUND_URL = "/404.html";
    protected static final String REDIRECT_URL = "/index.html";

    @Override
    public HttpResponse service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.matchMethod("POST")) {
            return doPost(httpRequest, httpResponse);
        }
        if (httpRequest.matchMethod("GET")) {
            return doGet(httpRequest, httpResponse);
        }
        final HttpBody httpBody = HttpBody.createByUrl(NOT_FOUND_URL);
        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.MOVED_TEMPORARILY)
                .contentType(NOT_FOUND_URL)
                .contentLength(httpBody.getBody().getBytes().length)
                .location(NOT_FOUND_URL);

        return httpResponse.header(httpHeader).body(httpBody);
    }

    protected HttpHeader defaultHeader(final StatusCode statusCode, final HttpBody httpBody, final String url) {
        return new HttpHeader().startLine(statusCode)
                .contentType(url)
                .contentLength(httpBody.getBody().getBytes().length);
    }

    protected abstract HttpResponse doPost(final HttpRequest httpRequest, final HttpResponse httpResponse)
            throws IOException;

    protected abstract HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse)
            throws IOException;
}
