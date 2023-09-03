package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.RequestMethod;
import org.apache.coyote.http11.common.ResponseStatus;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        System.out.println(request.getRequestMethod() == RequestMethod.POST);
        if (request.getRequestMethod() == RequestMethod.GET) {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("static/login.html");

            File file = new File(resource.getFile());
            String fileData = new String(Files.readAllBytes(file.toPath()));

            List<String> headers = List.of(
                    String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getEndPoint()).getContentType()),
                    String.join(" ", "Content-Length:", String.valueOf(fileData.getBytes().length))
            );

            return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.OK, headers, fileData);
        }
        if (request.getRequestMethod() == RequestMethod.POST) {
            if (checkIsRegisterUser(request)) {
                List<String> headers = List.of(
                        String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getEndPoint()).getContentType()),
                        String.join(" ", "Content-Length:", String.valueOf("".getBytes().length)),
                        String.join(" ", "Location: index.html")
                );

                return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.FOUND, headers, "");
            } else {
                List<String> headers = List.of(
                        String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getEndPoint()).getContentType()),
                        String.join(" ", "Content-Length:", String.valueOf("".getBytes().length)),
                        String.join(" ", "Location: 401.html")
                );

                return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.FOUND, headers, "");
            }
        }
        throw new UnsupportedOperationException("get, post만 가능합니다.");
    }

    private boolean checkIsRegisterUser(HttpRequest request) {
        Map<String, String> queryStrings = request.getQueryStrings();
        String account = queryStrings.get("account");
        System.out.println(account);
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
