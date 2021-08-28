package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.AuthorizationException;
import nextstep.jwp.model.NoSuchUserException;
import nextstep.jwp.model.User;

import java.util.Map;

public class LoginController implements Controller {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();

        return "GET".equals(httpMethod) && path.startsWith("/login");
    }

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        if (httpRequest.hasNoQueryParameters()) {
            final String path = httpRequest.getPath();
            final String responseBody = readFile(path);

            return new HttpResponse(
                    httpRequest.getProtocol(),
                    HttpStatus.OK,
                    "text/html",
                    responseBody.getBytes().length,
                    responseBody
            );
        }

        final Map<String, String> queryParameters = httpRequest.getQueryParameters();
        final String account = queryParameters.get("account");
        final String password = queryParameters.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchUserException::new);

        if (!user.checkPassword(password)) {
            throw new AuthorizationException();
        }

        final String responseBody = String.format(
                "Welcome %s!!",
                user.getAccount()
        );

        return new HttpResponse(
                httpRequest.getProtocol(),
                HttpStatus.OK,
                "text/html",
                responseBody.getBytes().length,
                responseBody
        );
    }
}
