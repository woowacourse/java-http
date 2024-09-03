package org.apache.coyote.http11;

record Http11Header(String key, String value) {

    @Override
    public String toString() {
        return "%s=%s".formatted(key, value);
    }
}
