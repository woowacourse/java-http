package nextstep.jwp.presentation;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.util.ResourceReader;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.response.HttpStatus.OK;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String requestPath = request.requestLine().requestPath().value();
        final String resourceBody = ResourceReader.read(requestPath);
        final ResponseBody responseBody = ResponseBody.from(resourceBody);

        response.setHttpVersion(HTTP_1_1)
                .setContentType(MediaType.from(requestPath).value())
                .setHttpStatus(OK)
                .setResponseBody(responseBody)
                .setContentLength(resourceBody.length());
    }
}
