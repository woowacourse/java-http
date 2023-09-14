package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

import java.io.IOException;
import java.util.Map;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> requestBodyValues = request.getRequestParameters();
        final var user = new User(requestBodyValues.get("account"), requestBodyValues.get("password"),
                requestBodyValues.get("email"));
        InMemoryUserRepository.save(user);

        final var statusLine = StatusLine.of(request.getRequestLine().getProtocol(), HttpStatus.FOUND);
        response.setStatusLine(statusLine);
        response.addResponseHeader("Location", INDEX_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final var statusLine = StatusLine.of(request.getRequestLine().getProtocol(), HttpStatus.OK);
        final var responseBody = ResponseBody.fromUri("/register.html");
        response.setStatusLine(statusLine);
        response.addResponseHeader("Content-Type", TEXT_HTML);
        response.addResponseHeader("Content-Length", String.valueOf(responseBody.getBody().getBytes().length));
        response.setResponseBody(responseBody);
    }
}
