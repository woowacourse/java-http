package org.apache.coyote.handler.dynamichandler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegisterHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private final static String ACCOUNT = "account";
    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (HttpMethod.GET == HttpMethod.valueOf(httpRequest.method())) {
            return handleGetMapping(httpRequest);
        }
        return handlePostMapping(httpRequest);
    }

    private HttpResponse handleGetMapping(HttpRequest httpRequest) {
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource("static" + httpRequest.uri() + ".html").getPath());
            byte[] bytes = Files.readAllBytes(path);
            String body = new String(bytes);
            HttpResponse response = new HttpResponse();
            response.setHttpVersion(HttpVersion.HTTP11.value());
            response.setHttpStatus(HttpStatus.OK);
            response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_HTML.value())
                    .setHeader(HttpHeader.CONTENT_LENGTH, body.getBytes().length + " ");
            response.setBody(body);
            return response;
        } catch (IOException e) {
        }
        return null;
    }

    private HttpResponse handlePostMapping(HttpRequest httpRequest) {
        String body = httpRequest.body();
        if (body != null) {
            String account = parseAccount(body);
            String email = parseEmail(body);
            String password = parsePassword(body);
            if (account != null && email != null && password != null) {
                InMemoryUserRepository.save(new User(account, password, email));
                return handleRedirectPage();
            }
            //TODO 예외처리 필요(계정 or 이메일 or 패스워드가 잘못되었을 시)
            return null;
        }
        return null;
    }

    private String parseAccount(String quiryString) {
        String[] accountInfo = quiryString.substring(0, quiryString.indexOf('&')).split("=");
        if (ACCOUNT.equals(accountInfo[0])) {
            return accountInfo[1];
        }
        return null;
    }

    private String parseEmail(String quiryString) {
        String[] emailInfo = quiryString.substring(quiryString.indexOf('&') + 1, quiryString.lastIndexOf('&')).split("=");
        if (EMAIL.equals(emailInfo[0])) {
            return emailInfo[1];
        }
        return null;
    }

    private String parsePassword(String quiryString) {
        String[] passwordInfo = quiryString.substring(quiryString.lastIndexOf('&') + 1).split("=");
        if (PASSWORD.equals(passwordInfo[0])) {
            return passwordInfo[1];
        }
        return null;
    }

    private HttpResponse handleRedirectPage() {
        HttpResponse response = new HttpResponse();
        response.setHttpVersion(HttpVersion.HTTP11.value());
        response.setHttpStatus(HttpStatus.FOUND);
        response.setHeader(HttpHeader.LOCATION, "/index.html");
        return response;
    }
}
