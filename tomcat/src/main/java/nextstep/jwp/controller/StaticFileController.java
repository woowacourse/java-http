package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class StaticFileController implements Controller {
    @Override
    public boolean isRunnable(final Request request) {
        return request.isForStaticFile() || request.isDefaultUrl();
    }

    @Override
    public void run(final Request request, final Response response) throws IOException, URISyntaxException {
        response.write(HttpStatus.OK, request.getURL());
    }
}
