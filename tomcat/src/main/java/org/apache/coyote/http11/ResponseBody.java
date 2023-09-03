package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ResponseBody {

    private static final ClassLoader CLASS_LOADER = ResponseBody.class.getClassLoader();
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final byte[] data;

    private ResponseBody(final byte[] data) {
        this.data = data;
    }

    public static <T> ResponseBody from(final T info) {
        if (info.equals("/")) {
            return new ResponseBody("Hello world!".getBytes());
        }
        return new ResponseBody(getNotDefaultPathResponseBody((String) info));
    }

    private static byte[] getNotDefaultPathResponseBody(final String requestURI) {
        String makeRequestURI = "static" + requestURI;
        if (isNonStaticFile(requestURI)) {
            makeRequestURI += ".html";
        }
        final URL url = getUrl(makeRequestURI);
        final Path path = new File(url.getPath()).toPath();

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("알 수 없는 에러 입니다.");
        }
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

    public String getResponseMessage(final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=" + CHARSET.name().toLowerCase() + " ",
                "Content-Length: " + data.length + " ",
                "",
                new String(data, CHARSET));
    }
}
