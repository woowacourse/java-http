package nextstep.jwp.handler;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;

public interface Handler {

    HttpResponse handle(final HttpRequest request) throws UncheckedServletException;

    boolean isSupported(final HttpRequest request);
}
