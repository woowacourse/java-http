package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.common.RequestMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

public class RegisterHandler implements Handler {

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        if (request.getRequestMethod() == RequestMethod.GET) {
            String fileData = FileReader.readFile("/register.html");
            return ResponseEntity.ok(fileData, "/register.html");
        }

        if (request.getRequestMethod() == RequestMethod.POST) {
            Map<String, String> postData = request.getQueryStrings();
            saveUser(postData);
            return ResponseEntity.redirect("index.html");
        }

        throw new UnsupportedOperationException("get, post만 가능합니다");
    }

    private void saveUser(Map<String, String> postData) {
        String account = postData.get("account");
        String email = postData.get("email");
        String password = postData.get("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
