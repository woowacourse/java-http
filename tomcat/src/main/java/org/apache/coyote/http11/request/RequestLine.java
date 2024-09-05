package org.apache.coyote.http11.request;

public record RequestLine (
        Method method,
        String target,
        String httpVersion
) {

    public static RequestLine parseFrom(String requestLineText) {
        String[] token = requestLineText.split(" ");
        return new RequestLine(Method.findByName(token[0]), token[1], token[2]);
    }
}
