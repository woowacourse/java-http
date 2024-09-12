package org.apache.coyote.http11.message.request;

import java.util.StringTokenizer;

public class RequestLine {

    private final String method;
    private final String uri;
    private final String protocolVersion;

    public RequestLine(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        if (tokenizer.countTokens() != 3) {
            throw new IllegalArgumentException("Http Line 형식이 일치하지 않습니다.");
        }
        method = tokenizer.nextToken();
        uri = tokenizer.nextToken();
        protocolVersion = tokenizer.nextToken();
    }

    public boolean isGet() {
        return method.equals("GET");
    }

    public boolean isPost() {
        return method.equals("POST");
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return uri.split("\\?")[0];
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String toString() {
        return "HttpLine{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                '}';
    }
}
