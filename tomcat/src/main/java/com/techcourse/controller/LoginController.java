package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeaders;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public static HttpResponse login(HttpRequest httpRequest) throws URISyntaxException, IOException {
        FileReader fileReader = FileReader.getInstance();
        String filePath = "/login";
        HttpStatusCode statusCode = HttpStatusCode.OK;
        if (!httpRequest.isParameterEmpty()) {
            String account = httpRequest.getQueryParameter("account");
            String password = httpRequest.getQueryParameter("password");
            try {
                User foundUser = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException(account + "는 존재하지 않는 계정입니다."));
                if (foundUser.checkPassword(password)) {
                    log.info("user : " + foundUser);
                    statusCode = HttpStatusCode.FOUND;
                    filePath = "/index.html";
                }
            } catch (IllegalArgumentException e) {
                filePath = "/401.html";
                statusCode = HttpStatusCode.UNAUTHORIZED;
            }
        }

        HttpResponseBody httpResponseBody = new HttpResponseBody(
                fileReader.readFile(filePath));

        HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders(new HashMap<>());
        httpResponseHeaders.setContentType(httpRequest);
        httpResponseHeaders.setContentLength(httpResponseBody);
        return new HttpResponse(statusCode, httpResponseHeaders, httpResponseBody);
    }
}
