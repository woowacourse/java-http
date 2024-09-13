package org.apache.coyote.http11.request;

class Http11MethodParser {

    private final Http11StartLineParser startLineParser;

    Http11MethodParser(Http11StartLineParser startLineParser) {
        this.startLineParser = startLineParser;
    }

    Http11Method parseMethod(String requestMessage) {
        String startLine = startLineParser.parseStartLine(requestMessage);
        String rawMethod = startLine.split(" ")[0];
        try {
            return Http11Method.valueOf(rawMethod.toUpperCase());
        } catch (Exception e) {
            return Http11Method.GET;
        }
    }
}