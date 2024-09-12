package org.apache.coyote.http11.httpmessage.request;

import org.apache.coyote.http11.exception.IllegalHttpMessageException;

public record RequestLine (
        Method method,
        String target,
        String httpVersion
) {

    public static RequestLine parseFrom(String requestLineText) {
        String[] token = requestLineText.split(" ");
        if (token.length != 3) {
            throw new IllegalHttpMessageException("잘못된 헤더 형식입니다.");
        }
        return new RequestLine(Method.findByName(token[0]), token[1], token[2]);
    }

    public boolean isPost() {
        return method == Method.POST;
    }

    public boolean isStaticResourceRequest() {
        return target.contains(".css") ||
                target.contains(".html") ||
                target.contains(".js");
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method=" + method +
                ", target='" + target + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                '}';
    }
}
