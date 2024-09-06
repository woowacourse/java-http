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
            HttpMethod httpRequestMethod = httpRequest.getHttpMethod();

            HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

            if (path.equals("/")) {
                httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                        .addHttpStatusCode(HttpStatusCode.OK)
                        .addResponseBody("Hello world!");
            } else if (path.endsWith(".html")) {
                String responseBody = htmlReader.loadHtmlAsString(path);
                httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                        .addHttpStatusCode(HttpStatusCode.OK)
                        .addResponseBody(responseBody);
            } else if (path.endsWith(".css")) {
                String responseBody = htmlReader.loadHtmlAsString(path);
                httpResponse.addContentType(new ContentType(MediaType.CSS, null))
                        .addHttpStatusCode(HttpStatusCode.OK)
                        .addResponseBody(responseBody);
            } else if (path.endsWith(".js")) {
                String responseBody = htmlReader.loadHtmlAsString(path);
                httpResponse.addContentType(new ContentType(MediaType.JAVASCRIPT, null))
                        .addHttpStatusCode(HttpStatusCode.OK)
                        .addResponseBody(responseBody);
            } else if (path.startsWith("/login")) {
                if (httpRequestMethod == HttpMethod.POST) {
                    String redirectUrl = "/index.html";
                    HttpRequestParameter requestParameter = httpRequest.getHttpRequestParameter();
                    try {
                        String sessionId = UserService.login(requestParameter);
                        httpResponse.addHttpStatusCode(HttpStatusCode.FOUND)
                                .addCookie("JSESSIONID", sessionId)
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
            } else if (path.equals("/register")) {
                if (httpRequestMethod == HttpMethod.POST) {
                    String redirectUrl = "/index.html";
                    HttpRequestParameter requestParameter = httpRequest.getHttpRequestParameter();
                    try {
                        UserService.saveUser(requestParameter);
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

            String response = HttpResponseParser.parse(httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean validateSession(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        return SessionManager.findSession(sessionId) != null;
    }
}
