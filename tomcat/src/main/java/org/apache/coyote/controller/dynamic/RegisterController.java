package org.apache.coyote.controller.dynamic;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpBody.HttpBody;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpRequest.httpHeader.HttpMethod;
import org.apache.coyote.httpResponse.HttpResponse;
import org.apache.coyote.httpResponse.StatusCode;

public class RegisterController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        final HttpHeader httpHeader = request.getHttpHeader();
        final HttpMethod httpMethod = httpHeader.getHttpMethod();
        if (httpMethod.equals(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    private void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        responseHtml(request.getHttpHeader(), response);
    }

    private void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        if (registerMember(request)) {
            response.updateStatusLine("HTTP/1.1", StatusCode.FOUND);
            response.addHeader("Content-Length", "0");
            response.addHeader("Location", "/index.html");
            return;
        }
        responseErrorPage("/register.html", StatusCode.BAD_REQUEST, response);
    }

    private void responseHtml(
            final HttpHeader httpHeader,
            final HttpResponse httpResponse
    ) throws IOException {
        final String body = getStaticResponseBody("static" + httpHeader.getPurePath());
        httpResponse.updateStatusLine("HTTP/1.1", StatusCode.OK);
        httpResponse.updateBody(body);
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    private String getStaticResponseBody(final String fileUrl) throws IOException {
        try {
            final URI uri = getClass().getClassLoader()
                    .getResource(fileUrl)
                    .toURI();
            final Path htmlPath = Path.of(uri);
            final byte[] read = Files.readAllBytes(htmlPath);
            final String body = new String(read, StandardCharsets.UTF_8);
            return body;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("존재하지 않은 정적 파일입니다.");
        }
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

    private void responseErrorPage(
            final String errorPagePath,
            final StatusCode statusCode,
            final HttpResponse httpResponse
    ) throws IOException {
        final String body = getStaticResponseBody("static" + errorPagePath);
        httpResponse.updateStatusLine("HTTP/1.1", statusCode);
        httpResponse.updateBody(body);
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }
}
