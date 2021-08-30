package nextstep.jwp.model.handler;

import nextstep.jwp.model.http_request.JwpHttpRequest;
import nextstep.jwp.model.http_response.JwpHttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceHandler implements CustomHandler {

    private static final String RESOURCE_PREFIX = "static";

    @Override
    public void handle(JwpHttpRequest jwpHttpRequest, OutputStream outputStream) throws IOException, URISyntaxException {
        String uri = jwpHttpRequest.getUri();
        URL resource = getClass().getClassLoader().getResource(RESOURCE_PREFIX + uri);
        final Path path = Paths.get(resource.toURI());
        String resourceFile = new String(Files.readAllBytes(path));
        final String response = JwpHttpResponse.ok(uri, resourceFile);
        outputStream.write(response.getBytes());
    }
}
