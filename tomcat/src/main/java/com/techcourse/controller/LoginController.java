package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(MyHttpRequest request, MyHttpResponse response) throws Exception {
        authenticate(request, response);
    }

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws Exception {
        Session session = request.getSession(false);
        if (session != null && getUser(session) != null) {
            response.setStatusCode(StatusCode.FOUND);
            response.setContentType(ContentType.HTML);
            response.sendRedirect("index.html");
            return;
        }

        response.setStatusCode(StatusCode.OK);
        response.setContentType(request.getContentType());
        final var responseBody = readStaticResource(request, "Hello world!");
        response.writeBody(responseBody);
    }

    private void authenticate(MyHttpRequest httpRequest, MyHttpResponse httpResponse) throws IOException {
        Map<String, String> params = httpRequest.getFormParameters();
        Optional<User> foundUser = findUserByAccount(params.get("account"));
        if (foundUser.isEmpty()) {
            log.info("authenticate failed: user not found");
            httpResponse.setStatusCode(StatusCode.FOUND);
            httpResponse.setContentType(ContentType.HTML);
            httpResponse.sendRedirect("401.html");
            return;
        }

        if (foundUser.get().checkPassword(params.get("password"))) {
            log.info("user matched={}", foundUser.get());
            final var session = httpRequest.getSession(true);
            if (httpRequest.isNewSession()) {
                httpResponse.addHeader("Set-Cookie", String.join("=", "JSESSIONID", session.getId()));
            }
            session.setAttribute("user", foundUser.get());
            httpResponse.setStatusCode(StatusCode.FOUND);
            httpResponse.setContentType(ContentType.HTML);
            httpResponse.sendRedirect("index.html");
            return;
        }
        log.info("authenticate failed: incorrectly password");
        httpResponse.setStatusCode(StatusCode.FOUND);
        httpResponse.setContentType(ContentType.HTML);
        httpResponse.sendRedirect("401.html");
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }

    private static Optional<User> findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account);
    }

    private static String readStaticResource(MyHttpRequest httpRequest, String defaultContent)
            throws IOException, URISyntaxException {
        URL fileUrl = Http11Processor.class
                .getClassLoader()
                .getResource(httpRequest.getResourcePath());
        File file = new File(Objects.requireNonNull(fileUrl).toURI());
        if (file.isFile()) {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        }
        return defaultContent;
    }
}
