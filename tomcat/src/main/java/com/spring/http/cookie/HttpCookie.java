package com.spring.http.cookie;

public record HttpCookie(
        String name,
        String value
) {

    public HttpCookie {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Cookie name cannot be null or empty");
        }

        if (value == null) {
            throw new IllegalArgumentException("Cookie value cannot be null");
        }
    }

    public boolean sameName(String name) {
        return this.name.equals(name);
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}

