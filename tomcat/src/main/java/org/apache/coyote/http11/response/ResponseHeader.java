package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.cookie.Cookie;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader {
    private final Map<String, String> responseHeader;

    private ResponseHeader(final Map<String, String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public static ResponseHeader basic(final RequestHeader requestHeader, final ResponseBody responseBody) {
        final Map<String, String> result = new LinkedHashMap<>();
       /* if (requestHeader.containsKey("Cookie")) {
            final Cookies cookies = Cookies.from(requestHeader.getHeaderValue("Cookie"));
            cookies.getCookieOf("JSESSIONID").ifPresent(
                    (cookie) -> result.put("Set-Cookie", cookie.toString())
            );
        }*/
        result.put("Content-Type", responseBody.getContentType().getContentType() + ";charset=utf-8");
        result.put("Content-Length", String.valueOf(responseBody.getContent().getBytes().length));
        return new ResponseHeader(result);
    }

    public static ResponseHeader redirect(final String redirectPath) {
        final Map<String, String> result = new LinkedHashMap<>();
        result.put("Location", redirectPath);
        return new ResponseHeader(result);
    }

    public void addCookie(final Cookie cookie) {
        responseHeader.put("Set-Cookie", cookie.toString());
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : responseHeader.entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue())
                    .append(" ")
                    .append("\r\n");
        }
        return result.toString();
    }
}
