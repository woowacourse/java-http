package org.apache.coyote.http11.httpmessage.request;

import org.apache.coyote.http11.exception.IllegalHttpMessageException;

public record HttpRequestLine(
        HttpMethod httpMethod,
        String target,
        String httpVersion
) {

    public static HttpRequestLine parseFrom(String httpRequestLineText) {
        String[] token = httpRequestLineText.split(" ");
        if (token.length != 3) {
            throw new IllegalHttpMessageException("잘못된 헤더 형식입니다.");
        }
        return new HttpRequestLine(HttpMethod.findByName(token[0]), token[1], token[2]);
    }

    public boolean isPost() {
        return httpMethod == HttpMethod.POST;
    }

    public boolean isStaticResourceRequest() {
        return target.contains(".css") ||
                target.contains(".html") ||
                target.contains(".js");
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "httpMethod=" + httpMethod +
                ", target='" + target + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                '}';
    }
}
