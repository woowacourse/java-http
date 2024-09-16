package com.techcourse.controller;

import java.util.Map;
import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.http.HttpMethod;
import org.apache.tomcat.util.http.HttpRequestLine;
import org.apache.tomcat.util.http.HttpVersion;
import org.apache.tomcat.util.http.ResourceURI;
import org.apache.tomcat.util.http.body.HttpBody;
import org.apache.tomcat.util.http.header.HttpCookie;
import org.apache.tomcat.util.http.header.HttpHeaderType;
import org.apache.tomcat.util.http.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class LoginControllerTest {
    @DisplayName("로그인 페이지를 반환한다.")
    @Test
    void serviceGET() throws Exception {
        LoginController controller = new LoginController();
        HttpRequest httpRequest = new HttpRequest(new HttpRequestLine(HttpMethod.GET, new ResourceURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(), HttpBody.none());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP11);

        controller.service(httpRequest, httpResponse);

        String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 3797\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "\r\n" +
                StandardRoot.getResource(new ResourceURI("/login.html")).getContent();

        assertThat(httpResponse.buildResponse()).isEqualTo(expected);
    }

    @DisplayName("이미 로그인한 사용자의 경우 메인 페이지를 반환한다.")
    @Test
    void serviceRedirectionGET() throws Exception {
        Session session = new Session();
        new SessionManager().add(session);
        LoginController controller = new LoginController();
        HttpRequest httpRequest = new HttpRequest(new HttpRequestLine(HttpMethod.GET, new ResourceURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(Map.of(HttpHeaderType.COOKIE, HttpCookie.getCookieString(session))), HttpBody.none());
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

    @DisplayName("로그인에 성공한다.")
    @Test
    void servicePOST() throws Exception {
        LoginController controller = new LoginController();
        HttpRequest httpRequest = new HttpRequest(new HttpRequestLine(HttpMethod.POST, new ResourceURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(), new HttpBody("account=gugu&password=password"));
        Session session = new Session();
        HttpRequest spyRequest = spy(httpRequest);
        when(spyRequest.getSession(true)).thenReturn(session);
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP11);

        controller.service(spyRequest, httpResponse);

        String expected = "HTTP/1.1 302 FOUND\r\n" +
                "Set-Cookie: JSESSIONID=" + session.getSessionId() + "\r\n" +
                "Content-Length: 5564\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Location: /index.html\r\n" +
                "\r\n" +
                StandardRoot.getResource(new ResourceURI("/index.html")).getContent();

        assertThat(httpResponse.buildResponse()).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 유저 정보로 로그인 요청 시 실패한다.")
    @Test
    void failServicePOSTRedirect404() throws Exception {
        LoginController controller = new LoginController();
        HttpRequest httpRequest = new HttpRequest(new HttpRequestLine(HttpMethod.POST, new ResourceURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(), new HttpBody("account=a&password=password"));
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

    @DisplayName("일치하지 않는 비밀번호로 로그인 요청 시 로그인 요청 시 실패한다.")
    @Test
    void failServicePOSTRedirect401() throws Exception {
        LoginController controller = new LoginController();
        HttpRequest httpRequest = new HttpRequest(new HttpRequestLine(HttpMethod.POST, new ResourceURI("/login"), HttpVersion.HTTP11),
                new HttpHeaders(), new HttpBody("account=gugu&password=pa"));
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP11);

        controller.service(httpRequest, httpResponse);

        String expected = "HTTP/1.1 302 FOUND\r\n" +
                "Content-Length: 2426\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Location: /401.html\r\n" +
                "\r\n" +
                StandardRoot.getResource(new ResourceURI("/401.html")).getContent();

        assertThat(httpResponse.buildResponse()).isEqualTo(expected);
    }
}
