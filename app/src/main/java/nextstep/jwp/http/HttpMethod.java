package nextstep.jwp.http;

public enum HttpMethod {
    GET, POST;

    HttpMethod() {
    }

    public static boolean isPost(final String httpMethod) {
        return GET.name().equals(httpMethod.toUpperCase());
    }

    public static boolean isGet(final String httpMethod) {
        return POST.name().equals(httpMethod.toUpperCase());
    }
}
