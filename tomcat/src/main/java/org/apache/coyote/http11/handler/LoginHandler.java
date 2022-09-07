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
import org.apache.coyote.util.FileUtil;

public class LoginHandler implements RequestHandler {

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        Map<String, String> queryParams = httpRequest.getQueryParams();
        if (queryParams.isEmpty()) {
            return new ResponseEntity(HttpStatus.OK, FileUtil.readAllBytes(httpRequest.getRequestUri() + ".html"),
                    ContentType.HTML);
        }
        if (!queryParams.containsKey("account") || !queryParams.containsKey("password")) {
            throw new IllegalArgumentException("No Exist Parameter");
        }

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(queryParams.get("account"));
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            if (user.checkPassword(queryParams.get("password"))) {
                return new ResponseEntity(HttpStatus.FOUND, FileUtil.readAllBytes("/index.html"), ContentType.HTML,
                        new HttpHeaders(Map.of("Location", "/index.html")));}
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED, FileUtil.readAllBytes("/401.html"), ContentType.HTML);
    }
}
