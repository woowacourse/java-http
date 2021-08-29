package nextstep.jwp.web.http.request.body;

public interface HttpRequestBody {

    Object getAttribute(String key);
    String getBody();
}
