package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpBody httpBody = HttpBody.createByUrl("/401.html");

        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.MOVED_TEMPORARILY)
                .contentType("/401.html")
                .contentLength(httpBody.getBody().getBytes().length)
                .location("/401.html");

        return new HttpResponse(httpHeader, httpBody);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpBody httpBody = HttpBody.createByUrl(httpRequest.getUrl());

        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.OK)
                .contentType(httpRequest.getUrl())
                .contentLength(httpBody.getBody().getBytes().length);

        return new HttpResponse(httpHeader, httpBody);
    }
}
