package nextstep.jwp.http;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.context.ApplicationContext;
import nextstep.jwp.http.message.HttpCookies;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpMethod;

public interface HttpRequest {

    HttpMethod getMethod();

    Optional<String> getQueryString();

    Map<String, String> getQueryStringAsMap();

    String getRequestURI();

    HttpHeaders getHeaders();

    String getHeader(String name);

    String getBody();

    HttpCookies getCookies();

    String asString();

    ApplicationContext getApplicationContext();

    void setApplicationContext(ApplicationContext applicationContext);
}
