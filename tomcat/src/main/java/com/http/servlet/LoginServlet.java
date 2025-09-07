package com.http.servlet;

import com.http.enums.HttpStatus;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnAuthorizedException;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.catalina.util.FileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet implements HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        final Map<String, String> queryStrings = request.queryStrings();

        String account = queryStrings.get("account");
        String password = queryStrings.get("password");
        processLogin(account, password, response);

        final String fileName = request.requestStartLine().path() + ".html";
        final byte[] loginHtml = FileParser.loadStaticResourceByFileName(fileName);
        response.setBody(loginHtml);
    }

    private void processLogin(String account, String password, HttpResponse httpResponse) {
        if (account == null && password == null) {
            return;
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            throw new UnAuthorizedException("잘못된 인증입니다.");
        }

        log.info("user : {} ", user);
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.addHeader("Location", "/index.html");
    }
}
