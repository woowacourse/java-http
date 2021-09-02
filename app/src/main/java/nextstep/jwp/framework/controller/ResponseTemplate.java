package nextstep.jwp.framework.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import nextstep.jwp.framework.infrastructure.exception.NotFoundException;
import nextstep.jwp.framework.infrastructure.http.content.ContentType;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse.Builder;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;

public class ResponseTemplate {

    private ResponseTemplate() {
    }

    private static String readFile(String url) {
        String resourcePath = "static" + url;
        URL resource = Thread.currentThread().getContextClassLoader()
            .getResource(resourcePath);
        try {
            Path path = Paths.get(resource.toURI());
            return String.join("\r\n", Files.readAllLines(path));
        } catch (IOException | URISyntaxException | RuntimeException exception) {
            throw new NotFoundException();
        }
    }

    public static Builder template(HttpStatus httpStatus, String url) {
        return new Builder()
            .protocol(Protocol.HTTP1_1)
            .httpStatus(httpStatus)
            .contentType(ContentType.find(url))
            .responseBody(readFile(url));
    }

    public static Builder ok(String url) {
        return template(HttpStatus.OK, url);
    }

    public static Builder redirect(String url) {
        return template(HttpStatus.FOUND, url)
            .location(url);
    }

    public static Builder unauthorize(String url) {
        return template(HttpStatus.UNAUTHORIZED, url);
    }
}
