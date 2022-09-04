package nextstep.jwp.handler;

import java.io.File;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.apache.coyote.http11.utils.FileUtil;

public class RegisterHandler {

    public HttpResponse register(final HttpRequest httpRequest) {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            return generateResponse(HttpStatus.OK, "/register.html");
        }

        final HttpRequestBody requestBody = httpRequest.getHttpRequestBody();
        final User user = createUser(requestBody);
        InMemoryUserRepository.save(user);

        final HttpResponse response = generateResponse(HttpStatus.FOUND, "/register.html");
        response.addHeader("Location", "/login.html");
        return response;
    }

    private User createUser(final HttpRequestBody requestBody) {
        final String account = requestBody.findByKey("account");
        final String password = requestBody.findByKey("password");
        final String email = requestBody.findByKey("email");
        return new User(account, password, email);
    }

    private HttpResponse generateResponse(final HttpStatus httpStatus, final String path) {
        final File file = FileUtil.findFile(path);
        final String contentType = FileUtil.findContentType(file);
        final String responseBody = FileUtil.generateFile(file);
        return new HttpResponse(httpStatus, contentType, responseBody);
    }
}
