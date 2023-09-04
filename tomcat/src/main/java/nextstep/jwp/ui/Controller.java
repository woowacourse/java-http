package nextstep.jwp.ui;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.coyote.http.MediaType;
import org.apache.coyote.http.controller.HttpController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.session.Session;

public class Controller extends HttpController {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse, Session session) {
        httpResponse.setStatusCode(StatusCode.OK);
        httpResponse.setMediaType(MediaType.TEXT_HTML);
        httpResponse.setCharset(UTF_8);
        httpResponse.setBody("Hello world!");
    }
}
