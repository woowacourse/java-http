package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    HttpResponse service(final HttpRequest httpRequest,
                         final HttpResponse httpResponse) throws IOException;
}
