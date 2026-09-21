package org.apache.coyote.http11.resolver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;

public class View {

    public static void renderStaticPage(String uri, HttpResponse response)
            throws URISyntaxException, IOException {
        String resourcePath = PageResolver.resolve(uri);
        byte[] content = readStaticResource(resourcePath);
        if (content == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            content = readStaticResource("/404.html");
        }

        response.setBody(new HttpResponseBody(content));
        response.setHeader("Content-Type", ContentType.from(resourcePath));
    }

    private static byte[] readStaticResource(String resourcePath) throws URISyntaxException, IOException {
        URL resource = View.class.getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            return null;
        }
        Path path = Path.of(resource.toURI());
        return Files.readAllBytes(path);
    }
}
