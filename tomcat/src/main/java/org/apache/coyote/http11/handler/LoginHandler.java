package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.ResponseStatus;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        if (checkIsRegisterUser(request)) {
            List<String> headers = List.of(
                    String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getEndPoint()).getContentType()),
                    String.join(" ", "Content-Length:", String.valueOf("".getBytes().length)),
                    String.join(" ", "Location: index.html")
            );

            return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.FOUND, headers, "");

        }

        List<String> headers = List.of(
                String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getEndPoint()).getContentType()),
                String.join(" ", "Content-Length:", String.valueOf("".getBytes().length)),
                String.join(" ", "Location: 401.html")
        );
        return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.FOUND, headers, "");
    }

    private boolean checkIsRegisterUser(HttpRequest request) {
        Map<String, String> queryStrings = request.getQueryStrings();
        String account = queryStrings.get("account");
        Optional<User> userFindResult = InMemoryUserRepository.findByAccount(account);
        if (userFindResult.isEmpty()) {
            return false;
        }
        User user = userFindResult.get();

        String password = queryStrings.get("password");
        if (!user.checkPassword(password)) {
            return false;
        }
        log.info(user.toString());
        return true;
    }
}
