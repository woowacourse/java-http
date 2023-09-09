package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.response.ResponseBody;

public class FileExtractor {

    private static final ClassLoader CLASSLOADER = ClassLoader.getSystemClassLoader();
    private static final String STATIC = "static";
    private static final String TYPE_SEPARATOR = ".";

    private FileExtractor() {
    }

    public static ResponseBody extractFile(final String resource) throws IOException {
        if (!resource.contains(TYPE_SEPARATOR)) {
            return extractHtmlFile(resource);
        }
        final URL url = CLASSLOADER.getResource(STATIC + resource);
        return ResponseBody.of(ExtensionType.from(resource).getExtension(), makeBodyContent(url));
    }

    private static ResponseBody extractHtmlFile(final String resource) throws IOException {
        final URL url = CLASSLOADER.getResource(STATIC + resource + ExtensionType.HTML.getExtension());
        return ResponseBody.of(ExtensionType.HTML.getExtension(), makeBodyContent(url));
    }

    private static String makeBodyContent(final URL url) throws IOException {
        final Path path = new File(url.getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }
}
