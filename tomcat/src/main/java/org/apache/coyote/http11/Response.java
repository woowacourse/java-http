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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class Response {

    private static final int LOCATION_INDEX = 1;
    private static final int METHOD_INDEX = 0;
    private static final String HTTP_HEADER_DELIMITER = " ";
    private static final String BASE_PATH = "static";
    private static final String ROOT_RESPONSE = "Hello world!";
    private static final String QUERY = "?";
    private static final Logger LOGGER = LoggerFactory.getLogger(Response.class);

    private final Path path;
    private final String method;
    private final ContentType contentType;
    private final Map<String, String> parameters;
    private final Cookies cookies;

    public Response(final Path path, final String method, final ContentType contentType, final Map<String, String> parameters, final Cookies cookies) {
        this.path = path;
        this.method = method;
        this.contentType = contentType;
        this.parameters = parameters;
        this.cookies = cookies;
    }

    public static Response from(final InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        final List<String> headers = new ArrayList<>();
        while (!headers.contains("")) {
            headers.add(br.readLine());
        }

        final int contentLength = Integer.parseInt(getContentLength(headers));
        final String[] request = headers.get(0).split(HTTP_HEADER_DELIMITER);
        final String method = request[METHOD_INDEX];
        final String location = request[LOCATION_INDEX];
        final Cookies cookies = Cookies.from(getCookieHeader(headers));

        if (method.equals("POST")) {
            final char[] buffer = new char[contentLength];
            br.read(buffer, 0, contentLength);
            final Map<String, String> parameters = parseParameters(String.valueOf(buffer));
            return createInstance(location, method, parameters, cookies);
        }

        if (location.contains(QUERY)) {
            final int queryIndex = location.indexOf(QUERY);
            final String locationWithoutParameters = location.substring(0, queryIndex);
            final Map<String, String> parameters = parseParameters(location.substring(queryIndex + 1));
            return createInstance(locationWithoutParameters, method, parameters, cookies);
        }

        return createInstance(location, method, new HashMap<>(), cookies);
    }

    private static String getCookieHeader(final List<String> headers) {
        return headers.stream().filter(header -> header.contains("Cookie")).findAny().orElse(null);
    }

    private static String getContentLength(final List<String> header) {
        final String contentLengthKey = "Content-Length: ";

        return header.stream()
                .filter(contentLength -> contentLength.contains(contentLengthKey))
                .map(contentLength -> contentLength.replace(contentLengthKey, ""))
                .findAny()
                .orElse("0");
    }

    private static Map<String, String> parseParameters(final String queries) {
        final Map<String, String> parameters = new HashMap<>();
        Arrays.stream(queries.split("&"))
                .map(query -> query.split("="))
                .forEach(query -> parameters.put(query[0], query[1]));

        return parameters;
    }

    private static Response createInstance(final String location, final String method,
                                           final Map<String, String> parameters, final Cookies cookies) {
        final ContentType contentType = ContentType.from(location);
        final Path path = getPath(location, contentType);

        return new Response(path, method, contentType, parameters, cookies);
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
        if (path.endsWith("register.html") && method.equals("POST")) {
            register();
            return makeResponseForRedirect("/index");
        }

        if (path.endsWith("login.html") && !parameters.isEmpty()) {
            try {
                login();
                return makeResponseForRedirect("/index");
            } catch (Exception e) {
                return makeResponseForRedirect("/401");
            }
        }

        final String body = makeResponseBody();
        return makeResponseForGet(body);
    }

    private void register() {
        final String account = parameters.get("account");
        final String email = parameters.get("email");
        final String password = parameters.get("password");
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private String makeResponseForRedirect(final String location) {
        final List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 302 Found ");
        headers.add("Location: " + location);
        headers.add("");

        return String.join("\r\n", headers);
    }

    private String makeResponseForGet(final String body) {
        final List<String> responses = new ArrayList<>();
        responses.add("HTTP/1.1 200 OK ");
        responses.add(contentType.toHeader());
        responses.add("Content-Length: " + body.getBytes().length + " ");
        if (cookies.notExist()) {
            responses.add(cookies.createNewCookieHeader());
        }

        responses.add("");
        responses.add(body);

        return String.join("\r\n", responses);
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
