package nextstep.jwp.model.handler;

import nextstep.jwp.model.http_request.JwpHttpRequest;

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
        URL resource = getClass().getClassLoader().getResource(RESOURCE_PREFIX + jwpHttpRequest.getUri());
        final Path path = Paths.get(resource.toURI());
        String resourceFile = new String(Files.readAllBytes(path));
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + resourceFile.getBytes().length + " ",
                "",
                resourceFile);
        outputStream.write(response.getBytes());
    }
}
