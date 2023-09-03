package nextstep.jwp.ui;

import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.controller.HttpController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;

public class Controller extends HttpController {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatusCode(StatusCode.OK);
        httpResponse.setContentType(ContentType.TEXT_HTML.value + ";charset=utf-8");
        httpResponse.setBody("Hello world!");
    }
}
