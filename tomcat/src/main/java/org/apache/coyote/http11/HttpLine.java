package org.apache.coyote.http11;

import java.util.StringTokenizer;

public class HttpLine {

    private final String method;
    private final String path;
    private final String protocolVersion;

    public HttpLine(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        if (tokenizer.countTokens() != 3) {
            throw new IllegalArgumentException("Http Line 형식이 일치하지 않습니다.");
        }
        method = tokenizer.nextToken();
        path = tokenizer.nextToken();
        protocolVersion = tokenizer.nextToken();
    }

    public boolean isGet() {
        return method.equals("GET");
    }

    public boolean isPost() {
        return method.equals("POST");
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String toString() {
        return "HttpLine{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                '}';
    }
}
