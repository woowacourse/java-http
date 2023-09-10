package nextstep.jwp.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

public class HomeController extends AbstractController {

    private static final String SUPPORT_URI_PATH = "/";
    private static final String HOME_FILE_PATH = "/home.html";

    @Override
    public boolean support(final HttpRequest request) {
        return request.isPathOf(SUPPORT_URI_PATH);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final ResponseEntity responseEntity = ResponseEntity.builder()
                                                            .httpStatus(HttpStatus.NOT_FOUND)
                                                            .build();

        response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final ResponseEntity responseEntity = ResponseEntity.builder()
                                                            .httpStatus(HttpStatus.OK)
                                                            .resourcePath(HOME_FILE_PATH)
                                                            .build();

        response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
    }
}
