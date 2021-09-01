package nextstep.jwp;

import nextstep.jwp.controller.dto.UserRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.httpmessage.*;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String STATIC_PATH = "static";

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
            final HttpResponse httpResponse = new HttpResponse();
            final UserService userService = new UserService();

            final String extractedUri = httpRequest.getPath();
            final HttpMethod extractedMethod = httpRequest.getHttpMethod();

            if ("/".equals(extractedUri)) {
                writeHttpResponseWithBody(httpResponse, ContentType.HTML, DEFAULT_RESPONSE_BODY);
                writeOutputStream(outputStream, httpResponse.getHttpMessage());
                return;
            }
            if (extractedUri.endsWith(".ico")) {
                writeHttpResponseWithBody(httpResponse, ContentType.ICON, responseBodyByStaticURLPath(extractedUri));
                writeOutputStream(outputStream, httpResponse.getHttpMessage());
                return;
            }

            if (extractedUri.endsWith(".html")) {
                writeHttpResponseWithBody(httpResponse, ContentType.HTML, responseBodyByStaticURLPath(extractedUri));
                writeOutputStream(outputStream, httpResponse.getHttpMessage());
                return;
            }

            if (extractedUri.endsWith(".css")) {
                writeHttpResponseWithBody(httpResponse, ContentType.CSS, responseBodyByStaticURLPath(extractedUri));
                writeOutputStream(outputStream, httpResponse.getHttpMessage());
                return;
            }

            if (extractedUri.endsWith(".js")) {
                writeHttpResponseWithBody(httpResponse, ContentType.JAVASCRIPT, responseBodyByStaticURLPath(extractedUri));
                writeOutputStream(outputStream, httpResponse.getHttpMessage());
                return;
            }

            if (extractedUri.startsWith("/assets/img")) {
                writeHttpResponseWithBody(httpResponse, ContentType.IMAGE, responseBodyByStaticURLPath(extractedUri));
                writeOutputStream(outputStream, httpResponse.getHttpMessage());
                return;
            }


            if ("/login".equals(extractedUri)) {
                if (HttpMethod.POST.equals(extractedMethod)) {
                    try {
                        userService.login(new UserRequest(httpRequest.getParameter("account"), httpRequest.getParameter("password")));
                        writeHttpResponseWithRedirect(httpResponse, getPathWithOrigin(httpRequest, "/index.html"));
                        writeOutputStream(outputStream, httpResponse.getHttpMessage());
                    } catch (RuntimeException exception) {
                        writeHttpResponseWithRedirect(httpResponse, getPathWithOrigin(httpRequest, "/401.html"));
                        writeOutputStream(outputStream, httpResponse.getHttpMessage());
                    }
                    return;
                }
                writeHttpResponseWithBody(httpResponse, ContentType.HTML, responseBodyByStaticURLPath(extractedUri + ".html"));
                writeOutputStream(outputStream, httpResponse.getHttpMessage());
                return;
            }

            if ("/register".equals(extractedUri)) {
                if (HttpMethod.POST.equals(extractedMethod)) {
                    try {
                        userService.register(new UserRequest(httpRequest.getParameter("account"), httpRequest.getParameter("password"), httpRequest.getParameter("email")));
                        writeHttpResponseWithRedirect(httpResponse, getPathWithOrigin(httpRequest, "/index.html"));
                        writeOutputStream(outputStream, httpResponse.getHttpMessage());
                    } catch (RuntimeException exception) {
                        writeHttpResponseWithRedirect(httpResponse, getPathWithOrigin(httpRequest, "/401.html"));
                        writeOutputStream(outputStream, httpResponse.getHttpMessage());
                    }
                    return;
                }
                writeHttpResponseWithBody(httpResponse, ContentType.HTML, responseBodyByStaticURLPath(extractedUri + ".html"));
                writeOutputStream(outputStream, httpResponse.getHttpMessage());
                return;
            }

            writeHttpResponseWithRedirect(httpResponse, getPathWithOrigin(httpRequest, "/404.html"));
            writeOutputStream(outputStream, httpResponse.getHttpMessage());
        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String getPathWithOrigin(HttpRequest httpRequest, String uri) {
        return "http://" + httpRequest.getHeader("Host") + uri;
    }

    private void writeOutputStream(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void writeHttpResponseWithBody(HttpResponse httpResponse, ContentType contentType, String body) {
        httpResponse.addHeader("Content-Type", contentType.getContentType());
        httpResponse.addHeader("Content-Length", body.getBytes().length);
        httpResponse.setBody(body);
    }

    private void writeHttpResponseWithRedirect(HttpResponse httpResponse, String redirectUri) {
        httpResponse.setStatusLine(new StatusLine(HttpVersion.HTTP1_1, HttpStatusCode.FOUND));
        httpResponse.addHeader("Location", redirectUri);
    }

    private String responseBodyByStaticURLPath(String targetUri) {
        try {
            final URL url = getClass().getClassLoader().getResource(STATIC_PATH + targetUri);
            return Files.readString(new File(url.getFile()).toPath());
        } catch (IOException exception) {
            LOG.info("리소스를 가져오려는 url이 존재하지 않습니다. 입력값: {}", STATIC_PATH + targetUri);
            throw new IllegalStateException(String.format("리소스를 가져오려는 url이 존재하지 않습니다. 입력값: %s",
                    STATIC_PATH + targetUri));
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
