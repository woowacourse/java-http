package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterServletTest {

    @Test
    @DisplayName("사용자 정보를 저장한다.")
    void saveUser() {
        RegisterServlet servlet = new RegisterServlet();
        byte[] requestBytes = """
                POST /register HTTP/1.1\r
                Content-Length: 38\r
                \r
                account=account&password=password&email=email
                """.trim().getBytes();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));
        HttpResponse response = new HttpResponse();
        servlet.service(request, response);
        assertThat(response.getHeader(HttpHeaders.LOCATION)).isEqualTo("/index.html");
    }
}
