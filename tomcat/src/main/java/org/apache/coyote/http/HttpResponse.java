package org.apache.coyote.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String LINE_FEED = "\r\n";
    private static final String SPACE = " ";
    private static final String STATIC_DIRECTORY = "static";

    private String startLine;
    private Map<String, String> header = new HashMap<>();
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
        header.put(key, value);
    }

    public void addCookie(String key, String value) {
        if (!header.containsKey("Cookie")) {
            addHeader("Cookie", key + "=" + value);
            return;
        }
        addHeader("Cookie", joinExistedCookie() + "; " + key + "=" + value);
    }

    private String joinExistedCookie() {
        return header.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((cookie1, cookie2) -> cookie1 + "; " + cookie2)
                .orElse("");
    }

    public String joinResponse() {
        return startLine +
                joinCookie() +
                joinHeaderWithoutCookie() +
                LINE_FEED +
                messageBody;
    }

    private String joinHeaderWithoutCookie() {
        String headers = header.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("Cookie"))
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .reduce((header1, header2) -> header1 + SPACE + LINE_FEED + header2)
                .orElse("");
        return headers + SPACE + LINE_FEED;
    }

    private String joinCookie() {
        if (isStaticPath() || !header.containsKey("Cookie")) {
            return "";
        }
        String cookieHeader = mapCookies().entrySet().stream()
                .map(entry -> "Set-Cookie: " + entry.getKey() + "=" + entry.getValue())
                .reduce((cookie1, cookie2) -> cookie1 + SPACE + LINE_FEED + cookie2)
                .orElse("");
        return cookieHeader + SPACE + LINE_FEED;
    }

    private boolean isStaticPath() {
        String contentType = header.get("Content-Type");
        return ContentType.isStaticFile(contentType);
    }

    private Map<String, String> mapCookies() {
        Map<String, String> cookies = header.entrySet().stream()
                .filter(entry -> entry.getKey().equals("Cookie"))
                .map(entry -> entry.getValue().split("; "))
                .flatMap(Arrays::stream)
                .map(line -> line.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(line -> line[KEY_INDEX], line -> line[VALUE_INDEX]));
        return cookies;
    }

    public String getStartLine() {
        return startLine;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
