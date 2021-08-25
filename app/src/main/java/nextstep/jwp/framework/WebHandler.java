package nextstep.jwp.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class WebHandler {

    private WebHandler() {
    }

    public static String run(final InputStream inputStream) throws IOException {
        try {
            RequestHeader request = RequestHeader.from(inputStream);
            request = loginPage(request);
            final URL resource = request.resource();

            assert resource != null;
            final Path path = new File(resource.getPath()).toPath();
            final String content = Files.readString(path);

            return String.join("\r\n",
                "HTTP/1.1 " + request.httpStatusValue() + " " + request.httpStatusReasonPhrase() + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
        } catch (ArrayIndexOutOfBoundsException e) {
            assert RequestURI.notFound() != null;
            final Path path = new File(RequestURI.notFound().getPath()).toPath();
            final String content = Files.readString(path);

            return String.join("\r\n",
                "HTTP/1.1 " + HttpStatus.NOT_FOUND.value() + " " + HttpStatus.NOT_FOUND.getReasonPhrase() + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
        }
    }

    private static RequestHeader loginPage(RequestHeader request) {
        if (request.url().equals("login.html")) {
            final Map<String, String> queryParam = request.queryParam();

            final Optional<User> account = InMemoryUserRepository.findByAccount(queryParam.get("account"));
            request = loginStatus(account, request);
        }
        return request;
    }

    private static RequestHeader loginStatus(final Optional<User> user, RequestHeader requestHeader) {
        if (user.isPresent()) {
            requestHeader.changeHttpStatus(HttpStatus.FOUND);
            return requestHeader;
        }
        requestHeader.changeHttpStatus(HttpStatus.UNAUTHORIZED);
        return requestHeader;
    }
}
