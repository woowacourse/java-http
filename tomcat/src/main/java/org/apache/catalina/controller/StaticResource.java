package org.apache.catalina.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpResponse;

public final class StaticResource {

    private static final String STATIC_RESOURCE_DIRECTORY = "static";

    private StaticResource() {
    }

    public static void serve(final String path, final HttpResponse response) throws IOException, URISyntaxException {
        response.setBody(contentTypeOf(path), read(path));
    }

    private static byte[] read(final String path) throws IOException, URISyntaxException {
        final URL url = StaticResource.class.getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + path);
        return Files.readAllBytes(Path.of(url.toURI()));
    }

    private static String contentTypeOf(final String path) {
        final String extension = path.substring(path.lastIndexOf(".") + 1);
        if (extension.equals("css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
