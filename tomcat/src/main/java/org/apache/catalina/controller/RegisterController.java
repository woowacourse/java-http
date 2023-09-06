package org.apache.catalina.controller;

import static org.apache.catalina.controller.IndexController.INDEX;
import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.POST;
import static org.apache.coyote.http11.response.StatusCode.OK;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Response;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.model.UserService;

public class RegisterController extends HttpController {

    public static final String RESISTER = "/register";
    private static final String REGISTER_HTML = "/register.html";

    @Override
    public Response service(final Request request)  throws IOException, MethodNotAllowedException {
        final HttpMethod httpMethod = request.getHttpMethod();

        if (httpMethod.equals(GET)) {
            return doGet(request);
        }

        if (httpMethod.equals(POST)) {
            return doPost(request);
        }

        throw new MethodNotAllowedException(List.of(GET, POST));
    }

    @Override
    protected Response doPost(final Request request) {
        final User user = UserService.parseToUser(request.getBodies());
        InMemoryUserRepository.save(user);
        return Response.generateRedirectResponse(INDEX);
    }

    @Override
    protected Response doGet(final Request request) throws IOException {
        final String responseBody = FileReader.read(REGISTER_HTML);
        final ContentType contentType = ContentType.findByName(REGISTER_HTML);
        return Response.of(request.getHttpVersion(), OK, contentType, responseBody);
    }
}
