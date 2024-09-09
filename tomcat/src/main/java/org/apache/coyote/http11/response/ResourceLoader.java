package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.ContentMimeType;

public class ResourceLoader {

    private static final ClassLoader classLoader = ResourceLoader.class.getClassLoader();

    public static ResponseBody loadHtmlResource(final String uri) throws IOException {
        final var resource = classLoader.getResource("static" + uri + ".html");
        final var responseBody = new String(getFileContent(resource));
        return new ResponseBody(ContentMimeType.getMimeByExtension("html"), responseBody);
    }

    public static ResponseBody loadStaticResource(final String uri) throws IOException {
        final var extension = uri.substring(uri.lastIndexOf('.') + 1);
        final var resource = classLoader.getResource("static" + uri);
        final var responseBody = new String(getFileContent(resource));
        return new ResponseBody(ContentMimeType.getMimeByExtension(extension), responseBody);
    }

    private static byte[] getFileContent(final URL resource) throws IOException {
        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }
}
