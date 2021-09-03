package nextstep.jwp.model;

import nextstep.jwp.model.httpmessage.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.model.httpmessage.request.HttpMethod.GET;
import static nextstep.jwp.model.httpmessage.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

class RequestLineTest {

    @DisplayName("요청의 첫번째 줄을 읽어온다. (GET /index.html)")
    @Test
    void createGetMethod() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        assertThat(requestLine.getMethod()).isEqualTo(GET);
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
    }

    @DisplayName("요청의 첫번째 줄을 읽어온다. (POST /login)")
    @Test
    void createPostMethod() {
        RequestLine requestLine = new RequestLine("POST /login.html HTTP/1.1");
        assertThat(requestLine.getMethod()).isEqualTo(POST);
        assertThat(requestLine.getPath()).isEqualTo("/login.html");
    }

    @DisplayName("요청의 첫번째 줄을 읽어온다. (GET /login?account=gugu&password=password)")
    @Test
    void createGetMethodParams() {
        RequestLine requestLine = new RequestLine("GET /login?account=gugu&password=password HTTP/1.1");
        assertThat(requestLine.getMethod()).isEqualTo(GET);
        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getParams()).hasSize(2);
        assertThat(requestLine.getParams()).containsEntry("account", "gugu");
        assertThat(requestLine.getParams()).containsEntry("password", "password");
    }
}