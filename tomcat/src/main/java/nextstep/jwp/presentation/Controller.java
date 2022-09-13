package nextstep.jwp.presentation;

import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;

public interface Controller {
    void service(Http11Request request, Http11Response response) throws Exception;
}
