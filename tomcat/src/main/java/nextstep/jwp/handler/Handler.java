package nextstep.jwp.handler;

import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;

public interface Handler {

    HttpResponse handle(final HttpRequest request);

    boolean isSupported(final HttpRequest request);
}
