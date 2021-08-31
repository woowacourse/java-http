package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.httpmessage.ContentType;
import nextstep.jwp.httpmessage.HttpMessageReader;
import nextstep.jwp.httpmessage.HttpMethod;
import nextstep.jwp.httpmessage.HttpRequest;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String STATIC_PATH = "static";
    private static final String HEADER_DELIMITER = ": ";

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final HttpRequest httpRequest = new HttpRequest(new HttpMessageReader(inputStream));
            final String extractedUri = httpRequest.getPath();
            final HttpMethod extractedMethod = httpRequest.getHttpMethod();

            if ("/".equals(extractedUri)) {
                final String response = http200Message(DEFAULT_RESPONSE_BODY, ContentType.HTML.getContentType());
                writeOutputStream(outputStream, response);
                return;
            }

            if (extractedUri.endsWith(".html")) {
                writeOutputStream(outputStream, httpStaticFileResponse(STATIC_PATH, extractedUri, ContentType.HTML));
                return;
            }

            if (extractedUri.endsWith(".css")) {
                writeOutputStream(outputStream, httpStaticFileResponse(STATIC_PATH, extractedUri, ContentType.CSS));
                return;
            }

            if (extractedUri.endsWith(".js")) {
                writeOutputStream(outputStream, httpStaticFileResponse(STATIC_PATH, extractedUri, ContentType.JAVASCRIPT));
                return;
            }

            if (extractedUri.startsWith("/assets/img")) {
                writeOutputStream(outputStream, httpStaticFileResponse(STATIC_PATH, extractedUri, ContentType.IMAGE));
                return;
            }

            if ("/login".equals(extractedUri)) {
                if (HttpMethod.POST.equals(extractedMethod)) {
                    try {
                        loginRequest(httpRequest);
                        writeOutputStream(outputStream, http302Response("/index.html"));
                    } catch (RuntimeException exception) {
                        writeOutputStream(outputStream, http302Response("/401.html"));
                    }
                    return;
                }
                writeOutputStream(outputStream, httpStaticFileResponse(STATIC_PATH, extractedUri + ".html", ContentType.HTML));
                return;
            }

            if ("/register".equals(extractedUri)) {
                if (HttpMethod.POST.equals(extractedMethod)) {
                    try {
                        registerRequest(httpRequest);
                        writeOutputStream(outputStream, http302Response("/index.html"));
                    } catch (RuntimeException exception) {
                        writeOutputStream(outputStream, http302Response("/401.html"));
                    }
                    return;
                }
                writeOutputStream(outputStream, httpStaticFileResponse(STATIC_PATH, extractedUri + ".html", ContentType.HTML));
                return;
            }

            writeOutputStream(outputStream, http302Response("/404.html"));
        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String loginRequest(HttpRequest httpRequest) {
        final User user = findUserByAccount(httpRequest.getParameter("account"));
        if (user.checkPassword(httpRequest.getParameter("password"))) {
            return user.toString();
        }
        LOG.info("옳지 않은 비밀번호입니다.");
        throw new IllegalArgumentException("옳지 않은 비밀번호입니다.");
    }

    private User findUserByAccount(String account) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            return user.get();
        }
        LOG.info("찾을 수 없는 사용자입니다.");
        throw new IllegalArgumentException("찾을 수 없는 사용자입니다.");
    }

    private void registerRequest(HttpRequest httpRequest) {
        final User user = new User(httpRequest.getParameter("account"), httpRequest.getParameter("password"), httpRequest.getParameter("email"));
        InMemoryUserRepository.save(user);
    }

    private void writeOutputStream(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String httpStaticFileResponse(String resourcesPath, String targetUri, ContentType contentType) {
        return http200Message(responseBodyByURLPath(resourcesPath, targetUri), contentType.getContentType());
    }

    private String responseBodyByURLPath(String resourcesPath, String targetUri) {
        try {
            final URL url = getClass().getClassLoader().getResource(resourcesPath + targetUri);
            return Files.readString(new File(url.getFile()).toPath());
        } catch (IOException exception) {
            LOG.info("리소스를 가져오려는 url이 존재하지 않습니다. 입력값: {}", resourcesPath + targetUri);
            throw new IllegalStateException(String.format("리소스를 가져오려는 url이 존재하지 않습니다. 입력값: %s",
                    resourcesPath + targetUri));
        }
    }

    private String http200Message(String responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String http302Response(String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: http://localhost:8080" + redirectUrl);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
