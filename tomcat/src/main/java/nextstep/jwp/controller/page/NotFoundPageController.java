package nextstep.jwp.controller.page;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseLine;

public class NotFoundPageController implements Controller {

    private static final String STATIC = "static";

    private NotFoundPageController() {
    }

    public static Controller create() {
        return new NotFoundPageController();
    }

    @Override
    public HttpResponse process(final HttpRequest request) throws IOException {
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + "/404" + ".html");

        final Path path = new File(url.getPath()).toPath();

        final HttpHeaders headers = HttpHeaders.createResponse(path);

        return new HttpResponse(ResponseLine.create(HttpStatus.NOT_FOUND), headers, "");
    }
}
