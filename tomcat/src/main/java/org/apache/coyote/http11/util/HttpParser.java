package org.apache.coyote.http11.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.HttpMethod;

public class HttpParser {

    private HttpMethod httpMethod;
    private String httpUrl;

    public HttpParser(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String[] startLine = bufferedReader.readLine().split(" ");
            httpMethod = HttpMethod.from(startLine[0]);
            httpUrl = startLine[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getHttpUrl() {
        return httpUrl;
    }
}
