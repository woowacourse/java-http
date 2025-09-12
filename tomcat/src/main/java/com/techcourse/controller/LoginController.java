package com.techcourse.controller;

import org.apache.catalina.Session;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.StaticFileHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final StaticFileHandler staticFileHandler = new StaticFileHandler();

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        Session session = request.getSession(false);
        
        if (session != null) {
            User user = getUser(session);
            if (user != null) {
                return HttpResponse.redirect("/index.html");
            }
        }
        
        try {
            String resolvedPath = resolveFilePath("/login");
            return generateHttpResponse(resolvedPath);
        } catch (IOException e) {
            log.error("로그인 페이지 로드 실패", e);
            return HttpResponse.notFound();
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        
        if (account == null || password == null) {
            return HttpResponse.redirect("/401.html");
        }
        
        return processLoginCredentials(account, password, request);
    }

    private HttpResponse processLoginCredentials(final String account, final String password, final HttpRequest request) {
        return InMemoryUserRepository.findByAccount(account)
            .filter(user -> user.checkPassword(password))
            .map(user -> {
                Session session = request.getSession(true);
                session.setAttribute("user", user);
                String setCookieHeader = HttpCookie.createJSessionIdSetCookieHeader(session.getId());
                HttpResponse response = HttpResponse.redirect("/index.html");
                response.addHeader("Set-Cookie", setCookieHeader);
                log.info("로그인 성공: {}", user);
                return response;
            })
            .orElseGet(() -> {
                log.info("로그인 실패 - account: {}, password: {}", account, password);
                return HttpResponse.redirect("/401.html");
            });
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }

    private String resolveFilePath(final String requestPath) {
        String htmlPath = requestPath + ".html";
        if (staticFileHandler.exists(htmlPath)) {
            return htmlPath;
        }
        return requestPath;
    }

    private HttpResponse generateHttpResponse(final String requestPath) throws IOException {
        if (staticFileHandler.exists(requestPath)) {
            String fileContent = staticFileHandler.readFile(requestPath);
            MimeType mimeType = staticFileHandler.getContentType(requestPath);
            return HttpResponse.ok(fileContent, mimeType);
        } else {
            return HttpResponse.notFound();
        }
    }
}