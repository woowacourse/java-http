package com.spring.http.cookie;

public record HttpCookie(
        String name,
        String value
) {

    public boolean sameName(String name) {
        return this.name.equals(name);
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}

