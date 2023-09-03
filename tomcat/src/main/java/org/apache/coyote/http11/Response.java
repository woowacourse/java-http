package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

public class Response {

    private static final int LOCATION_INDEX = 1;
    private static final String HTTP_HEADER_DELIMITER = " ";
    private static final String BASE_PATH = "static";
    private static final String ROOT_RESPONSE = "Hello world!";
    private static final String QUERY = "?";
    private static final Logger LOGGER = LoggerFactory.getLogger(Response.class);

    private final Path path;
    private final ContentType contentType;
    private final Map<String, String> parameters;

    public Response(final Path path, final ContentType contentType, final Map<String, String> parameters) {
        this.path = path;
        this.contentType = contentType;
        this.parameters = parameters;
    }

    public static Response from(final InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        final String request = br.readLine().split(HTTP_HEADER_DELIMITER)[LOCATION_INDEX];

        if (request.contains(QUERY)) {
            final int queryIndex = request.indexOf(QUERY);
            return createInstanceWithParameters(request, queryIndex);
        }

        return createInstance(request, new HashMap<>());
    }

    private static Response createInstanceWithParameters(final String request, final int queryIndex) {
        final String location = request.substring(0, queryIndex);

        final Map<String, String> parameters = new HashMap<>();
        final String queries = request.substring(queryIndex + 1);
        Arrays.stream(queries.split("&"))
                .map(query -> query.split("="))
                .forEach(query -> parameters.put(query[0], query[1]));

        return createInstance(location, parameters);
    }

    private static Response createInstance(final String location, final Map<String, String> parameters) {
        final ContentType contentType = ContentType.from(location);
        final Path path = getPath(location, contentType);

        return new Response(path, contentType, parameters);
    }

    private static Path getPath(final String location, final ContentType contentType) {
        final ClassLoader classLoader = Response.class.getClassLoader();
        final String locationWithoutExtension = location.replaceAll("\\.[^.]+$", "");
        final URL resource = classLoader.getResource(BASE_PATH + locationWithoutExtension + contentType.getExtension());

        if (isNull(resource)) {
            return Paths.get("/");
        }
        return Paths.get(resource.getPath());
    }

    public String get() throws IOException {
        if (path.endsWith("login.html") && !parameters.isEmpty()) {
            try {
                login();
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /index",
                        "");
            } catch (Exception e) {
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /401",
                        "");
            }
        }

        final String body = makeResponseBody();
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                contentType.toHeader(),
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private void login() {
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 유저 정보입니다."));

        if (user.checkPassword(password)) {
            LOGGER.info("user : {}", user);
            return;
        }

        throw new IllegalArgumentException("잘못된 유저 정보입니다.");
    }

    private String makeResponseBody() throws IOException {
        if (Files.isDirectory(path)) {
            return ROOT_RESPONSE;
        }
        return String.join("\n", Files.readAllLines(path)) + "\n";
    }
}
