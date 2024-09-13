package org.apache.tomcat.util.http.body;

public record HttpBody(String body) {

    public static HttpBody none() {
        return new HttpBody("");
    }

    public String buildResponse() {
        return body;
    }
}
