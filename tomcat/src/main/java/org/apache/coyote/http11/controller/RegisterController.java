package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

public class RegisterController {


    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String INDEX_FILE = "index.html";
    private static final String REGISTER_FILE = "register.html";

    public ResponseEntity postRegister(final RequestBody requestBody) {
        final String account = requestBody.search("account");
        final String password = requestBody.search("password");
        final String email = requestBody.search("email");
        final User user = new User(account, password, email);

        try {
            InMemoryUserRepository.save(user);
        } catch (IllegalArgumentException e) {
            final String registerPath = DIRECTORY_SEPARATOR + REGISTER_FILE;
            return ResponseEntity.of(HttpStatus.BAD_REQUEST, registerPath);
        }

        final String indexPath = DIRECTORY_SEPARATOR + INDEX_FILE;
        return ResponseEntity.of(HttpStatus.FOUND, indexPath);
    }

    public ResponseEntity getRegister() {
        final String registerResourcePath = DIRECTORY_SEPARATOR + REGISTER_FILE;

        return ResponseEntity.of(HttpStatus.OK, registerResourcePath);
    }
}
