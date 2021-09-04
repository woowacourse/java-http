package nextstep.jwp.controller;

import static nextstep.jwp.controller.StaticResourcePath.CONFLICT_PAGE;
import static nextstep.jwp.controller.StaticResourcePath.INDEX_PAGE;
import static nextstep.jwp.controller.StaticResourcePath.NOT_FOUND_PAGE;

import java.io.IOException;
import nextstep.jwp.controller.request.RegisterRequest;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.exception.StaticResourceNotFoundException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.service.StaticResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends RestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    private final RegisterService registerService;
    private final StaticResourceService staticResourceService;

    public RegisterController(RegisterService registerService, StaticResourceService staticResourceService) {
        this.registerService = registerService;
        this.staticResourceService = staticResourceService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        try {
            StaticResource staticResource = staticResourceService.findByPathWithExtension(httpRequest.getUri(), ".html");

            return HttpResponse.withBody(HttpStatus.OK, staticResource);
        } catch (StaticResourceNotFoundException e) {
            StaticResource staticResource = staticResourceService.findByPath(NOT_FOUND_PAGE.getValue());

            LOGGER.warn(e.getMessage());

            return HttpResponse.withBody(HttpStatus.NOT_FOUND, staticResource);
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        try {
            RegisterRequest registerRequest = getRegisterRequest(httpRequest);
            registerService.registerUser(registerRequest);

            LOGGER.debug("Register Success.");

            return HttpResponse.redirect(HttpStatus.MOVED_PERMANENTLY, INDEX_PAGE.getValue());
        } catch (DuplicateAccountException e) {
            LOGGER.debug("Register Failed.");

            StaticResource resource = staticResourceService.findByPath(CONFLICT_PAGE.getValue());
            return HttpResponse.withBody(HttpStatus.CONFLICT, resource);
        }
    }

    private RegisterRequest getRegisterRequest(HttpRequest httpRequest) {
        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");
        String email = httpRequest.getBodyParameter("email");

        LOGGER.debug("Register Request => account: {}, email: {}", account, email);

        return new RegisterRequest(account, password, email);
    }
}
