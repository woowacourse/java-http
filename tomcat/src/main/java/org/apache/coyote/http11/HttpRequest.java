package org.apache.coyote.http11;

public record HttpRequest(
        String httpMethod,
        String uri,
        double httpVersion
) {
    public static HttpRequest parseByFirstLine(String firstLine) {
        String[] splitFirstLine = firstLine.split(" ");
        return new HttpRequest(
                splitFirstLine[0],
                splitFirstLine[1],
                Double.parseDouble(splitFirstLine[2].split("/")[1])
        );
    }
}
