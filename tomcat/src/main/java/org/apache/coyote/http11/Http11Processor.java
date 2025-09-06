package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethodType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.MimeType;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final StaticFileHandler staticFileHandler = new StaticFileHandler();

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

            HttpRequest request = parseRequest(inputStream);
            if (request == null) {
                return;
            }

            HttpResponse response = createResponse(request);
            sendResponse(outputStream, response);
            
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final java.io.InputStream inputStream) throws IOException {
        try {
            return HttpRequest.from(inputStream);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 HTTP 요청: {}", e.getMessage());
            return null;
        }
    }

    private HttpResponse createResponse(final HttpRequest request) throws IOException {
        String requestPath = request.getPath();
        
        if (Objects.equals("/login", requestPath)) {
            handleLoginRequest(request);
        }
        
        String resolvedPath = resolveFilePath(requestPath);
        return generateHttpResponse(resolvedPath);
    }

    private void handleLoginRequest(final HttpRequest request) {
        if (request.getMethodType() != HttpMethodType.GET) {
            return;
        }
        
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        
        if (account != null && password != null) {
            processLoginCredentials(account, password);
        }
    }

    private void processLoginCredentials(final String account, final String password) {
        InMemoryUserRepository.findByAccount(account)
            .filter(user -> user.checkPassword(password))
            .ifPresentOrElse(
                user -> log.info("로그인 성공: {}", user),
                () -> log.info("로그인 실패 - account: {}, password: {}", account, password)
            );
    }

    private String resolveFilePath(final String requestPath) {
        String htmlPath = requestPath + ".html";
        if (staticFileHandler.exists(htmlPath)) {
            return htmlPath;
        }
        
        return requestPath;
    }

    private HttpResponse generateHttpResponse(final String requestPath) throws IOException {
        if ("/".equals(requestPath)) {
            return HttpResponse.ok("Hello world!", MimeType.TEXT_PLAIN);
        }
        
        return createFileResponse(requestPath);
    }

    private HttpResponse createFileResponse(final String requestPath) throws IOException {
        if (staticFileHandler.exists(requestPath)) {
            String fileContent = staticFileHandler.readFile(requestPath);
            MimeType mimeType = staticFileHandler.getContentType(requestPath);
            return HttpResponse.ok(fileContent, mimeType);
        } else {
            return HttpResponse.notFound();
        }
    }

    private void sendResponse(final java.io.OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
