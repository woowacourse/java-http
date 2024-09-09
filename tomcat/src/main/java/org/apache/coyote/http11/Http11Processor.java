package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.service.UserService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HtmlReader htmlReader = HtmlReader.getInstance();

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);
            String path = httpRequest.getPath();

            HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1)
                    .addHttpStatusCode(HttpStatusCode.NOT_FOUND);

            if (path.equals("/")) {
                getRoot(httpResponse);
            } else if (path.endsWith(".html")) {
                getHtml(httpResponse, path);
            } else if (path.startsWith("/css") || path.startsWith("/js")) {
                getStaticResource(httpResponse, path);
            } else if (path.startsWith("/login")) {
                login(httpResponse, httpRequest);
            } else if (path.equals("/register")) {
                register(httpResponse, httpRequest);
            }

            String response = HttpResponseParser.parse(httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void getRoot(HttpResponse httpResponse) {
        httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                .addHttpStatusCode(HttpStatusCode.OK)
                .addResponseBody("Hello world!");
    }

    private void getHtml(HttpResponse httpResponse, String path) throws IOException {
        String responseBody = htmlReader.loadHtmlAsString(path);
        httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                .addHttpStatusCode(HttpStatusCode.OK)
                .addResponseBody(responseBody);
    }

    private void getStaticResource(HttpResponse httpResponse, String path) throws IOException {
        int lastIndexOfDot = path.lastIndexOf(".");
        String postfix = path.substring(lastIndexOfDot + 1);
        String responseBody = htmlReader.loadHtmlAsString(path);
        httpResponse.addContentType(ContentType.from(postfix))
                .addHttpStatusCode(HttpStatusCode.OK)
                .addResponseBody(responseBody);
    }

    private void login(HttpResponse httpResponse, HttpRequest httpRequest) throws IOException {
        HttpMethod httpRequestMethod = httpRequest.getHttpMethod();
        if (httpRequestMethod == HttpMethod.POST) {
            String redirectUrl = "/index.html";
            HttpRequestParameter requestParameter = httpRequest.getHttpRequestParameter();
            try {
                String sessionId = UserService.login(requestParameter);
                httpResponse.addHttpStatusCode(HttpStatusCode.FOUND)
                        .addCookie("JSESSIONID", sessionId)
                        .addCookie("Max-Age", "600")
                        .addRedirectUrl(redirectUrl);
            } catch (IllegalArgumentException e) {
                httpResponse.addHttpStatusCode(HttpStatusCode.FOUND)
                        .addRedirectUrl("/401.html");
            }
        } else if (httpRequestMethod == HttpMethod.GET && !validateSession(httpRequest.getSessionId())) {
            String responseBody = htmlReader.loadHtmlAsString("login.html");
            httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                    .addHttpStatusCode(HttpStatusCode.OK)
                    .addResponseBody(responseBody);
        } else if (httpRequestMethod == HttpMethod.GET && validateSession(httpRequest.getSessionId())) {
            httpResponse.addHttpStatusCode(HttpStatusCode.FOUND)
                    .addRedirectUrl("/index.html");
        }
    }

    private void register(HttpResponse httpResponse, HttpRequest httpRequest) throws IOException {
        HttpMethod httpRequestMethod = httpRequest.getHttpMethod();
        if (httpRequestMethod == HttpMethod.POST) {
            String redirectUrl = "/index.html";
            HttpRequestParameter requestParameter = httpRequest.getHttpRequestParameter();
            try {
                UserService.createUser(requestParameter);
            } catch (IllegalArgumentException e) {
                redirectUrl = "/400.html";
            }
            httpResponse.addHttpStatusCode(HttpStatusCode.FOUND)
                    .addRedirectUrl(redirectUrl);
        } else if (httpRequestMethod == HttpMethod.GET) {
            String responseBody = htmlReader.loadHtmlAsString("register.html");
            httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                    .addHttpStatusCode(HttpStatusCode.OK)
                    .addResponseBody(responseBody);
        }
    }

    private boolean validateSession(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        return SessionManager.findSession(sessionId) != null;
    }
}
