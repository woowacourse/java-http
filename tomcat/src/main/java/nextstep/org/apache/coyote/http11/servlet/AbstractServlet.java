package nextstep.org.apache.coyote.http11.servlet;

import static nextstep.org.apache.coyote.http11.HttpUtil.selectFirstContentTypeOrDefault;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import nextstep.org.apache.coyote.http11.Http11Request;
import nextstep.org.apache.coyote.http11.Http11Response;
import nextstep.org.apache.coyote.http11.Status;

public abstract class AbstractServlet implements Servlet{

    private static final String RESOURCES_PATH_PREFIX = "static";
    protected static final String NOT_FOUND_DEFAULT_MESSAGE = "404 Not Found";

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        String method = request.getMethod();

        if (method.equals("GET")) {
            doGet(request, response);
        } else if (method.equals("POST")) {
            doPost(request, response);
        } else {
            response.setStatus(Status.NOT_IMPLEMENTED);
        }
    }

    protected abstract void doPost(Http11Request request, Http11Response response) throws Exception;

    protected abstract void doGet(Http11Request request, Http11Response response) throws  Exception;

    protected Optional<String> createResponseBody(String requestPath) throws IOException {
        if (requestPath.equals("/")) {
            return Optional.of("Hello world!");
        }

        String resourceName = RESOURCES_PATH_PREFIX + requestPath;
        if (!resourceName.contains(".")) {
            resourceName += ".html";
        }
        URL resource = getClass().getClassLoader().getResource(resourceName);

        if (Objects.isNull(resource)) {
            return Optional.empty();
        }
        return Optional.of(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    protected void responseWithBody(Http11Request request, Http11Response response) throws IOException {
        Optional<String> responseBody = createResponseBody(request.getPathInfo());
        String contentType = selectFirstContentTypeOrDefault(request.getHeader("Accept"));

        if (responseBody.isEmpty()) {
            responseWithNotFound(request, response);
            return;
        }

        response.setStatus(Status.OK)
                .setHeader("Content-Type", contentType + ";charset=utf-8")
                .setHeader("Content-Length", String.valueOf(
                        responseBody.get().getBytes(StandardCharsets.UTF_8).length))
                .setBody(responseBody.get());
    }

    private void responseWithNotFound(Http11Request request, Http11Response response) throws IOException {
        String notFoundPageBody = createResponseBody("/404.html")
                .orElse(NOT_FOUND_DEFAULT_MESSAGE);
        String contentType = selectFirstContentTypeOrDefault(request.getHeader("Accept"));

        response.setStatus(Status.NOT_FOUND)
                .setHeader("Content-Type", contentType + ";charset=utf-8")
                .setHeader("Content-Length", String.valueOf(
                        notFoundPageBody.getBytes(StandardCharsets.UTF_8).length))
                .setBody(notFoundPageBody);
    }
}
