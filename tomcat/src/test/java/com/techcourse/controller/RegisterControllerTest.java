package com.techcourse.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.http.HttpMethod;
import org.apache.tomcat.util.http.HttpRequestLine;
import org.apache.tomcat.util.http.HttpVersion;
import org.apache.tomcat.util.http.ResourceURI;
import org.apache.tomcat.util.http.body.HttpBody;
import org.apache.tomcat.util.http.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    @DisplayName("회원가입 페이지를 반환한다.")
    @Test
    void serviceGET() throws Exception {
        RegisterController controller = new RegisterController();
        HttpRequest httpRequest = new HttpRequest(new HttpRequestLine(HttpMethod.GET, new ResourceURI("/register"), HttpVersion.HTTP11),
                new HttpHeaders(), HttpBody.none());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP11);

        controller.service(httpRequest, httpResponse);

        String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 4319\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "\r\n" +
                StandardRoot.getResource(new ResourceURI("/register.html")).getContent();

        assertThat(httpResponse.buildResponse()).isEqualTo(expected);
    }

    @DisplayName("회원가입에 성공한다.")
    @Test
    void servicePOST() throws Exception {
        RegisterController controller = new RegisterController();
        HttpRequest httpRequest = new HttpRequest(new HttpRequestLine(HttpMethod.POST, new ResourceURI("/register"), HttpVersion.HTTP11),
                new HttpHeaders(), new HttpBody("account=hotea&password=password&email=hotea%40woowahan.com"));
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP11);

        controller.service(httpRequest, httpResponse);

        String expected = "HTTP/1.1 302 FOUND\r\n" +
                "Content-Length: 5564\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Location: /index.html\r\n" +
                "\r\n" +
                StandardRoot.getResource(new ResourceURI("/index.html")).getContent();

        assertThat(httpResponse.buildResponse()).isEqualTo(expected);
    }

    @DisplayName("적절하지 못한 정보로 회원가입 요청 시 실패한다.")
    @Test
    void failServicePOST() throws Exception {
        RegisterController controller = new RegisterController();
        HttpRequest httpRequest = new HttpRequest(new HttpRequestLine(HttpMethod.POST, new ResourceURI("/register"), HttpVersion.HTTP11),
                new HttpHeaders(), new HttpBody("account=&password=&email="));
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP11);

        controller.service(httpRequest, httpResponse);

        String expected = "HTTP/1.1 302 FOUND\r\n" +
                "Content-Length: 2426\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Location: /404.html\r\n" +
                "\r\n" +
                StandardRoot.getResource(new ResourceURI("/404.html")).getContent();

        assertThat(httpResponse.buildResponse()).isEqualTo(expected);
    }
}
