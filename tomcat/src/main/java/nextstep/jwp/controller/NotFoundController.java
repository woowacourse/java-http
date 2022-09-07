package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.header.HttpStatusCode.FOUND;

import java.io.IOException;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.Location;

public class NotFoundController implements Controller {

    private static final String NOT_FOUND_REDIRECT_LOCATION = "/404.html";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return false;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setHttpStatusCode(FOUND);
        httpResponse.addHeader(new Location(NOT_FOUND_REDIRECT_LOCATION));
    }
}
