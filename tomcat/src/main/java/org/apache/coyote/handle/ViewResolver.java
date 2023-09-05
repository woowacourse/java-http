package org.apache.coyote.handle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.response.HttpResponse;

public class ViewResolver {

    private static final String STATIC_DIRECTORY = "static";
    private static final String SLASH = File.separator;
    private static final String NOT_FOUND = "404.html";
    private static final ViewResolver VIEW_RESOLVER = new ViewResolver();

    private ViewResolver() {
    }

    public static ViewResolver getInstance() {
        return VIEW_RESOLVER;
    }

    public void renderPage(final HttpResponse httpResponse, final HttpStatus httpStatus, final String page)
            throws IOException {
        try {
            httpResponse.setStatus(httpStatus);
            httpResponse.setContentType(getContentType(page));
            httpResponse.setContent(getBody(page));
        } catch (NullPointerException e) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND);
            httpResponse.setContentType(ContentType.TEXT_HTML.getType());
            httpResponse.setContent(getBody(NOT_FOUND));
        }
    }

    private String getContentType(final String page) {
        final String extension = page.substring(page.lastIndexOf(".") + 1);
        String contentType = ContentType.getTypeFrom(extension);
        return contentType == null ? ContentType.APPLICATION_OCTET_STREAM.getType() : contentType;
    }

    private String getBody(final String page) throws IOException {
        final URL resource = ClassLoader.getSystemClassLoader().getResource(STATIC_DIRECTORY + SLASH + page);
        final File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
