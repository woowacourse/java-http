package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.types.ContentType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.coyote.http11.types.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.types.HttpStatus.NOT_FOUND;
import static org.apache.coyote.http11.types.HttpStatus.OK;

public class ViewResolver {
    private static final String DEFAULT_FILE_ROUTE = "static";

    private ViewResolver() {
    }

    public static void resolveView(HttpRequest request, HttpResponse response) throws IOException {
        String filePath = request.getPath();
        if (!filePath.contains(".")) {
            filePath += ".html";
        }

        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        final URL resource = systemClassLoader.getResource(String.format("%s%s", DEFAULT_FILE_ROUTE, filePath));

        if (resource == null) {
            final URL notFoundUrl = systemClassLoader.getResource(String.format("%s/%s", DEFAULT_FILE_ROUTE, "404.html"));
            File notFound = new File(notFoundUrl.getPath());
            String body = new String(Files.readAllBytes(notFound.toPath()));
            response.setBody(body, TEXT_HTML);
            response.setHttpStatus(NOT_FOUND);
            return;
        }

        File file = new File(resource.getPath());
        String body = new String(Files.readAllBytes(file.toPath()));
        response.setBody(body, ContentType.from(file.getName()));
        response.setHttpStatus(OK);
    }
}
