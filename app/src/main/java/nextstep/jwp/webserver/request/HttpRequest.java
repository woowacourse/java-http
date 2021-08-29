package nextstep.jwp.webserver.request;

public interface HttpRequest {

    HttpMethod httpMethod();

    String httpUrl();

    String getAttribute(String key);
}
