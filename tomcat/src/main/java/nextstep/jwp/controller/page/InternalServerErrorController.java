package nextstep.jwp.controller.page;

import static nextstep.jwp.controller.FileContent.STATIC;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatusLine;

public class InternalServerErrorController extends AbstractController {

    public static HttpResponse create(final HttpRequest request) throws IOException {
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + "/500.html");
        final Path path = new File(url.getPath()).toPath();

        final ResponseStatusLine statusLine = ResponseStatusLine.create(HttpStatus.INTERNAL_SERVER_ERROR);
        final HttpHeaders headers = HttpHeaders.createResponse(path);

        final byte[] content = Files.readAllBytes(path);
        final String responseBody = new String(content);

        return new HttpResponse(statusLine, headers, responseBody);
    }
}
