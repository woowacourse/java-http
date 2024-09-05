package org.apache.coyote.http11.request;

class Http11RequestUriParser {

    private final Http11StartLineParser startLineParser;

    public Http11RequestUriParser(Http11StartLineParser startLineParser) {
        this.startLineParser = startLineParser;
    }

    /*
            https://www.rfc-editor.org/rfc/rfc2616#section-5 이 문서에서 이 메서드가 반환하는 것을 Request-URI라 지칭합니다.
        */
    String parseRequestURI(String requestMessage) {
        String startLine = startLineParser.parseStartLine(requestMessage);
        return startLine.split(" ")[1];
    }
}
