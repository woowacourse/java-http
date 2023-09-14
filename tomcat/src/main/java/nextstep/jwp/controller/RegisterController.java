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
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Map<String, String> requestBodyValues = httpRequest.getRequestParameters();
        final var user = new User(requestBodyValues.get("account"), requestBodyValues.get("password"),
                requestBodyValues.get("email"));
        InMemoryUserRepository.save(user);

        final var statusLine = StatusLine.of(httpRequest.getRequestLine().getProtocol(), HttpStatus.FOUND);
        httpResponse.setStatusLine(statusLine);
        httpResponse.addResponseHeader("Location", INDEX_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final var statusLine = StatusLine.of(httpRequest.getRequestLine().getProtocol(), HttpStatus.OK);
        final var responseBody = ResponseBody.fromUri("/register.html");
        httpResponse.setStatusLine(statusLine);
        httpResponse.addResponseHeader("Content-Type", TEXT_HTML);
        httpResponse.addResponseHeader("Content-Length", String.valueOf(responseBody.getBody().getBytes().length));
        httpResponse.setResponseBody(responseBody);
    }
}
