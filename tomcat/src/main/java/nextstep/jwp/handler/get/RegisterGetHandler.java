package nextstep.jwp.handler.get;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class RegisterGetHandler implements Handler {

    private static final String STATIC = "static";

    @Override
    public Http11Response resolve(final Http11Request request) throws IOException {
        final var resource = getClass().getClassLoader().getResource(STATIC + "/register.html");
        return makeHttp11Response(resource, StatusCode.OK);
    }

    private Http11Response makeHttp11Response(final URL resource, final StatusCode statusCode) throws IOException {
        final var actualFilePath = new File(resource.getPath()).toPath();
        final var fileBytes = Files.readAllBytes(actualFilePath);
        final String responseBody = new String(fileBytes, StandardCharsets.UTF_8);
        return new Http11Response(statusCode, ContentType.findByPath(resource.getPath()), responseBody);
    }
}
