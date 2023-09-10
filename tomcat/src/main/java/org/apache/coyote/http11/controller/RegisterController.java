package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_PAGE_URI = "/register.html";
    private static final String INDEX_PAGE_URI = "/index.html";

    @Override
    public ResponseEntity service(HttpRequest request) {
        HttpRequestStartLine httpRequestStartLine = request.getHttpRequestStartLine();
        HttpRequestBody httpRequestBody = request.getHttpRequestBody();
        HttpMethod httpMethod = httpRequestStartLine.getHttpMethod();

        if (httpMethod == HttpMethod.GET) {
            return ResponseEntity.builder()
                    .httpStatus(HttpStatus.OK)
                    .contentType(ContentType.HTML)
                    .location(REGISTER_PAGE_URI)
                    .build();
        }

        String account = httpRequestBody.find("account");
        String password = httpRequestBody.find("password");
        String email = httpRequestBody.find("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("{} user 회원가입 성공", account);
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.FOUND)
                .contentType(ContentType.HTML)
                .location(INDEX_PAGE_URI)
                .build();
    }
}
