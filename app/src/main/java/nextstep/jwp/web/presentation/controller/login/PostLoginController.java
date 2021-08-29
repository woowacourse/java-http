package nextstep.jwp.web.presentation.controller.login;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.web.infrastructure.db.InMemoryUserRepository;
import nextstep.jwp.http.common.ParameterExtractor;
import nextstep.jwp.web.presentation.controller.CustomController;
import nextstep.jwp.http.exception.BadRequestException;
import nextstep.jwp.http.exception.UnauthorizedException;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.http.message.response.HttpResponse;
import nextstep.jwp.http.message.response.Response;
import nextstep.jwp.web.domain.model.User;

public class PostLoginController extends CustomController {

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

        return HttpResponse.redirect(INDEX_PAGE_PATH);
    }

    private String getBody(HttpRequest httpRequest) {
        return httpRequest.getBody().getValue()
            .orElseThrow(BadRequestException::new);
    }

    private void validateLoginParams(Map<String, String> body) {
        if(Objects.isNull(body.get("account")) || Objects.isNull(body.get("password"))) {
            throw new BadRequestException();
        }
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    protected String path() {
        return LOGIN_PATH;
    }
}
