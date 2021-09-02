package nextstep.jwp.model;

public enum MethodType {

    GET, POST;

    public static boolean isGet(String requestMethod) {
        return requestMethod.equals(GET.name());
    }

    public static boolean isPost(String requestMethod) {
        return requestMethod.equals(POST.name());
    }
}
