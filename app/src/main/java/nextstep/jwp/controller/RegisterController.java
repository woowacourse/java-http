package nextstep.jwp.controller;

import static nextstep.jwp.controller.IndexController.INDEX_RESOURCE_PATH;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestURI;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.http.util.ParamExtractor;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    private static final String REGISTER_RESOURCE_PATH = "/register.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.of(HttpStatus.OK, new RequestURI(REGISTER_RESOURCE_PATH));
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        final Body body = request.getBody();
        final Map<String, String> params = ParamExtractor.extractParams(body.asString());
        String account = URLDecoder.decode(params.get("account"), Charset.defaultCharset());
        String password = URLDecoder.decode(params.get("password"), Charset.defaultCharset());
        String email = URLDecoder.decode(params.get("email"), Charset.defaultCharset());

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.redirect(INDEX_RESOURCE_PATH);
    }
}
