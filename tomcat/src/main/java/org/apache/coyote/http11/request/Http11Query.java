package org.apache.coyote.http11.request;

record Http11Query(String key, String value) {

    @Override
    public String toString() {
        return "%s=%s".formatted(key, value);
    }
}
