package nextstep.jwp.handler.post;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BusinessException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class RegisterPostController implements Controller {

    private static final String STATIC = "static";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        final User user = makeUser(request.getRequestBody());
        InMemoryUserRepository.save(user);
        final var resource = getClass().getClassLoader().getResource(STATIC + "/index.html");
        setResponse(response, StatusCode.CREATED, ContentType.HTML, resource);
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

    private void setResponse(final HttpResponse response, final StatusCode statusCode,
                             final ContentType contentType, final URL resource) throws IOException {
        response.setStatusCode(statusCode);
        response.setContentType(contentType);
        response.setResponseBodyByUrl(resource);
    }
}
