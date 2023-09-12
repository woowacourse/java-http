package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.HttpResponseHeader.ALLOW;
import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_TYPE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public abstract class AbstractController implements Controller {

    private static final String METHOD_NOT_ALLOWED_PAGE_PATH = "/405.html";
    private static final String RESOURCE_DIRECTORY = "static";
    private static final String ALLOW_METHOD = "GET, POST";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        if(HttpMethod.POST == request.getHttpMethod()) {
            doPost(request, response);
            return;
        }

        if(HttpMethod.GET == request.getHttpMethod()) {
            doGet(request, response);
            return;
        }

        resolveMethodNowAllowedPage(response);
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;

    private void resolveMethodNowAllowedPage(final HttpResponse response) throws IOException {
        final String body = generateBody();
        response.setStatusCode(StatusCode.METHOD_NOT_ALLOWED);
        response.setBody(body);
        response.addHeader(ALLOW, ALLOW_METHOD);
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getContentType());
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    private String generateBody() throws IOException {
        String fileName = RESOURCE_DIRECTORY + METHOD_NOT_ALLOWED_PAGE_PATH;
        URL resource = getClass().getClassLoader().getResource(fileName);
        final Path path = new File(resource.getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }
}
