package org.apache.coyote.http11;

public enum RequestPathType {

    API,
    RESOURCE,
    ;

    public static RequestPathType reqeustPathToRequestPathType(String requestPath) {
        String[] split = requestPath.split("\\.");
        if (split.length == 1) {
            return API;
        }
        if (split.length == 2) {
            return RESOURCE;
        }
        throw new IllegalArgumentException("존재하지 않는 PathType입니다.");
    }

    public boolean isAPI() {
        return this == API;
    }

    public boolean isResource() {
        return this == RESOURCE;
    }
}
