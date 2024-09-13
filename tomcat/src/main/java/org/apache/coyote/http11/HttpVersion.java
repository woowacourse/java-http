package org.apache.coyote.http11;

enum HttpVersion {

    HTTP_1_1("HTTP/1.1"),
    ;

    private final String versionName;

    HttpVersion(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        return versionName;
    }
}
