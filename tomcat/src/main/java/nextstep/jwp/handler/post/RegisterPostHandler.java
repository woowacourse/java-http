package nextstep.jwp.handler.post;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BusinessException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.util.Arrays;

public class RegisterPostHandler implements Handler {

    private static final String STATIC = "static";

    @Override
    public HttpResponse resolve(final HttpRequest request) throws IOException {
        final User user = makeUser(request.getRequestBody());
        InMemoryUserRepository.save(user);
        final var resource = getClass().getClassLoader().getResource(STATIC + "/index.html");
        return HttpResponse.createBy(request.getVersion(), resource, StatusCode.CREATED);
    }

    private User makeUser(final RequestBody requestBody) {
        final String[] tokens = requestBody.getValue().split("&");

        final String account = findValueByKey(tokens, "account");
        final String email = findValueByKey(tokens, "email");
        final String password = findValueByKey(tokens, "password");

        return new User(account, password, email);
    }

    private String findValueByKey(final String[] tokens, final String key) {
        return Arrays.stream(tokens)
                .filter(it -> it.split("=")[0]
                        .equals(key))
                .map(it -> it.split("=")[1])
                .findFirst()
                .orElseThrow(() -> new BusinessException(key + "에 대한 정보가 존재하지 않습니다."));
    }
}
