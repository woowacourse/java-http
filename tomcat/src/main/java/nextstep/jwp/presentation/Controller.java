package nextstep.jwp.presentation;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import java.io.IOException;

public interface Controller {

    HttpResponse service(final HttpRequest request) throws NotFoundException, IOException;
}
