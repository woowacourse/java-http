package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.QueryParamsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterPostResponseMaker implements ResponseMaker {

    private static final Logger log = LoggerFactory.getLogger(LoginGetResponseMaker.class);

    @Override
    public String createResponse(final HttpRequest httpRequest) throws URISyntaxException, IOException {
        final String requestUrl = httpRequest.getRequestUrl();
        final String requestBody = httpRequest.getRequestBody();
        saveUser(requestBody);
        final URL resource =
                this.getClass().getClassLoader().getResource("static" + requestUrl + ".html");
        final Path path = Paths.get(resource.toURI());
        final var responseBody = new String(Files.readAllBytes(path));
        final HttpResponse httpResponse =
                new HttpResponse(HttpStatus.FOUND, responseBody, ContentType.HTML, "/index.html");
        return httpResponse.toString();
    }

    private void saveUser(final String requestBody) {
        final HashMap<String, String> registerData = QueryParamsParser.parseByBody(requestBody);
        final String account = registerData.get("account");
        final String email = registerData.get("email");
        final String password = registerData.get("password");
        final User user = new User(account, password, email);
        final Optional<User> findUser = InMemoryUserRepository.findByAccount(user.getAccount());
        if (findUser.isPresent()) {
            log.info("이미 존재하는 유저입니다.");
            return;
        }
        InMemoryUserRepository.save(user);
        log.info("유저 회원가입 성공! user={}", user);
    }
}
