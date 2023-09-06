package org.apache.coyote.http11;

import static org.apache.common.Config.CHARSET;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ResponseBody {

    private static final ClassLoader CLASS_LOADER = ResponseBody.class.getClassLoader();

    private final byte[] data;
    private final ContentType contentType;
    private final HttpStatus httpStatus;
    private final HttpVersion httpVersion;

    private ResponseBody(final byte[] data, final ContentType contentType, final HttpStatus httpStatus,
                         final HttpVersion httpVersion) {
        this.data = data;
        this.contentType = contentType;
        this.httpStatus = httpStatus;
        this.httpVersion = httpVersion;
    }

    public static <T> ResponseBody from(final T info, final HttpStatus httpStatus, final HttpVersion httpVersion) {
        if (info.equals("/")) {
            return new ResponseBody("Hello world!".getBytes(), ContentType.HTML, httpStatus, httpVersion);
        }
        return new ResponseBody(getNotDefaultPathResponseBody((String) info),
                ContentType.findContentTypeByURI((String) info), httpStatus, httpVersion);
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

    public static String redirectResponse(final String page, final HttpVersion httpVersion) {
        return getLocationHeaderMessage(page, httpVersion);
    }

    private static String getLocationHeaderMessage(final String page, final HttpVersion httpVersion) {
        return String.join("\r\n",
                httpVersion.getVersion() + " " + HttpStatus.FOUND.getStatus() + " " + HttpStatus.FOUND.name() + " ",
                "Location: " + page,
                "");
    }

    public String getMessage() {
        return String.join("\r\n",
                httpVersion.getVersion() + " " + httpStatus.getStatus() + " " + httpStatus.name() + " ",
                "Content-Type: " + contentType.getType() + ";charset=" + CHARSET.name().toLowerCase() + " ",
                "Content-Length: " + data.length + " ",
                "",
                new String(data, CHARSET));
    }

    public static String redirectResponse(final String page, final HttpRequest httpRequest, final String sessionId) {
        if (httpRequest.hasCookie("JSESSIONID")) {
            return getLocationHeaderMessage(page, httpRequest.getHttpVersion());
        }
        return String.join("\r\n",
                httpRequest.getHttpVersion().getVersion() + " " + HttpStatus.FOUND.getStatus() + " "
                        + HttpStatus.FOUND.name() + " ",
                "Location: " + page,
                "Set-Cookie: " + "JSESSIONID=" + sessionId,
                "");
    }
}
