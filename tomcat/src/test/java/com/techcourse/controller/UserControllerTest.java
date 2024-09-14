package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.servlet.DispatcherServlet;
import com.techcourse.servlet.RequestMapping;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.ServletContainer;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class UserControllerTest {

    private final RequestMapping requestMapping = new RequestMapping(Map.of(
            "/user", new UserController()
    ));
    private final List<Servlet> servlet = List.of(new DispatcherServlet(requestMapping));
    private final ServletContainer servletContainer = ServletContainer.init(servlet);

    @Test
    @DisplayName("GET '/user' 요청 시 비 로그인 상태일 경우 로그인 요청 메세지를 응답한다.")
    void notLoggedIn() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /user HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(servletContainer, socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 400 Bad Request ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 33 ",
                "",
                "로그인 상태가 아닙니다.");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET '/user' 요청 시 로그인 상태일 경우 사용자 정보를 응답한다.")
    void loggedIn() {
        // given
        User user = new User("account", "password", "email@gmail.com");
        InMemoryUserRepository.save(user);
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);


        String httpRequest = String.join("\r\n",
                "GET /user HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=" + session.getId(),
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(servletContainer, socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 84 ",
                "",
                "User: User{id=null, account='account', email='email@gmail.com', password='password'}");

        assertThat(socket.output()).isEqualTo(expected);
    }
}
