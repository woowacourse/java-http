package nextstep.jwp.controller;

import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.StatusCode;
import nextstep.jwp.io.ClassPathResource;
import nextstep.java.servlet.Controller;

public abstract class AbstractController implements Controller {

    private static final String HTML_EXTENSION = ".html";
    private static final String FILE_EXTENSION_START = ".";

    protected static final String INDEX_PAGE_URL = "./index.html";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.hasPostMethod()) {
            doPost(request, response);
        }
        if (request.hasGetMethod()) {
            doGet(request, response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String path = resourcePath(request.getPath());
        String responseBody = new ClassPathResource().getStaticContent(path);

        response.setContentLength(responseBody.getBytes().length);
        response.setResponseBody(responseBody);
        response.setStatusCode(StatusCode.OK);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        response.sendRedirect(INDEX_PAGE_URL);
    }

    private String resourcePath(final String path) {
        if (!path.contains(FILE_EXTENSION_START)) {
            return path + HTML_EXTENSION;
        }
        return path;
    }
}
