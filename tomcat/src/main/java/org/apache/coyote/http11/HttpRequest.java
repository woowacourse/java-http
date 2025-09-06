package org.apache.coyote.http11;

public record HttpRequest(
        String uri
) {
    public static HttpRequest parseByFirstLine(String firstLine) {
        return new HttpRequest(firstLine.split(" ")[1]);
    }
}
