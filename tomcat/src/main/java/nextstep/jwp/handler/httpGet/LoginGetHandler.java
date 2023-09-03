package nextstep.jwp.handler.httpGet;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BusinessException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class LoginGetHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginGetHandler.class);
    private static final String STATIC = "static";

    @Override
    public Http11Response resolve(final Http11Request request) throws IOException {
        final String query = request.getQuery();
        final String[] tokens = query.split("&");

        final String account = findValueByKey(tokens, "account");
        final String password = findValueByKey(tokens, "password");

        final User user = findUser(account, password);
        log.info(user.toString());

        final var resource = getClass().getClassLoader().getResource(STATIC + "/login.html");
        return makeHttp11Response(resource, StatusCode.OK);
    }

    private User findUser(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new BusinessException("존재하지 않는 회원입니다."));
        if (!user.checkPassword(password)) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    private String findValueByKey(final String[] tokens, final String key) {
        return Arrays.stream(tokens)
                .filter(it -> it.split("=")[0]
                        .equals(key))
                .map(it -> it.split("=")[1])
                .findFirst()
                .orElseThrow(() -> new BusinessException(key + "에 대한 정보가 존재하지 않습니다."));
    }

    private Http11Response makeHttp11Response(final URL resource, final StatusCode statusCode) throws IOException {
        final var actualFilePath = new File(resource.getPath()).toPath();
        final var fileBytes = Files.readAllBytes(actualFilePath);
        final String responseBody = new String(fileBytes, StandardCharsets.UTF_8);
        return new Http11Response(statusCode, ContentType.findByPath(resource.getPath()), responseBody);
    }
}
