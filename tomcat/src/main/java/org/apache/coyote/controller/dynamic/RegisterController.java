package org.apache.coyote.controller.dynamic;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.resource.StaticResourceReader;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpBody.HttpBody;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpRequest.httpHeader.HttpMethod;
import org.apache.coyote.httpResponse.HttpResponse;
import org.apache.coyote.httpResponse.StatusCode;

public class RegisterController implements Controller {

    private static final StaticResourceReader staticResourceReader = StaticResourceReader.getInstance();

    @Override
    public void service(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException {
        final HttpHeader httpHeader = request.getHttpHeader();
        final HttpMethod httpMethod = httpHeader.getHttpMethod();
        if (httpMethod.equals(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    private void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException {
        responseRegisterHtml(request.getHttpHeader(), response);
    }

    private void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        if (registerMember(request)) {
            response.updateStatusLine("HTTP/1.1", StatusCode.FOUND);
            response.addHeader("Content-Length", "0");
            response.addHeader("Location", "/index.html");
            return;
        }
        responseRegisterErrorPage("/register.html", StatusCode.BAD_REQUEST, response);
    }

    private void responseRegisterHtml(
            final HttpHeader httpHeader,
            final HttpResponse httpResponse
    ) throws IOException {
        final String body = staticResourceReader.getStaticResponseBody("static" + httpHeader.getPurePath());
        httpResponse.updateStatusLine("HTTP/1.1", StatusCode.OK);
        httpResponse.updateBody(body);
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    private boolean registerMember(final HttpRequest httpRequest) {
        final HttpBody httpBody = httpRequest.getHttpBody();
        final String account = httpBody.getData("account");
        final String email = httpBody.getData("email");
        final String password = httpBody.getData("password");
        if (account == null || email == null || password == null) {
            return false;
        }
        boolean isAlreadyRegister = InMemoryUserRepository.findByAccount(account)
                .isPresent();
        if (isAlreadyRegister) {
            return false;
        }
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return true;
    }

    private void responseRegisterErrorPage(
            final String errorPagePath,
            final StatusCode statusCode,
            final HttpResponse httpResponse
    ) throws IOException {
        final String body = staticResourceReader.getStaticResponseBody("static" + errorPagePath);
        httpResponse.updateStatusLine("HTTP/1.1", statusCode);
        httpResponse.updateBody(body);
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }
}
