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

public class GetRegisterController implements Controller {

    private static final String REGISTER_PATH = "/register";
    private static final String REGISTER_RESOURCE_PATH = "/register.html";

    @Override
    public Response doService(HttpRequest httpRequest) {
        final File file = new HttpPath(REGISTER_RESOURCE_PATH).toFile();

        final ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, HttpStatus.OK);
        final Headers headers = new Headers();
        final Body body = Body.fromFile(file);

        headers.setBodyHeader(body);
        return new HttpResponse(responseLine, headers, body);
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return httpRequest.getHttpMethod().equals(HttpMethod.GET) &&
            httpRequest.getPath().getPath().equals(REGISTER_PATH);
    }
}
