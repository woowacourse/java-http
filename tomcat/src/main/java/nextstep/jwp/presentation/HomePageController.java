package nextstep.jwp.presentation;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.common.MediaType.TEXT_HTML;
import static org.apache.coyote.response.HttpStatus.OK;

public class HomePageController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final ResponseBody responseBody = ResponseBody.from("Hello world!");

        response.setHttpVersion(HTTP_1_1)
                .setHttpStatus(OK)
                .setContentType(TEXT_HTML.value())
                .setContentLength(responseBody.length())
                .setResponseBody(responseBody);
    }
}
