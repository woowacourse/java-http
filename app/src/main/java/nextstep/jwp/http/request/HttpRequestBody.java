package nextstep.jwp.http.request;

public interface HttpRequestBody {

    Object getAttribute(String key);
    String getBody();
}
