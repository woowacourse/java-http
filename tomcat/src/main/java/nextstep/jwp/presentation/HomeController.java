package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;

public class HomeController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpBody httpBody = HttpBody.createByUrl("/404.html");
        final HttpHeader httpHeader = defaultHeader(StatusCode.MOVED_TEMPORARILY, httpBody, "/404.html");
        httpHeader.location("/404.html");

        return new HttpResponse(httpHeader, httpBody);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final HttpBody httpBody = new HttpBody("Hello world!");
        final HttpHeader httpHeader = defaultHeader(StatusCode.OK, httpBody, httpRequest.getUrl());

        return new HttpResponse(httpHeader, httpBody);
    }
}
