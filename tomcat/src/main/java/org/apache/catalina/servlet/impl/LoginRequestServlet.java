package org.apache.catalina.servlet.impl;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.servlet.RequestServlet;
import org.apache.catalina.util.FileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestServlet implements RequestServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestServlet.class);

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        final Map<String, String> queryStrings = httpRequest.queryStrings();

        String account = queryStrings.get("account");
        String password = queryStrings.get("password");
        processLogin(account, password);

        final String fileName = httpRequest.startLine().path() + ".html";
        final byte[] loginHtml = FileParser.loadStaticResourceByFileName(fileName);
        httpResponse.setBody(loginHtml);
    }

    private void processLogin(String account, String password) {
        if (account == null || password == null) {
            throw new IllegalArgumentException("account와 password는 필수입니다.");
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (user.checkPassword(password)) {
            log.info("user : {} ", user);
        }
    }
}
