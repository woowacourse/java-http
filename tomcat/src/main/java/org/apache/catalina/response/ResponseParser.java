package org.apache.catalina.response;

import org.apache.catalina.Cookie;
import org.apache.catalina.ResourceResolver;

public class ResponseParser {

    private final ResourceResolver resourceResolver;

    public ResponseParser() {
        this.resourceResolver = new ResourceResolver();
    }

    public String parse(HttpResponse response) {
        String responseBody = resourceResolver.resolve(response.getResource());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("HTTP/1.1 %d OK ", response.getStatusCode())).append("\r\n");

        Cookie cookie = response.getCookie();
        if (cookie != null) {
            stringBuilder.append("Set-Cookie: JSESSIONID=" + cookie.getValue("JSESSIONID") + "\r\n");
        }

        if (response.getStatusCode() == 302) {
            stringBuilder.append("Location: " + response.getResource() + "\r\n");
        }

        stringBuilder.append("Content-Length: " + responseBody.getBytes().length + " ").append("\r\n")
                .append("Content-Type: " + getContentType(response.getResource())).append("\r\n")
                .append("\r\n").append(responseBody);

        return stringBuilder.toString();
    }

    private String getContentType(String contentType) {
        if (contentType.endsWith(".css")) {
            return "text/css ";
        }
        return "text/html;charset=utf-8 ";
    }
}
