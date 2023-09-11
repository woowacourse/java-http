package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ResponseBody {

    private static final ClassLoader CLASS_LOADER = ResponseBody.class.getClassLoader();

    private byte[] data;

    private ResponseBody(final byte[] data) {
        this.data = data;
    }

    public static <T> ResponseBody from(final T info) throws IOException {
        if (info instanceof String) {
            final String uri = (String) info;
            if (uri.equals("/")) {
                return defaultBody();
            }
            return new ResponseBody(getNotDefaultPathResponseBody(uri));
        }

        throw new IllegalArgumentException("uri를 확인해주세요.");
    }

    private static ResponseBody defaultBody() {
        return new ResponseBody("Hello world!".getBytes());
    }

    private static byte[] getNotDefaultPathResponseBody(final String requestURI) throws IOException {
        String makeRequestURI = "static" + requestURI;
        if (isNonStaticFile(requestURI)) {
            makeRequestURI += ".html";
        }
        final URL url = getUrl(makeRequestURI);
        final Path path = new File(url.getPath()).toPath();
        return Files.readAllBytes(path);
    }

    private static URL getUrl(final String requestURI) {
        final URL url = CLASS_LOADER.getResource(requestURI);
        if (Objects.isNull(url)) {
            return CLASS_LOADER.getResource("static/404.html");
        }
        return url;
    }

    private static boolean isNonStaticFile(final String requestURI) {
        return !ContentType.checkFileExtension(requestURI);
    }

    public boolean isEmpty() {
        return data.length < 1;
    }

    public byte[] getBody() {
        return data;
    }

    public void changePage(final String page) throws IOException {
        this.data = getNotDefaultPathResponseBody(page);
    }

    public void changeBody(final String body) {
        this.data = body.getBytes();
    }
}
