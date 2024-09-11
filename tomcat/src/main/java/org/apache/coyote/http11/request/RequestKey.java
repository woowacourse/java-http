package org.apache.coyote.http11.request;

import org.apache.coyote.HttpMethod;

public record RequestKey(HttpMethod method, String path) {

    public static RequestKey ofGet(String path) {
        return new RequestKey(HttpMethod.GET, path);
    }

    public static RequestKey ofPost(String path) {
        return new RequestKey(HttpMethod.POST, path);
    }
}
