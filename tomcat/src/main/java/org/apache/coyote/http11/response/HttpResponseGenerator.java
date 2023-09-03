package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.common.HttpStatus;

public class HttpResponseGenerator {

    public static final String CRLF = "\r\n";
    public static final String BLANK = " ";

    private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    public String generate(final ResponseEntity responseEntity) throws IOException {
        final String uri = responseEntity.getUri();
        if (uri.equals("/")) {
            return generateResponse(responseEntity, "Hello world!");
        }
        if (responseEntity.getHttpStatus() == HttpStatus.FOUND) {
            return generateRedirectResponse(responseEntity);
        }
        final String responseBody = readStaticFile(uri);
        return generateResponse(responseEntity, responseBody);
    }

    private String generateResponse(final ResponseEntity responseEntity, final String responseBody) {
        return String.join(
                CRLF,
                generateHttpStatusLine(responseEntity.getHttpStatus()),
                generateContentTypeLine(responseEntity.getUri()),
                generateContentLengthLine(responseBody),
                "",
                responseBody
        );
    }

    private String generateHttpStatusLine(final HttpStatus httpStatus) {
        return String.join(BLANK, "HTTP/1.1", httpStatus.getCode(), httpStatus.name(), "");
    }

    private String generateContentTypeLine(final String url) {
        if (url.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    private CharSequence generateContentLengthLine(final String responseBody) {
        return "Content-Length: " + responseBody.getBytes().length + BLANK;
    }

    private String readStaticFile(final String uri) throws IOException {
        final URL resource = classLoader.getResource("static" + uri);
        final File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String generateRedirectResponse(final ResponseEntity responseEntity) {
        final HttpStatus httpStatus = responseEntity.getHttpStatus();
        final String firstLine = String.join(BLANK, "HTTP/1.1", httpStatus.getCode(), httpStatus.name(), "");
        return String.join(
                CRLF,
                firstLine,
                "Location: " + responseEntity.getUri(),
                generateSetCookieLine(responseEntity)
        );
    }

    private String generateSetCookieLine(final ResponseEntity responseEntity) {
        final String jsessionid = responseEntity.getHttpCookie().get("JSESSIONID");
        if (jsessionid == null) {
            return "";
        }
        return "Set-Cookie: JSESSIONID=" + jsessionid + " ";
    }

}
