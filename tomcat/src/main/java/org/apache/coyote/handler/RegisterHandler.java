package org.apache.coyote.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.domain.FilePath;
import org.apache.coyote.domain.request.HttpMethod;
import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.HttpStatusCode;
import org.apache.coyote.domain.response.MyHttpResponse;
import org.apache.coyote.domain.response.RedirectUrl;
import org.apache.coyote.domain.request.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    @Override
    public MyHttpResponse run(HttpRequest httpRequest) throws URISyntaxException, IOException {
        final FilePath filePath = FilePath.from(httpRequest.getUri());
        if (httpRequest.getHttpMethod().equals(HttpMethod.GET) && httpRequest.getQueryParam().isEmpty()) {
            return MyHttpResponse.from(filePath, HttpStatusCode.OK);
        }
        return MyHttpResponse.from(filePath, HttpStatusCode.FOUND)
                .addRedirectUrlHeader(RedirectUrl.from(register(httpRequest)));
    }

    private static String register(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getRequestBody().get("account");
        String password = requestBody.getRequestBody().get("password");
        String email = requestBody.getRequestBody().get("email");
        User user = new User(InMemoryUserRepository.size() + 1L, account, password, email);
        InMemoryUserRepository.save(user);
        return "/index.html";
    }
}
