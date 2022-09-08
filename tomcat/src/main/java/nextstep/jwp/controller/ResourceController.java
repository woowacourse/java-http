package nextstep.jwp.controller;

import static org.apache.coyote.response.StatusCode.OK;

import java.net.URL;
import javassist.NotFoundException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String requestPath = request.getRequestPath();
        checkExistResource(requestPath);

        response.setResponse(OK, ContentType.from(requestPath), requestPath);
        response.print();
    }

    private static void checkExistResource(final String requestPath) throws NotFoundException {
        final URL resourceUrl = ClassLoader.getSystemResource("static" + requestPath);
        if (resourceUrl == null) {
            throw new NotFoundException("존재하지 않는 자원에 대한 요청입니다.");
        }
    }
}
