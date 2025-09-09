package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.SessionManager;
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
    private static final SessionManager sessionManager = SessionManager.getInstance();

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

                return Http11Response.createHtmlResponse(HttpStatus.OK, defaultResponseBytes);
            }
            if (requestTarget.contains("/login")) {
                return handleLoginRequest(request);
            }
            if (requestTarget.endsWith("/register")) {
                return handleRegisterRequest(request);
            }
            if (requestTarget.endsWith(".html")) {
                return handleHtmlRequest(HttpStatus.OK, requestTarget);
            }
            if (requestTarget.endsWith(".css")) {
                return handleCssRequest(requestTarget);
            }
            if (requestTarget.endsWith(".js")) {
                return handleJsResponse(requestTarget);
            }
            if (requestTarget.endsWith(".svg")) {
                return handleImgResponse(requestTarget);
            }
        }

        if (requestMethod.equals("POST")) {
            if (requestTarget.contains("/login")) {
                return handleLoginRequest(request);
            }
            if (requestTarget.endsWith("/register")) {
                return handleRegisterRequest(request);
            }
        }

        return Http11Response.createRedirectResponse("/404.html");
    }

    private Http11Response handleLoginRequest(final Http11Request request) throws IOException {
        if (request.getMethod().equals("GET")) {
            final Optional<String> sessionId = request.findCookie("JSESSIONID");

            if (sessionId.isPresent()) {
                final Session session = sessionManager.findSession(sessionId.get());
                if (session != null) {
                    return Http11Response.createRedirectResponse("/index.html");
                }
            }

            final byte[] fileContent = readFile("/login.html");

            return Http11Response.createHtmlResponse(HttpStatus.OK, fileContent);
        }

        final Map<String, String> body = request.getBodyByContentType("application/x-www-form-urlencoded");

        final String account = body.get("account");
        final String password = body.get("password");

        final Optional<User> userOrEmpty = findUserByAccount(account, password);
        if (userOrEmpty.isEmpty()) {
            return Http11Response.createRedirectResponse("/401.html");
        }

        final User user = userOrEmpty.get();
        return handleAuthorizedRequest(user, request);
    }

    private Http11Response handleRegisterRequest(final Http11Request request) throws IOException {
        if (request.getMethod().equals("GET")) {
            return handleHtmlRequest(HttpStatus.OK, "/register.html");
        }

        final Map<String, String> urlEncodedResponseBody = request.getBodyByContentType("application/x-www-form-urlencoded");

        final String account = urlEncodedResponseBody.get("account");
        final String email = urlEncodedResponseBody.get("email");
        final String password = urlEncodedResponseBody.get("password");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException(String.format("Already signed up : account = %s", account));
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return handleAuthorizedRequest(user, request);
    }

    private Http11Response handleAuthorizedRequest(final User user, final Http11Request request) throws IOException {
        final String sessionId = generateSessionID();

        final Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Set-Cookie", String.format("JSESSIONID=%s", sessionId));

        return Http11Response.createRedirectResponse("/index.html", headers);
    }

    private Http11Response handleHtmlRequest(
            final HttpStatus status,
            final String requestTarget
    ) throws IOException {
        final byte[] fileContent = readFile(requestTarget);

        return Http11Response.createHtmlResponse(status, fileContent);
    }

    private Http11Response handleCssRequest(final String requestTarget) throws IOException {
        final byte[] fileContent = readFile(requestTarget);

        return Http11Response.createCssResponse(HttpStatus.OK, fileContent);
    }

    private Http11Response handleJsResponse(final String requestTarget) throws IOException {
        final byte[] fileContent = readFile(requestTarget);

        return Http11Response.createJsResponse(HttpStatus.OK, fileContent);
    }

    private Http11Response handleImgResponse(final String requestTarget) throws IOException {
        final byte[] fileContent = readFile(requestTarget);

        return Http11Response.createSvgResponse(HttpStatus.OK, fileContent);
    }

    private byte[] readFile(final String location) throws IOException {
        try (final InputStream fileInputStream = new FileInputStream(getClass().getClassLoader().getResource(STATIC_FILE_LOCATION + location).getPath())) {
            return fileInputStream.readAllBytes();
        } catch (final NullPointerException e) {
            throw new NoSuchFileException(location);
        }
    }

    private String generateSessionID() {
        final UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private Optional<User> findUserByAccount(
            final String account,
            final String password
    ) {
        final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);

        if (userOrEmpty.isEmpty()) {
            log.warn("User not found : account = {}", account);
            return Optional.empty();
        }

        final User user = userOrEmpty.get();
        if (!user.checkPassword(password)) {
            log.warn("Wrong password : account = {}", account);
            return Optional.empty();
        }

        log.info("User found : {}", user);
        return Optional.of(user);
    }
}
