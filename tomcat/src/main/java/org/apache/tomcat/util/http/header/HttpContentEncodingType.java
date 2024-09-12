package org.apache.tomcat.util.http.header;

public enum HttpContentEncodingType {

    UTF8(";charset=utf-8");

    private final String encoding;

    HttpContentEncodingType(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }
}
