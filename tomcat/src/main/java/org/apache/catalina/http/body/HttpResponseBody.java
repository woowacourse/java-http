package org.apache.catalina.http.body;

public class HttpResponseBody {

    private String value;

    public HttpResponseBody() {
        this.value = "";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
