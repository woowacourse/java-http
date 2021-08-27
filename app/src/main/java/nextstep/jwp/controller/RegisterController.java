package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.request.RegisterRequest;
import nextstep.jwp.exception.DuplicateException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.staticresource.StaticResourceFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nextstep.jwp.http.request.HttpMethod.GET;

public class RegisterController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    private final RegisterService registerService;

    public RegisterController(StaticResourceFinder staticResourceFinder, RegisterService registerService) {
        super(staticResourceFinder);
        this.registerService = registerService;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.hasMethod(GET)) {
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        assignStaticResourceByUriToResponse(request, response, ".html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final RegisterRequest registerRequest = getRegisterRequest(request);
        try {
            registerService.register(registerRequest.toUser());
        } catch (DuplicateException e) {
            LOG.debug("중복으로 인한 회원가입 실패");
        } finally {
            assignRedirectToResponse(response, "http://localhost:8080/index.html");
        }
    }

    private RegisterRequest getRegisterRequest(HttpRequest request) {
        final RequestBody requestBody = request.getBody();
        final String account = requestBody.getParameter("account");
        final String email = requestBody.getParameter("email");
        final String password = requestBody.getParameter("password");
        LOG.debug("회원가입 요청 account : {}", account);
        LOG.debug("회원가입 요청 email : {}", email);
        LOG.debug("회원가입 요청 password : {}", password);
        return new RegisterRequest(account, password, email);
    }
}
