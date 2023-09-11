package org.apache.coyote.http11;

import org.apache.catalina.Session;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseParser {

    private static final String BLANK_DELIMITER = " ";
    private static final String HEADER_DELIMITER = ": ";
    private static final String LINE_DELIMITER = " \r\n";
    private static final String HTTP11_PROTOCOL = "HTTP/1.1";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";
    public static final String SESSION_HEADER = "JSESSIONID";

    public String generate(final HttpResponse httpResponse) throws IOException {
        String responseLine = findResponseLine(httpResponse);
        String responseBody = findResponseBody(httpResponse);
        String responseHeader = findResponseHeader(httpResponse, responseBody);
        return String.join(LINE_DELIMITER, responseLine,
                responseHeader,
                "\r\n",
                responseBody);
    }

    private String findResponseLine(final HttpResponse httpResponse) {
        String httpStatusCode = String.valueOf(httpResponse.getHttpStatus().getCode());
        String httpStatusMessage = httpResponse.getHttpStatus().getMessage();
        return String.join(BLANK_DELIMITER, HTTP11_PROTOCOL,
                httpStatusCode,
                httpStatusMessage);

    }

    private String findResponseBody(final HttpResponse httpResponse) throws IOException {
        String redirectUri = httpResponse.getRedirectUri();
        URL requestedFile = ClassLoader.getSystemClassLoader().getResource("static" + redirectUri);
        return new String(Files.readAllBytes(new File(requestedFile.getFile()).toPath()));
    }

    private String findResponseHeader(final HttpResponse httpResponse, final String responseBody) {
        Map<String, String> responseHeader = getResponseHeader(httpResponse, responseBody);
        return responseHeader.entrySet().stream()
                .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue())
                .collect(Collectors.joining(LINE_DELIMITER));
    }

    private Map<String, String> getResponseHeader(final HttpResponse httpResponse, final String responseBody) {
        Map<String, String> responseHeader = new LinkedHashMap<>();
        String redirectUri = httpResponse.getRedirectUri();
        if (!responseBody.isEmpty()) {
            ContentType contentType = ContentType.valueOfContentType(redirectUri.substring(redirectUri.lastIndexOf(".")+1));
            responseHeader.put(CONTENT_TYPE_HEADER, contentType.getContentType());
            responseHeader.put(CONTENT_LENGTH_HEADER, String.valueOf(responseBody.getBytes().length));
        }
        setSessionCookie(httpResponse, responseHeader);
        responseHeader.putAll(httpResponse.getHeaders());
        return responseHeader;
    }

    private void setSessionCookie(final HttpResponse httpResponse, final Map<String, String> responseHeader) {
        Session session = httpResponse.getSession();
        if (session.isFirstSent()) {
            responseHeader.put(SET_COOKIE_HEADER, SESSION_HEADER + "= " + session.getId() + ";");
        }
    }
}
