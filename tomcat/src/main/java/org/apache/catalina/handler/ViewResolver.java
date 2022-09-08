package org.apache.catalina.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ViewResolver {

    public static HttpResponse render(final HttpRequest request) {
        return new HttpResponse.Builder(request).ok()
            .messageBody(getStaticResource(request.getPath())).build();
    }

    private static String getStaticResource(final String path) {
        final URL resource = addExtensionToPath(path);

        try {
            return Files.readString(new File(Objects.requireNonNull(resource)
                .getFile())
                .toPath());
        } catch (IOException e) {
            throw new IllegalArgumentException("No such resource");
        }
    }

    private static URL addExtensionToPath(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }

        final URL resource = ViewResolver.class.getClassLoader().getResource("static" + path);
        if (resource == null) {
            throw new IllegalArgumentException("Static File Not Found for this path: " + path);
        }
        return resource;
    }
}
