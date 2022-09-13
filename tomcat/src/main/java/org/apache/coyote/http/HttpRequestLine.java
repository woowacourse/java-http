package org.apache.coyote.http;

import java.net.http.HttpClient.Version;

public class HttpRequestLine {

    private final HttpMethod httpMethod;
    private final String uri;
    private final Version version;


    public HttpRequestLine(final String requestLine) {
        final String[] parseRequest = requestLine.split(" ");
        this.httpMethod = HttpMethod.from(parseRequest[0]);
        this.uri = parseRequest[1];
        this.version = convertVersion(parseRequest[2]);
    }

    private Version convertVersion(final String version){
        if(version.equals("HTTP/1.1")){
            return Version.HTTP_1_1;
        }
        return Version.HTTP_2;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }
}
