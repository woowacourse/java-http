package nextstep.jwp.framework.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DBNotFoundException;
import nextstep.jwp.exception.PasswordNotMatchException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final String NEW_LINE = System.lineSeparator();
    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private final ProtocolVersion protocolVersion;
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final URL resourceURL;

    public HttpResponse(final HttpRequestLine httpRequestLine, final HttpHeaders headers) {
        this.protocolVersion = httpRequestLine.getProtocolVersion();
        this.status = operate(httpRequestLine.getPath(), HttpStatus.OK);
        this.headers = headers;
        this.resourceURL = httpRequestLine.url(status);
    }

    private HttpStatus operate(final HttpPath path, HttpStatus status) {
        status = login(path, status);
        return register(path, status);
    }

    private HttpStatus login(final HttpPath path, HttpStatus status) {
        if (!path.getPath().equals("login.html") || path.hasNotQueryParams()) {
            return status;
        }

        try {
            checkAccount(path.queryParams());
            return HttpStatus.FOUND;
        } catch (DBNotFoundException | PasswordNotMatchException ignore) {
            return HttpStatus.UNAUTHORIZED;
        }
    }

    private void checkAccount(final Map<String, String> queryParams) {
        final String account = queryParams.get("account");
        final User user = InMemoryUserRepository.findByAccount(account).orElseThrow(DBNotFoundException::new);

        if (user.checkPassword(queryParams.get("password"))) {
            return;
        }
        throw new PasswordNotMatchException();
    }

    private HttpStatus register(final HttpPath path, HttpStatus status) {
        if (!path.getPath().equals("register.html") || path.hasNotQueryParams()) {
            return status;
        }

        try {
            final Map<String, String> queryParams = path.queryParams();
            final String account = queryParams.get("account");
            final String password = queryParams.get("password");
            final String email = queryParams.get("email");

            InMemoryUserRepository.save(new User(generateRandomId(), account, password, email));
            logger.info(account + "님의 새로운 계정이 생성 되었습니다.");

            return HttpStatus.CREATED;
        } catch (DBNotFoundException ignored) {
            return HttpStatus.UNAUTHORIZED;
        }
    }

    private int generateRandomId() {
        return ThreadLocalRandom.current()
            .nextInt(Integer.MAX_VALUE);
    }

    public byte[] getBytes() throws IOException {
        if (Objects.isNull(body())) {
            return getResponseAsBytesWithEmptyBody();
        }
        return getResponseAsBytesWithBody();
    }

    private byte[] getResponseAsBytesWithEmptyBody() {
        return String.join(NEW_LINE,
            statusLine(),
            headers.getHeaders())
            .getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getResponseAsBytesWithBody() throws IOException {
        return String.join(NEW_LINE,
            statusLine(),
            "Host: localhost:8080",
            "Accept: text/css,*/*;q=0.1",
            "Connection: keep-alive",
            "Content-Length: " + body().getBytes().length + " ",
            "",
            body())
            .getBytes(StandardCharsets.UTF_8);
    }

    private String statusLine() {
        return protocolVersion.getProtocolVersion() + " " + status.value() + " " + status.getReasonPhrase() + " ";
    }

    public String body() throws IOException {
        final Path path = new File(resourceURL.getPath()).toPath();
        return Files.readString(path);
    }
}
