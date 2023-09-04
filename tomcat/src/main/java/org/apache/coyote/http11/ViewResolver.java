package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.coyote.http11.ContentType.CSS;
import static org.apache.coyote.http11.ContentType.TEXT_HTML;

public class ViewResolver {
    private static final String DEFAULT_FILE_ROUTE = "static";

    private ViewResolver() {
    }

    public static HttpResponse resolveView(String path) throws IOException {
        String filePath = path;
        if (!filePath.contains(".")) {
            filePath += ".html";
        }

        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        final URL resource = systemClassLoader.getResource(String.format("%s%s", DEFAULT_FILE_ROUTE, filePath));

        if (resource == null) {
            final URL notFoundUrl = systemClassLoader.getResource(String.format("%s/%s", DEFAULT_FILE_ROUTE, "404.html"));
            File notFound = new File(notFoundUrl.getPath());
            String body = new String(Files.readAllBytes(notFound.toPath()));
            return new HttpResponse(body, HttpStatus.NOT_FOUND, TEXT_HTML);
        }

        File file = new File(resource.getPath());
        String body = new String(Files.readAllBytes(file.toPath()));
        if (file.getName().endsWith(".css")) {
            return new HttpResponse(body, HttpStatus.OK, CSS);
        }
        return new HttpResponse(body, HttpStatus.OK, TEXT_HTML);
    }

    public static HttpResponse routePath(String path) throws IOException {
        if (path.equals("/")) {
            return new HttpResponse("Hello world!", HttpStatus.OK, TEXT_HTML);
        }

        return ViewResolver.resolveView(path);
    }
}
