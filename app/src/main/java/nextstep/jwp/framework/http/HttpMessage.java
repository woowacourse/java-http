package nextstep.jwp.framework.http;

public interface HttpMessage {

    HttpHeaders getHttpHeaders();

    String getBody();
}
