package nextstep.jwp.http;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    HttpMethod() {
    }

    public boolean isGet() {
        return GET.name().equals(this.name());
    }

    public boolean isPost() {
        return POST.name().equals(this.name());
    }
}
