package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

public class HttpResponseGenerator {

    public static final String CRLF = "\r\n";
    public static final String BLANK = " ";
    public static final String CSS_FILE_SUFFIX = ".css";
    public static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    public String generate(final HttpResponse httpResponse) throws IOException {
        final String location = httpResponse.getLocation();
        if (location.equals("/")) {
            return generateResponse(httpResponse, DEFAULT_RESPONSE_BODY);
        }
        if (httpResponse.getHttpStatus() == HttpStatus.FOUND) {
            return generateRedirectResponse(httpResponse);
        }
        final String responseBody = readStaticFile(location);
        return generateResponse(httpResponse, responseBody);
    }

    private String generateResponse(final HttpResponse httpResponse, final String responseBody) {
        return String.join(
                CRLF,
                generateHttpStatusLine(httpResponse.getProtocol(), httpResponse.getHttpStatus()),
                generateContentTypeLine(httpResponse.getLocation()),
                generateContentLengthLine(responseBody),
                "",
                responseBody
        );
    }

    private String generateHttpStatusLine(final String protocol, final HttpStatus httpStatus) {
        return String.join(BLANK, protocol, httpStatus.getCode(), httpStatus.name(), "");
    }

    private String generateContentTypeLine(final String location) {
        if (location.endsWith(CSS_FILE_SUFFIX)) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    private CharSequence generateContentLengthLine(final String responseBody) {
        return "Content-Length: " + responseBody.getBytes().length + BLANK;
    }

    private String readStaticFile(final String location) throws IOException {
        final URL resource = classLoader.getResource("static" + location);
        final File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String generateRedirectResponse(final HttpResponse httpResponse) {
        final HttpStatus httpStatus = httpResponse.getHttpStatus();
        final String firstLine = String.join(BLANK, "HTTP/1.1", httpStatus.getCode(), httpStatus.name(), "");
        return String.join(
                CRLF,
                firstLine,
                "Location: " + httpResponse.getLocation(),
                generateSetCookieLine(httpResponse)
        );
    }

    private String generateSetCookieLine(final HttpResponse httpResponse) {
        Optional<String> cookieOption = httpResponse.getHttpCookie().get("JSESSIONID");
        if (cookieOption.isEmpty()) {
            return null;
        }
        final String jsessionid = cookieOption.get();
        if (jsessionid == null) {
            return "";
        }
        return "Set-Cookie: JSESSIONID=" + jsessionid + " ";
    }

}
