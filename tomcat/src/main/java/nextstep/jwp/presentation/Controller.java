package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    HttpResponse process(final HttpRequest httpRequest) throws IOException;
}
