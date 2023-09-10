package org.apache.coyote.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String LINE_FEED = "\r\n";
    private static final String SPACE = " ";
    private static final String STATIC_DIRECTORY = "static";

    private String startLine;
    private Map<String, Header> headers = new HashMap<>();
    private String messageBody;

    public void updateStartLine(String startLine) {
        this.startLine = startLine;
    }

    public void updateMessageBody(String messageBody)  {
        this.messageBody = messageBody;
    }

    public void updateFileMessageBody(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + path);
        messageBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public void addHeader(String key, String value) {
        Header header = headers.computeIfAbsent(key, ignore -> new CommaSeperatedHeader());
        header.add(value);
    }

    public void addHeader(String key, List<String> values) {
        Header header = headers.computeIfAbsent(key, ignore -> new CommaSeperatedHeader());
        header.addAll(values);
    }

    public void addCookie(String key, String value) {
        Header header = headers.computeIfAbsent(HttpHeader.COOKIE.getName(), ignore -> new SemicolonSeperatedHeader());
        header.add(key + KEY_VALUE_DELIMITER + value);
    }

    public String joinResponse() {
        return startLine +
                joinCookie() +
                joinHeaderWithoutCookie() +
                LINE_FEED +
                messageBody;
    }

    private String joinHeaderWithoutCookie() {
        String headersWithoutCookie = this.headers.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(HttpHeader.COOKIE.getName()))
                .map(entry -> entry.getKey() + ": " + entry.getValue().getValues())
                .reduce((header1, header2) -> header1 + SPACE + LINE_FEED + header2)
                .orElse("");
        return headersWithoutCookie + SPACE + LINE_FEED;
    }

    private String joinCookie() {
        if (isStaticPath() || !headers.containsKey(HttpHeader.COOKIE.getName())) {
            return "";
        }
        String cookieHeader = headers.get(HttpHeader.COOKIE.getName()).getValues();
        String cookieHeaderResponse = Arrays.stream(cookieHeader.split("; "))
                .map(line -> "Set-Cookie: " + line)
                .reduce((cookie1, cookie2) -> cookie1 + SPACE + LINE_FEED + cookie2)
                .orElse("");

        return cookieHeaderResponse + SPACE + LINE_FEED;
    }

    private boolean isStaticPath() {
        String contentType = headers.get(HttpHeader.CONTENT_TYPE.getName()).getValues();
        return ContentType.isStaticFile(contentType);
    }

    public String getStartLine() {
        return startLine;
    }

    public Map<String, Header> getHeaders() {
        return headers;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
