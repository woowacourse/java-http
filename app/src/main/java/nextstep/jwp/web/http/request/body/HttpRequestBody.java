package nextstep.jwp.web.http.request.body;

import java.util.Set;

public interface HttpRequestBody<T> {

    T getAttribute(String key);
    String getBody();
}
