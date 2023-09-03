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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        System.out.println("handler: " + request.getRequestUri());
        URL resource = classLoader.getResource("static/login.html");

        File file = new File(resource.getFile());
        String fileData = new String(Files.readAllBytes(file.toPath()));

        List<String> headers = List.of(
                String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getRequestUri()).getContentType()),
                String.join(" ", "Content-Length:", String.valueOf(fileData.getBytes().length))
        );

        String account = request.findQueryStringValue("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        String password = request.findQueryStringValue("password");
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }

        return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.OK, headers, fileData);
    }
}
