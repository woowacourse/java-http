package nextstep.jwp.http.controller.custom.login;

import java.io.File;
import nextstep.jwp.http.Body;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.request.request_line.HttpPath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Response;
import nextstep.jwp.http.response.response_line.ResponseLine;

public class GetLoginController implements Controller {

    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE_RESOURCE_PATH = "/login.html";
    
    @Override
    public Response doService(HttpRequest httpRequest) {
        ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, HttpStatus.OK);
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
