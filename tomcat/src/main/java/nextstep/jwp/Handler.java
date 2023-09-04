package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.cookie.HttpCookie;
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
            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.OK)
                    .responseBody("Hello world!")
                    .contentType(httpRequest.contentType())
                    .build();
        }
        if (requestURI.isRegister()) {
            return doRegister(httpRequest);
        }
        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.OK)
                .responseBody(parseResponseBody(requestURI.getResourcePath()))
                .contentType(httpRequest.contentType())
                .build();
    }

    private static HttpResponse doRegister(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isRequestBodyEmpty()) {
            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.OK)
                    .responseBody(parseResponseBody("static/register.html"))
                    .contentType(httpRequest.contentType())
                    .redirectPage("register.html")
                    .build();
        }

        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getValueOf("account");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.FOUND)
                    .responseBody(parseResponseBody("static/register.html"))
                    .contentType(httpRequest.contentType())
                    .redirectPage("register.html")
                    .build();
        }
        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.CREATED)
                .responseBody(parseResponseBody("static/index.html"))
                .contentType(httpRequest.contentType())
                .redirectPage("index.html")
                .build();
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
            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .responseBody(parseResponseBody("static/401.html"))
                    .contentType(httpRequest.contentType())
                    .redirectPage("401.html")
                    .build();
        }
        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.FOUND)
                .responseBody(parseResponseBody("static/index.html"))
                .contentType(httpRequest.contentType())
                .redirectPage("index.html")
                .httpCookie(HttpCookie.jSessionId(UUID.randomUUID().toString()))
                .build();
    }

    private static String parseResponseBody(String resourcePath) throws IOException {
        Path path = new File(Objects.requireNonNull(
                Handler.class.getClassLoader().getResource(resourcePath)).getFile()
        ).toPath();
        return new String(Files.readAllBytes(path));
    }

}
