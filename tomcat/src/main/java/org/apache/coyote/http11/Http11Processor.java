package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_FILE_LOCATION = "static";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final Http11Request request = readRequest(inputStream);

            final Http11Response response = findResponse(request);
            final byte[] responseMessage = response.toMessage();

            outputStream.write(responseMessage);
            outputStream.flush();
        } catch (final IllegalArgumentException e) {
            log.warn("bad request : {}", e.getMessage());
        } catch (final NoSuchFileException e) {
            log.warn("not found : {}", e.getMessage());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                connection.close();
            } catch (final IOException e) {
                log.error("failed to close connection : ", e);
            }
        }
    }

    private Http11Request readRequest(final InputStream requestInputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(requestInputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final List<String> requestMessage = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            requestMessage.add(line);
            if (line.isEmpty()) {
                break;
            }
        }

        final Optional<String> contentLengthHeader = requestMessage.stream()
                .filter(l -> l.startsWith("Content-Length:"))
                .map(l -> l.split(":")[1].trim())
                .findFirst();

        if (contentLengthHeader.isPresent()) {
            final int contentLength = Integer.parseInt(contentLengthHeader.get());
            final char[] bodyChars = new char[contentLength];
            bufferedReader.read(bodyChars, 0, contentLength);
            requestMessage.add(new String(bodyChars));
        }

        return Http11Request.create(requestMessage);
    }

    private Http11Response findResponse(final Http11Request request) throws IOException {
        final String requestMethod = request.getMethod();
        final String requestTarget = request.getTarget();

        if (requestMethod.equals("GET")) {
            if (requestTarget.equals("/"))  {
                final byte[] defaultResponseBytes = "Hello world!".getBytes(StandardCharsets.UTF_8);

                return createHtmlResponse(200, defaultResponseBytes);
            }
            if (requestTarget.contains("/login")) {
                return handleLoginRequest(request);
            }
            if (requestTarget.endsWith("/register")) {
                return handleRegisterRequest(request);
            }
            if (requestTarget.endsWith(".html")) {
                return handleHtmlRequest(200, requestTarget);
            }
            if (requestTarget.endsWith(".css")) {
                return handleCssRequest(requestTarget);
            }
            if (requestTarget.endsWith(".js")) {
                return handleJsResponse(requestTarget);
            }
        }

        if (requestMethod.equals("POST")) {
            if (requestTarget.endsWith("/register")) {
                return handleRegisterRequest(request);
            }
        }

        throw new NoSuchFileException(requestTarget);
    }

    private Http11Response handleLoginRequest(final Http11Request request) throws IOException {
        final byte[] fileContent = readFile("/login.html");

        final Optional<String> account = request.findQueryParam("account");
        final Optional<String> password = request.findQueryParam("password");

        if (account.isEmpty() || password.isEmpty()) {
            return createHtmlResponse(200, fileContent);
        }

        if (existsUserByAccount(account.get(), password.get())) {
            return handleHtmlRequest(302, "/index.html");
        }

        return handleHtmlRequest(401, "/401.html");

    }

    private Http11Response handleRegisterRequest(final Http11Request request) throws IOException {
        if (request.getMethod().equals("GET")) {
            return handleHtmlRequest(200, "/register.html");
        }

        final String body = request.getBody();

        final Map<String, String> urlEncodedResponseBody = getUrlEncodedBody(body);

        final String account = urlEncodedResponseBody.get("account");
        final String email = urlEncodedResponseBody.get("email");
        final String password = urlEncodedResponseBody.get("password");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException(String.format("Already signed up : account = %s", account));
        }

        InMemoryUserRepository.save(new User(account, email, password));

        return handleHtmlRequest(302, "/index.html");
    }

    private Http11Response handleHtmlRequest(
            final int statusCode,
            final String requestTarget
    ) throws IOException {
        final byte[] fileContent = readFile(requestTarget);

        return createHtmlResponse(statusCode, fileContent);
    }

    private Http11Response handleCssRequest(final String requestTarget) throws IOException {
        final byte[] fileContent = readFile(requestTarget);

        return createCssResponse(fileContent);
    }

    private Http11Response handleJsResponse(final String requestTarget) throws IOException {
        final byte[] fileContent = readFile(requestTarget);

        return createJsResponse(fileContent);
    }

    private byte[] readFile(final String location) throws IOException {
        try (final InputStream fileInputStream = new FileInputStream(getClass().getClassLoader().getResource(STATIC_FILE_LOCATION + location).getPath())) {
            return fileInputStream.readAllBytes();
        } catch (final NullPointerException e) {
            throw new NoSuchFileException(location);
        }
    }

    private boolean existsUserByAccount(final String account, final String password) {
        final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);

        if (userOrEmpty.isEmpty()) {
            log.warn("User not found : account = {}", account);
            return false;
        }

        final User user = userOrEmpty.get();
        if (!user.checkPassword(password)) {
            log.warn("Wrong password : account = {}", account);
            return false;
        }

        log.info("User found : {}", user);
        return true;
    }

    private Http11Response createHtmlResponse(
            final int statusCode,
            final byte[] body
    ) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.length));

        return new Http11Response(
                "HTTP/1.1",
                statusCode,
                "OK",
                headers,
                body
        );
    }

    private Http11Response createCssResponse(final byte[] body) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/css;charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.length));

        return new Http11Response(
                "HTTP/1.1",
                200,
                "OK",
                headers,
                body
        );
    }

    private Http11Response createJsResponse(final byte[] body) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/javascript;charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.length));

        return new Http11Response(
                "HTTP/1.1",
                200,
                "OK",
                headers,
                body
        );
    }

    private static Map<String, String> getUrlEncodedBody(final String body) {
        final Map<String, String> urlEncodedResponseBody = new HashMap<>();
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("Fill the register form");
        }

        final String[] pairs = body.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                urlEncodedResponseBody.put(keyValue[0], keyValue[1]);
                continue;
            }
            throw new IllegalArgumentException(String.format("Wrong x-www-form-urlencoded response : %s", pair));
        }
        return urlEncodedResponseBody;
    }
}
