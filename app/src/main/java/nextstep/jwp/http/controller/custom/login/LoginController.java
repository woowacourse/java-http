package nextstep.jwp.http.controller.custom.login;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.Body;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.exception.BadRequestException;
import nextstep.jwp.http.exception.UnauthorizedException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.request.request_line.HttpPath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Response;
import nextstep.jwp.http.response.response_line.ResponseLine;
import nextstep.jwp.model.User;

public class LoginController implements Controller {

    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE_RESOURCE_PATH = "/login.html";
    private static final String INDEX_PAGE_PATH = "/index.html";

    @Override
    public Response doService(HttpRequest httpRequest) {
        final HttpPath path = httpRequest.getPath();

        if(!isLoginable(path)) {
            return createLoginPageResponse();
        }

        final String account = getParam(path, "account");
        final String password = getParam(path, "password");

        final User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(UnauthorizedException::new);

        if(!user.checkPassword(password)) {
            throw new UnauthorizedException();
        }

        return createLoginSuccessPage();
    }

    private Response createLoginPageResponse() {
        ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, HttpStatus.OK);
        Headers headers = new Headers();

        File file = new HttpPath(LOGIN_PAGE_RESOURCE_PATH).toFile();
        Body body = Body.fromFile(file);

        headers.setBodyHeader(body);
        return new HttpResponse(responseLine, headers, body);
    }

    private HttpResponse createLoginSuccessPage() {
        ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, HttpStatus.FOUND);

        Map<String, String> rawHeaders = new HashMap<>();
        rawHeaders.put("Location", INDEX_PAGE_PATH);

        Headers headers = new Headers(rawHeaders);
        return new HttpResponse(responseLine, headers);
    }

    private String getParam(HttpPath path, String password) {
        return path.getParam(password)
            .orElseThrow(BadRequestException::new);
    }

    private boolean isLoginable(HttpPath path) {
        return path.getParam("account").isPresent() &&
            path.getParam("password").isPresent();
    }

    private Response createResponse(HttpStatus httpStatus, String path) {
        ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, httpStatus);
        Headers headers = new Headers();

        File file = new HttpPath(LOGIN_PAGE_RESOURCE_PATH).toFile();
        Body body = Body.fromFile(file);

        headers.setBodyHeader(body);
        return new HttpResponse(responseLine, headers, body);
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return httpRequest.getHttpMethod().equals(HttpMethod.GET) &&
            httpRequest.getPath().getPath().equals(LOGIN_PATH);
    }
}
