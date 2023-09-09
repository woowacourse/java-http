package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.Constants.BLANK;
import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.EMPTY;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpExtensionType;
import org.apache.coyote.http11.common.HttpStatus;

public class HttpResponseGenerator {

    private static final String STATIC_FOLDER_PATH = "static";
    private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    public String generate(final HttpResponse httpResponse) throws IOException {
        final String responseBody = readStaticFile(httpResponse.getRedirect());
        return String.join(
                CRLF,
                generateHttpStatusLine(httpResponse),
                generateHttpHeadersLine(httpResponse, responseBody),
                EMPTY,
                responseBody
        );
    }

    private String readStaticFile(final String uri) throws IOException {
        final URL resource = classLoader.getResource(STATIC_FOLDER_PATH + uri);
        final File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String generateHttpStatusLine(final HttpResponse httpResponse) {
        final HttpStatus httpStatus = httpResponse.getHttpStatus();
        return String.join(
                BLANK,
                httpResponse.getHttpVersion().getField(),
                httpStatus.getCode(),
                httpStatus.name(),
                EMPTY
        );
    }

    private String generateHttpHeadersLine(final HttpResponse httpResponse, final String responseBody) {
        final StringBuilder stringBuilder = new StringBuilder()
                .append(generateContentTypeLine(HttpExtensionType.from(httpResponse.getRedirect())))
                .append(CRLF)
                .append(generateContentLengthLine(responseBody));

        final Headers headers = httpResponse.getHeaders();
        if (!headers.isEmpty()) {
            stringBuilder.append(CRLF).append(generateHeaderLine(headers));
        }

        final HttpCookie httpCookie = httpResponse.getHttpCookie();
        if (!httpCookie.isEmpty()) {
            stringBuilder.append(CRLF).append(generateCookieLine(httpCookie));
        }
        
        return stringBuilder.toString();
    }

    private String generateContentTypeLine(final HttpExtensionType httpExtensionType) {
        return "Content-Type: " + httpExtensionType.getContentType();
    }

    private CharSequence generateContentLengthLine(final String responseBody) {
        return "Content-Length: " + responseBody.getBytes().length + BLANK;
    }

    private String generateHeaderLine(final Headers headers) {
        return headers.getItems().entrySet().stream()
                .map(header -> header.getKey() + ": " + header.getValue())
                .collect(Collectors.joining(CRLF));
    }

    private String generateCookieLine(final HttpCookie httpCookie) {
        return "Set-Cookie: " + httpCookie.getItems().entrySet().stream()
                .map(cookie -> cookie.getKey() + "=" + cookie.getValue() + ";")
                .collect(Collectors.joining(BLANK));
    }
}
