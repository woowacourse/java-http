package nextstep.jwp.http.controller.custom.login;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.common.ParameterExtractor;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.exception.BadRequestException;
import nextstep.jwp.http.exception.UnauthorizedException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Response;
import nextstep.jwp.http.response.response_line.ResponseLine;
import nextstep.jwp.model.User;

public class PostLoginController implements Controller {

    private static final String LOGIN_PATH = "/login";
    private static final String INDEX_PAGE_PATH = "/index.html";

    @Override
    public Response doService(HttpRequest httpRequest) {
        final String rawBody = getBody(httpRequest);
        final Map<String, String> body = ParameterExtractor.extract(rawBody);

        validateLoginParams(body);

        final String account = body.get("account");
        final String password = body.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(UnauthorizedException::new);

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException();
        }

        return createLoginSuccessPage();
    }

    private String getBody(HttpRequest httpRequest) {
        return httpRequest.getBody().getBody()
            .orElseThrow(BadRequestException::new);
    }

    private HttpResponse createLoginSuccessPage() {
        ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, HttpStatus.FOUND);

        Map<String, String> rawHeaders = new HashMap<>();
        rawHeaders.put("Location", INDEX_PAGE_PATH);

        Headers headers = new Headers(rawHeaders);
        return new HttpResponse(responseLine, headers);
    }

    private void validateLoginParams(Map<String, String> body) {
        if(Objects.isNull(body.get("account")) || Objects.isNull(body.get("password"))) {
            throw new BadRequestException();
        }
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return httpRequest.getHttpMethod().equals(HttpMethod.POST) &&
            httpRequest.getPath().getUri().equals(LOGIN_PATH);
    }
}
