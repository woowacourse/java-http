package nextstep.jwp.handler.post;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BusinessException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.Http11Response;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;

public class LoginPostHandler implements Handler {

    private static final String STATIC = "static";

    @Override
    public Http11Response resolve(final Http11Request request) throws IOException {
        final RequestBody requestBody = request.getRequestBody();
        final User user = findUser(requestBody);
        if (user == null) {
            final var resource = getClass().getClassLoader().getResource(STATIC + "/401.html");
            return makeHttp11Response(resource, StatusCode.UNAUTHORIZED);
        }
        final var resource = getClass().getClassLoader().getResource(STATIC + "/index.html");
        return makeHttp11Response(resource, StatusCode.FOUND);
    }

    private User findUser(final RequestBody requestBody) {
        final String[] tokens = requestBody.getValue().split("&");

        final String account = findValueByKey(tokens, "account");
        final String password = findValueByKey(tokens, "password");

        return checkUser(account, password);
    }

    private User checkUser(final String account, final String password) {
        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            return optionalUser.get();
        }
        return null;
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
