package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryString;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestURI;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private Handler() {
    }

    public static HttpResponse run(HttpRequest httpRequest) throws IOException {
        RequestURI requestURI = httpRequest.getRequestUrl();
        if (requestURI.isLoginPage() && requestURI.hasQueryString()) {
            return doLogin(httpRequest);
        }
        if (requestURI.isHome()) {
            return new HttpResponse(HttpStatus.OK, "Hello world!", httpRequest.contentType());
        }
        if (requestURI.isRegister()) {
            return doRegister(httpRequest);
        }
        String responseBody = readResponseBody(requestURI.getResourcePath());
        return new HttpResponse(HttpStatus.OK, responseBody, httpRequest.contentType());
    }

    private static HttpResponse doRegister(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isRequestBodyEmpty()) {
            String responseBody = readResponseBody("static/register.html");
            return new HttpResponse(HttpStatus.OK, responseBody, httpRequest.contentType(), "register.html");
        }

        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getValueOf("account");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            String responseBody = readResponseBody("static/register.html");
            return new HttpResponse(HttpStatus.FOUND, responseBody, httpRequest.contentType(), "register.html");
        }
        String responseBody = readResponseBody("static/index.html");
        return new HttpResponse(HttpStatus.CREATED, responseBody, httpRequest.contentType(), "index.html");
    }

    private static HttpResponse doLogin(HttpRequest httpRequest) throws IOException {
        RequestURI requestURI = httpRequest.getRequestUrl();
        QueryString queryString = requestURI.getQueryString();
        String account = queryString.getValueOf("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
        log.info(user.toString());
        String password = queryString.getValueOf("password");
        if (!user.checkPassword(password)) {
            String responseBody = readResponseBody("static/401.html");
            return new HttpResponse(HttpStatus.UNAUTHORIZED, responseBody, httpRequest.contentType(), "401.html");
        }
        String responseBody = readResponseBody("static/index.html");
        return new HttpResponse(HttpStatus.FOUND, responseBody, httpRequest.contentType(), "index.html");
    }

    private static String readResponseBody(String resourcePath) throws IOException {
        Path path = new File(Objects.requireNonNull(
                Handler.class.getClassLoader().getResource(resourcePath)).getFile()
        ).toPath();
        return new String(Files.readAllBytes(path));
    }

}
