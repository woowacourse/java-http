package nextstep.jwp.servlets;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import java.io.IOException;

public interface Servlet {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
