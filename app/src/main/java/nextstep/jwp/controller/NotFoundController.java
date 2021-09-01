package nextstep.jwp.controller;

import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public class NotFoundController extends AbstractController {

    private static final String NOT_FOUND_ERROR_PAGE = "/404.html";

    @Override
    public JwpHttpResponse doGet(JwpHttpRequest request) throws URISyntaxException, IOException {
        return parsingNotFoundPage();
    }

    @Override
    public JwpHttpResponse doPost(JwpHttpRequest request) throws URISyntaxException, IOException {
        return parsingNotFoundPage();
    }

    private JwpHttpResponse parsingNotFoundPage() throws URISyntaxException, IOException {
        String resourceFile = findResourceFile(RESOURCE_PREFIX + NOT_FOUND_ERROR_PAGE);
        return JwpHttpResponse.notFound(resourceFile);
    }
}
