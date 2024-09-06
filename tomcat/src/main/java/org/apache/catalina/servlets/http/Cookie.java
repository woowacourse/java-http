package org.apache.catalina.servlets.http;

public class Cookie {

    private String name;
    private String value;
    private String path = "/";

    private Cookie() {}

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name).append("=").append(value);
        sb.append("; path=").append(path);
        return sb.toString();
    }
}
