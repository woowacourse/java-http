package nextstep.jwp.web.http.request.body;

public interface HttpRequestBody<T> {

    T getAttribute(String key);

    String getBody();
}
