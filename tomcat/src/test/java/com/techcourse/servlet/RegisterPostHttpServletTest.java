package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import java.io.ByteArrayInputStream;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterPostHttpServletTest {

    @Test
    @DisplayName("POST이면서 /register 경로로 요청이 들어오면 요청을 처리한다.")
    void canService() {
        RegisterPostHttpServlet handler = new RegisterPostHttpServlet();
        byte[] requestBytes = "POST /register HTTP/1.1".getBytes();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));
        assertThat(handler.canService(request)).isTrue();
    }

    @Test
    @DisplayName("사용자 정보를 저장한다.")
    void saveUser() {
        RegisterPostHttpServlet handler = new RegisterPostHttpServlet();
        byte[] requestBytes = """
                POST /register HTTP/1.1\r
                Content-Length: 38\r
                \r
                account=account&password=password&email=email
                """.getBytes();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));
        HttpResponse response = new HttpResponse();
        handler.service(request, response);
        assertAll(
                () -> assertThat(response.getHeader(HttpHeaders.LOCATION)).isEqualTo("/index.html"),
                () -> assertThat(InMemoryUserRepository.findByAccount("account")).isPresent()
        );
    }
}
