package org.apache.coyote.http11;

record HttpCookie(String name, String value) {

    String toHeaderValue() {
        return name + "=" + value;
    }
}
