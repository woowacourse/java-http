package nextstep.jwp.controller;

import org.apache.coyote.model.request.ContentType;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;

public class HomeHandler extends AbstractHandler {

    public static final String HELLO_WORLD = "Hello world!";

    public HomeHandler(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public String getResponse() {
        return HttpResponse.of(ContentType.HTML.toString(), HELLO_WORLD, ResponseLine.of(StatusCode.OK))
                .getResponse();
    }
}
