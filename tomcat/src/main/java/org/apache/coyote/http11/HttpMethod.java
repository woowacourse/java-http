package org.apache.coyote.http11;

public class HttpMethod {

    private final String name;

    public HttpMethod(final String name) {
        this.name = name;
    }

    public boolean isGet() {
        return name.equals("GET");
    }

    public boolean isPost() {
        return name.equals("POST");
    }

    public String getName() {
        return name;
    }
}
