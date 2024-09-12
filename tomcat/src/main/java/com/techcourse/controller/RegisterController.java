package com.techcourse.controller;

import static org.apache.catalina.webresources.FileResource.NOT_FOUND_RESOURCE_URI;

import java.util.Map;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.servlet.AbstractController;
import org.apache.tomcat.util.http.ResourceURI;
import org.apache.tomcat.util.http.parser.QueryStringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final ResourceURI REGISTER_RESOURCE_URI = new ResourceURI("/register.html");
    private static final ResourceURI REDIRECT_RESOURCE_URI = new ResourceURI("/index.html");

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.writeStaticResource(REGISTER_RESOURCE_URI);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> parsed = QueryStringParser.parse(request.httpBody().body());
        String account = parsed.get("account");
        String password = parsed.get("password");
        String email = parsed.get("email");
        User user = new User(account, password, email);
        if (user.isValid()) {
            InMemoryUserRepository.save(user);
            log.info("save user: {}", user);
            response.sendRedirect(REDIRECT_RESOURCE_URI);
            return;
        }
        log.error("account={}, password={}, email={}, 회원가입에 실패하였습니다.", account, password, email);
        response.sendRedirect(NOT_FOUND_RESOURCE_URI);
    }
}
