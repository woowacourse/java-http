package nextstep.jwp.presentation;

import static org.apache.coyote.http11.model.ContentType.TEXT_HTML_CHARSET_UTF_8;

import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.request.model.HttpRequest;

public class HomeController {

    private static final String BODY = "Hello world!";

    public HttpResponse index(final HttpRequest httpRequest) {
        return HttpResponse.builder()
                .body(BODY)
                .version(httpRequest.getVersion())
                .status(HttpStatus.OK.getValue())
                .headers("Content-Type: " + TEXT_HTML_CHARSET_UTF_8.getValue(),
                        "Content-Length: " + BODY.getBytes().length)
                .build();
    }
}
