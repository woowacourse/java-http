package nextstep.jwp.controller;

import static nextstep.jwp.controller.DefaultController.INDEX_RESOURCE_PATH;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestPath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.http.util.ParamExtractor;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_RESOURCE_PATH = "/register.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.of(HttpStatus.OK, new RequestPath(REGISTER_RESOURCE_PATH));
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        try {
            final Body body = request.getBody();
            final Map<String, String> params = ParamExtractor.extractParams(body.asString());
            final User user = createUser(params);
            InMemoryUserRepository.save(user);

            return HttpResponse.redirect(INDEX_RESOURCE_PATH);
        } catch (BadRequestException exception) {
            logger.error("Register Fail!");
            return HttpResponse.redirect(REGISTER_RESOURCE_PATH);
        }
    }

    private User createUser(Map<String, String> params) {
        try {
            String account = URLDecoder.decode(params.get("account"), Charset.defaultCharset());
            String password = URLDecoder.decode(params.get("password"), Charset.defaultCharset());
            String email = URLDecoder.decode(params.get("email"), Charset.defaultCharset());

            return new User(account, password, email);
        } catch (NullPointerException exception) {
            throw new BadRequestException();
        }
    }
}
