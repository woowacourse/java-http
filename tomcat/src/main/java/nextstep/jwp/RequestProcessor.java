package nextstep.jwp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.jwp.common.ContentType;
import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.common.HttpVersion;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.HttpCookie;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestHeaders;
import nextstep.jwp.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestProcessor {

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String LOCATION_HEADER = "Location";
    private static final String DEFAULT_FILE_LOCATION = "static/";
    private static final String INDEX_PAGE = "index.html";
    private static final String REGISTER_PAGE = "register.html";
    private static final String UNAUTHORIZED_PAGE = "401.html";
    private static final String SERVER_ERROR_PAGE = "500.html";

    private static final Logger log = LoggerFactory.getLogger(RequestProcessor.class);

    public ResponseEntity from(final HttpRequest httpRequest) throws URISyntaxException, IOException {

        final HttpVersion version = httpRequest.getHttpVersion();
        final HttpMethod method = httpRequest.getHttpMethod();
        final String requestUri = httpRequest.getRequestUri();
        final RequestHeaders requestHeaders = httpRequest.getHeaders();
        String content = "Hello World";

        if (method.equals(HttpMethod.GET)) {
            if (requestUri.isEmpty()) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.HTML.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }

            if (requestUri.equals("login")) {
                content = makeResponseBody(requestUri);
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.HTML.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }

            content = makeResponseBody(requestUri);

            if (requestUri.endsWith(".html") || requestUri.equals("login") || requestUri.equals("register")) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.HTML.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }

            if (requestUri.endsWith(".css")) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.CSS.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }

            if (requestUri.endsWith(".js")) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.JS.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }

            if (requestUri.endsWith(".svg")) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.SVG.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }
        }

        if (method.equals(HttpMethod.POST)) {
            final String requestBody = httpRequest.getRequestBody();

            if (requestUri.equals("register")) {
                final Map<String, String> registerInfo = Arrays.stream(requestBody.split("&"))
                        .map(input -> input.split("="))
                        .collect(Collectors.toMap(info -> info[0], info -> info[1]));

                if (InMemoryUserRepository.findByAccount(registerInfo.get("account")).isPresent()) {
                    return ResponseEntity.of(version, HttpStatus.FOUND, content,
                            Map.of(LOCATION_HEADER, SERVER_ERROR_PAGE));
                }
                final User newUser = new User(registerInfo.get("account"), registerInfo.get("password"),
                        registerInfo.get("email"));
                InMemoryUserRepository.save(newUser);
                return ResponseEntity.of(version, HttpStatus.FOUND, content, Map.of(LOCATION_HEADER, REGISTER_PAGE));
            }

            if (requestUri.equals("login")) {
                String redirectedPage = UNAUTHORIZED_PAGE;
                final Map<String, String> logInfo = Arrays.stream(requestBody.split("&"))
                        .map(input -> input.split("="))
                        .collect(Collectors.toMap(info -> info[0], info -> info[1]));

                final Optional<User> findedUser = InMemoryUserRepository.findByAccount(logInfo.get("account"));
                if (findedUser.isPresent()) {
                    User user = findedUser.get();
                    if (user.checkPassword(logInfo.get("password"))) {
                        log.info(user.toString());
                        if (!requestHeaders.hasJSessionCookie()) {
                            HttpCookie.save("JSESSIONID");
                        }
                        final String cookie = HttpCookie.cookieInfo("JSESSIONID");
                        return ResponseEntity.of(version, HttpStatus.FOUND, content,
                                Map.of(LOCATION_HEADER, INDEX_PAGE,
                                        "Set-Cookie", cookie));
                    }
                }

                return ResponseEntity.of(version, HttpStatus.FOUND, content, Map.of(LOCATION_HEADER, redirectedPage));
            }
        }

        return ResponseEntity.of(version, HttpStatus.NOT_FOUND, content,
                Map.of(CONTENT_TYPE_HEADER, ContentType.HTML.getType(),
                        CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
    }

    private String makeResponseBody(String requestUri) throws URISyntaxException, IOException {
        final URL url;

        if (requestUri.equals("login")) {
            url = getClass().getClassLoader().getResource(DEFAULT_FILE_LOCATION + requestUri + ".html");
            final var path = Paths.get(url.toURI());
            return Files.readString(path);
        }

        if (requestUri.equals("register")) {
            url = getClass().getClassLoader().getResource(DEFAULT_FILE_LOCATION + requestUri + ".html");
            final var path = Paths.get(url.toURI());
            return Files.readString(path);
        }

        url = getClass().getClassLoader().getResource(DEFAULT_FILE_LOCATION + requestUri);
        if (url == null) {
            URL notFoundUrl = getClass().getClassLoader().getResource(DEFAULT_FILE_LOCATION + "404.html");
            final var path = Paths.get(notFoundUrl.toURI());
            return Files.readString(path);
        }
        final var path = Paths.get(url.toURI());
        return Files.readString(path);
    }
}