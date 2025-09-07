package org.apache.coyote.http11;

public record HttpUri(String uri) {
    public String getPath() {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            return uri.substring(0, index);
        }
        return uri;
    }

    public String getQueryString() {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            return uri.substring(index + 1);
        }
        return "";
    }
}
