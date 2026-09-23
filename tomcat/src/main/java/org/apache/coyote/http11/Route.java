package org.apache.coyote.http11;

enum Route {

    HOME(HttpMethod.GET, "/"),
    LOGIN_PAGE(HttpMethod.GET, "/login"),
    LOGIN(HttpMethod.POST, "/login"),
    REGISTER_PAGE(HttpMethod.GET, "/register"),
    REGISTER(HttpMethod.POST, "/register"),
    STATIC_RESOURCE(HttpMethod.GET, null),
    NOT_FOUND(null, null);

    private final HttpMethod method;
    private final String path;

    Route(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    static Route find(HttpMethod method, String path) {
        for (Route route : values()) {
            if (route.path != null && route.method == method && route.path.equals(path)) {
                return route;
            }
        }
        if (method == HttpMethod.GET) {
            return STATIC_RESOURCE;
        }
        return NOT_FOUND;
    }
}
