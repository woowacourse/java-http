package org.apache.coyote.support;

public enum Method {
    GET, POST, PUT, DELETE;

    public boolean check(Method method) {
        return this.equals(method);
    }
}
