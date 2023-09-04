package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.common.RequestMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.RequestData;
import org.apache.coyote.http11.http.ResponseEntity;

import java.io.IOException;

public class RegisterHandler implements Handler {

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        if (request.getRequestMethod() == RequestMethod.GET) {
            String fileData = FileReader.readFile("/register.html");
            return ResponseEntity.ok(fileData, ContentType.HTML);
        }

        if (request.getRequestMethod() == RequestMethod.POST) {
            RequestData requestData = request.getRequestData();
            saveUser(requestData);
            return ResponseEntity.redirect("index.html");
        }

        throw new UnsupportedOperationException("get, post만 가능합니다");
    }

    private void saveUser(RequestData requestData) {
        String account = requestData.find("account");
        String email = requestData.find("email");
        String password = requestData.find("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
