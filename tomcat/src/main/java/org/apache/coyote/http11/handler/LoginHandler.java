package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.RequestBody;
import org.apache.coyote.util.FileUtil;

public class LoginHandler implements RequestHandler {

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        if (httpRequest.getHttpMethod().isGet()) {
            return doGet(httpRequest);
        }
        if (httpRequest.getHttpMethod().isPost()) {
            return doPost(httpRequest);
        }
        return new ResponseEntity(HttpStatus.NOTFOUND, FileUtil.readAllBytes("/404.html"), ContentType.HTML);
    }

    private ResponseEntity doGet(HttpRequest httpRequest) {
        return new ResponseEntity(HttpStatus.OK, FileUtil.readAllBytes(httpRequest.getRequestUri() + ".html"),
                ContentType.HTML);
    }

    private ResponseEntity doPost(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();

        if (!requestBody.containsKey("account") || !requestBody.containsKey("password")) {
            return new ResponseEntity(HttpStatus.NOTFOUND, FileUtil.readAllBytes("/404.html"), ContentType.HTML);
        }

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(requestBody.get("account"));
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            if (user.checkPassword(requestBody.get("password"))) {
                return new ResponseEntity(HttpStatus.FOUND, ContentType.HTML, new HttpHeaders(Map.of("Location", "/index.html")));
            }
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED, FileUtil.readAllBytes("/401.html"), ContentType.HTML);
    }
}
