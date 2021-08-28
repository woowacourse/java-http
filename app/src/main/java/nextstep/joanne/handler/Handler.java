package nextstep.joanne.handler;

import nextstep.joanne.db.InMemoryUserRepository;
import nextstep.joanne.domain.HttpRequest;
import nextstep.joanne.domain.HttpResponse;
import nextstep.joanne.domain.HttpStatus;
import nextstep.joanne.infra.HttpRequestResponseConverter;
import nextstep.joanne.model.User;

import java.io.IOException;

public class Handler {
    private final HttpRequest httpRequest;

    public Handler(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpResponse handle() throws IOException {

        if (httpRequest.uriEquals("/register")) {
            if (httpRequest.isGet()) {
                return HttpRequestResponseConverter.convertToHttpResponse(
                        HttpStatus.OK,
                        httpRequest.resourceUri(),
                        httpRequest.contentType()
                );
            }

            if (httpRequest.isPost()) {
                registerRequest();
                return HttpRequestResponseConverter.convertToHttpResponse(
                        HttpStatus.FOUND,
                        "/index.html",
                        httpRequest.contentType()
                );
            }
        }

        if (httpRequest.uriEquals("/login")) {
            if (httpRequest.isGet()) {
                return HttpRequestResponseConverter.convertToHttpResponse(
                        HttpStatus.OK,
                        httpRequest.resourceUri(),
                        httpRequest.contentType()
                );
            }

            if (httpRequest.isPost()) {
                try {
                    loginRequest(httpRequest.getFromRequestBody("account"),
                            httpRequest.getFromRequestBody("password"));
                    return HttpRequestResponseConverter.convertToHttpResponse(
                            HttpStatus.FOUND,
                            "/index.html",
                            httpRequest.contentType()
                    );

                } catch (IllegalArgumentException e) {
                    return HttpRequestResponseConverter.convertToHttpResponse(
                            HttpStatus.UNAUTHORIZED,
                            "/401.html",
                            httpRequest.contentType()
                    );
                }
            }
        }

        return HttpRequestResponseConverter.convertToHttpResponse(
                HttpStatus.OK,
                httpRequest.resourceUri(),
                httpRequest.contentType()
        );
    }

    private void loginRequest(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException();
        }
    }

    private void registerRequest() {
        final User user = new User(httpRequest.getFromRequestBody("account"),
                httpRequest.getFromRequestBody("password"),
                httpRequest.getFromRequestBody("email"));
        InMemoryUserRepository.save(user);
    }
}
