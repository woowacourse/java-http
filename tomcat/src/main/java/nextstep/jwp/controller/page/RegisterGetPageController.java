package nextstep.jwp.controller.page;

import static nextstep.jwp.controller.FileContent.HTML;
import static nextstep.jwp.controller.FileContent.STATIC;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatusLine;

public class RegisterGetPageController implements Controller {

    private RegisterGetPageController() {
    }

    public static Controller create() {
        return new RegisterGetPageController();
    }

    @Override
    public HttpResponse process(final HttpRequest request) throws IOException {
        final String uri = request.getUri();
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + uri + HTML);

        final Path path = new File(url.getPath()).toPath();

        final byte[] content = Files.readAllBytes(path);

        final HttpHeaders headers = HttpHeaders.createResponse(path);
        final String responseBody = new String(content);

        return new HttpResponse(ResponseStatusLine.create(HttpStatus.OK), headers, responseBody);
    }
}
