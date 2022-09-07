package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.cookie.HttpCookie;

public class RequestHeaders {

    private Map<String, Object> values = new HashMap<>();

    public RequestHeaders(List<String> unparsedHeaders) {
        parseHeaders(unparsedHeaders);
    }

    private void parseHeaders(List<String> unparsedHeaders) {
        for (String unparsedHeader : unparsedHeaders) {
            if ("Cookie".equals(unparsedHeader.split(":")[0])) {
                values.put("Cookie", new HttpCookie(unparsedHeader.split(":")[1].strip()));
                continue;
            }
            values.put(unparsedHeader.split(":")[0], unparsedHeader.split(":")[1].strip());
        }

        if (!values.containsKey("Cookie")) {
            values.put("Cookie", HttpCookie.createNewCookie());
        }
    }

    private HttpCookie createCookie(String unparsedCookies) {
        return new HttpCookie(unparsedCookies);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public boolean hasSessionCookie() {
        HttpCookie httpCookie = (HttpCookie) values.get("Cookie");
        return httpCookie.hasSessionCookie();
    }

    public String getCookie() {
        HttpCookie httpCookie = (HttpCookie) values.get("Cookie");
        return httpCookie.getAllCookies();
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
                "values=" + values +
                '}';
    }
}
