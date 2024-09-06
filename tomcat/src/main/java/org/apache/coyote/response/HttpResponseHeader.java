package org.apache.coyote.response;

public class HttpResponseHeader {

    private String name;
    private String value;

    public HttpResponseHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
