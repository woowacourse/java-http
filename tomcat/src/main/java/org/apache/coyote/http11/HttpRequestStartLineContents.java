package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestStartLineContents {

    private final String method;
    private final String url;
    private final String httpVersion;

    public HttpRequestStartLineContents(String method, String url, String httpVersion) {
        this.method = method;
        this.url = url;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestStartLineContents from(final BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();

        if (startLine == null) {
            throw new IllegalArgumentException("잘못된 입력입니다.");
        }

        final String[] strings = startLine.split(" ");

        return new HttpRequestStartLineContents(strings[0], strings[1], strings[2]);
    }

    public String getUrl() {
        return url;
    }
}
