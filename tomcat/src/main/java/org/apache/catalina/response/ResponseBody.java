package org.apache.catalina.response;

public class ResponseBody {

    private final String uri;

    public ResponseBody(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
