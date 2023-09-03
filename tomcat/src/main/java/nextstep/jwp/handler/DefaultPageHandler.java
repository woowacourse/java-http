package nextstep.jwp.handler;

import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.Url;

public class DefaultPageHandler implements Handler {

    private static final String supportUrl = "/";

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return new HttpResponse(HttpStatus.OK, HttpHeaders.getEmptyHeaders(), "Hello world");
    }

    @Override
    public boolean isSupported(final HttpRequest request) {
        return request.isRequestMethodOf(HttpMethod.GET) && request.isUrl(Url.from(supportUrl));
    }
}
