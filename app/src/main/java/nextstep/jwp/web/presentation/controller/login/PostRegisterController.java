package nextstep.jwp.web.presentation.controller.login;

import nextstep.jwp.http.common.ParameterExtractor;
import nextstep.jwp.http.exception.BadRequestException;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.http.message.response.HttpResponse;
import nextstep.jwp.http.message.response.Response;
import nextstep.jwp.web.domain.model.User;
import nextstep.jwp.web.infrastructure.db.InMemoryUserRepository;
import nextstep.jwp.web.presentation.controller.CustomController;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;

public class PostRegisterController extends CustomController {

    public static final String REGISTER_PATH = "/register";
    private static final String INDEX_PAGE_PATH = "/index.html";

    @Override
    public Response doService(HttpRequest httpRequest) {
        final var body = getBody(httpRequest);
        final var params = ParameterExtractor.extract(body);

        final String account = getUrlDecodedParam(params, "account");
        final String password = getUrlDecodedParam(params, "password");
        final String email = getUrlDecodedParam(params, "email");

        User user = new User(null, account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.redirect(INDEX_PAGE_PATH);
    }

    private String getBody(HttpRequest httpRequest) {
        return httpRequest.getBody().getValue()
            .orElseThrow(BadRequestException::new);
    }

    private String getUrlDecodedParam(Map<String, String> params, String email) {
        try {
            return URLDecoder.decode(params.get(email), Charset.defaultCharset());
        } catch (NullPointerException e) {
            throw new BadRequestException();
        }
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    protected String path() {
        return REGISTER_PATH;
    }
}
