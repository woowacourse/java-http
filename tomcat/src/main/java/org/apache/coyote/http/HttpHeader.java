package org.apache.coyote.http;

import static org.apache.coyote.http.HeaderKey.CONTENT_LENGTH;
import static org.apache.coyote.http.HeaderKey.CONTENT_TYPE;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http.cookie.Cookies;
import org.apache.coyote.http.request.ContentType;
import org.apache.coyote.http.session.Session;

public class HttpHeader {

    private final Map<String, List<String>> header;
    private final Cookies cookies;

    private HttpHeader(Map<String, List<String>> header, Cookies cookies) {
        this.header = header;
        this.cookies = cookies;
    }

    public HttpHeader() {
        this(new HashMap<>(), Cookies.emptyCookies());
    }

    public static HttpHeader from(Map<String, List<String>> header) {
        Cookies cookies = Cookies.from(header);
        return new HttpHeader(header, cookies);
    }

    public Optional<String> getSessionId() {
        return cookies.getCookie(Session.COOKIE_KEY);
    }

    public Map<String, List<String>> getHeaders() {
        return new HashMap<>(header);
    }

    public Optional<String> getValue(String key) {
        List<String> values = getValues(key);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(values.get(0));
    }

    public int getContentLength() {
        return Integer.parseInt(getValue(CONTENT_LENGTH.value).orElse("0"));
    }

    public List<String> getValues(String key) {
        return new ArrayList<>(header.getOrDefault(key, Collections.emptyList()));
    }

    public void addValue(String key, String value) {
        List<String> values = header.computeIfAbsent(key, ignored -> new ArrayList<>());
        values.add(value);
    }

    public void setContentType(MediaType mediaType, Charset charset) {
        addValue(CONTENT_TYPE.value, ContentType.of(mediaType, charset).getValue());
    }

    public Optional<ContentType> getContentType() {
        return getValue(CONTENT_TYPE.value).map(ContentType::of);
    }
}
