package org.apache.coyote.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpMessageBodyInfo;

public class HttpResponseHeader {

    private final Map<String, List<String>> values;
    private final Cookie cookie;

    public HttpResponseHeader() {
        this.values = new LinkedHashMap<>();
        this.cookie = new Cookie();
    }

    public void add(String name, String value) {
        if (HttpMessageBodyInfo.isContentType(name)) {
            addContentType(value);
            return;
        }
        if (HttpMessageBodyInfo.isContentLength(name)) {
            addContentLength(Integer.parseInt(value));
            return;
        }
        this.values.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    public void addCookie(String key, String value) {
        cookie.add(key, value);
        values.computeIfAbsent(HttpMessageBodyInfo.SET_COOKIE.getValue(), k -> new ArrayList<>()).add(key + "=" + value);
    }

    public void addContentType(String contentType) {
        List<String> contentTypes = new ArrayList<>();
        if (contentType.equals("text/html")) {
            contentTypes.add(contentType + ";charset=utf-8");
            values.put(HttpMessageBodyInfo.CONTENT_TYPE.getValue(), contentTypes);
            return;
        }
        contentTypes.add(contentType);
        values.put(HttpMessageBodyInfo.CONTENT_TYPE.getValue(), contentTypes);
    }

    public void addContentLength(int length) {
        List<String> contentLengths = new ArrayList<>();
        contentLengths.add(String.valueOf(length));
        values.put(HttpMessageBodyInfo.CONTENT_LENGTH.getValue(), contentLengths);
    }

    public void addLocation(String redirectUri) {
        List<String> locations = new ArrayList<>();
        locations.add(redirectUri);
        values.put(HttpMessageBodyInfo.LOCATION.getValue(), locations);
    }

    @Override
    public String toString() {
        return values.entrySet().stream()
                .map(entry -> {
                    List<String> values = entry.getValue();
                    if (values.size() == 1) {
                        return entry.getKey() + ": " + values.get(0) + " ";
                    }
                    return entry.getKey() + ": " + String.join("; ", values) + " ";
                })
                .collect(Collectors.joining("\r\n"));
    }
}
