package nextstep.jwp.http;

import java.util.Optional;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpMethod;

public interface HttpRequest {

    HttpMethod getMethod();

    Optional<String> getQueryString();

    String getRequestURI();

    HttpHeaders getHeaders();

    String getHeader(String name);

    String getBody();

    String asString();
}
