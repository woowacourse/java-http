package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpRequestURI;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.model.HttpStatusCode;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        if (request.isGetRequest()) {
            return doGet(request);
        }
        if (request.isPostRequest()) {
            return doPost(request);
        }
        return redirectTo(HttpRequestURI.from("/404"), HttpStatusCode.HTTP_STATUS_NOT_FOUND);
    }

    protected abstract HttpResponse doGet(HttpRequest request) throws Exception;

    protected abstract HttpResponse doPost(HttpRequest request) throws Exception;

    protected HttpResponse redirectTo(HttpRequestURI requestURI, HttpStatusCode httpStatusCode) throws IOException {
        return createHttpResponseFrom("static" + requestURI.getPath(), httpStatusCode, "text/html", requestURI);
    }

    protected HttpResponse createGetResponseFrom(HttpRequest request) throws IOException {
        return createHttpResponseFrom(request.getResourcePath(), HttpStatusCode.HTTP_STATUS_OK,
            request.getContentType(),
            request.getRequestURI());
    }

    private HttpResponse createHttpResponseFrom(String resourcePath, HttpStatusCode httpStatusCode, String ContentType,
        HttpRequestURI requestURI) throws IOException {

        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (Objects.isNull(resource)) {
            return redirectTo(HttpRequestURI.from("/404"), HttpStatusCode.HTTP_STATUS_NOT_FOUND);
        }
        String responseBody = createResponseBodyFrom(resource);

        return HttpResponse.of(httpStatusCode, responseBody)
            .setContentType(ContentType + ";charset=utf-8")
            .setLocation(requestURI.getPath());
    }

    private String createResponseBodyFrom(URL resource) throws IOException {
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
