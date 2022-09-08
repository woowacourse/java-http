package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    HttpResponse service(final HttpRequest httpRequest);
}
