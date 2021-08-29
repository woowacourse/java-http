package nextstep.jwp.http.request;

import nextstep.jwp.http.response.HttpResponse;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod parse(String method){
        return valueOf(method.toUpperCase());
    }

    public boolean isGet() {
        return this.equals(GET);
    }

    public boolean isPost(){
        return this.equals(POST);
    }
}
