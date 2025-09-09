package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.StatusLine;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return "/register".equals(request.getPath());
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        final String httpVersion = request.getVersion();

        if (request.equalMethod("GET")) {
            return registerPageResponse(httpVersion, "/register.html");
        }

        if (request.equalMethod("POST") && request.equalContentType("application/x-www-form-urlencoded")) {
            final Map<String, String> formParams = request.getFormParams();
            final String account = formParams.get("account");
            final String email = formParams.get("email");
            final String password = formParams.get("password");
            final User user = new User(account, password, email);

            if (account != null && email != null & password != null) {
                InMemoryUserRepository.save(user);
                log.info("회원가입 완료 {}", user);
                return redirectResponse(httpVersion, "/index.html");
            }
        }

        return registerPageResponse(httpVersion, "/register.html");
    }

    private HttpResponse registerPageResponse(String httpVersion, String location) throws IOException {
        final String resourcePath = "static" + location;
        final URL resource = getClass().getClassLoader().getResource(resourcePath);

        String body = "";
        if (resource != null) {
            body = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.HTML.getMimeType());
        headers.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        return new HttpResponse(
                new StatusLine(httpVersion, StatusCode.OK),
                headers,
                body.getBytes(StandardCharsets.UTF_8)
        );
    }

    private HttpResponse redirectResponse(final String httpVersion, final String location) {
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("location", location);
        headers.addHeader("Content-Length", "0");

        return new HttpResponse(
                new StatusLine(httpVersion, StatusCode.FOUND),
                headers,
                new byte[0]
        );
    }
}
