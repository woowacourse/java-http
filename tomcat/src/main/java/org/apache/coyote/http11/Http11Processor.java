package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticPageController;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeaders;
import org.apache.coyote.http11.response.HttpResponseParser;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
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
            HttpRequestParser httpRequestParser = new HttpRequestParser();
            HttpResponseParser httpResponseParser = new HttpResponseParser();
            FileReader fileReader = FileReader.getInstance();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = httpRequestParser.parseRequest(bufferedReader);

            String requestedFilePath = httpRequest.getHttpRequestPath();
            HttpStatusCode httpStatusCode = HttpStatusCode.OK;

            HttpResponseBody httpResponseBody = new HttpResponseBody(
                    fileReader.readFile(requestedFilePath));
            HttpResponse httpResponse = mapToHttpResponse(HttpStatusCode.OK, httpRequest, httpResponseBody);
            if (httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
                httpResponse = StaticPageController.getStaticPage(httpRequest);
            }
            if (httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
                if (httpRequest.getHttpRequestPath().contains("/login")) {
                    httpResponse = LoginController.login(httpRequest);
                }
                if (httpRequest.getHttpRequestPath().contains("/register")) {
                    httpResponse = RegisterController.register(httpRequest);
                }
            }

            String response = httpResponseParser.parseResponse(httpResponse);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse mapToHttpResponse(HttpStatusCode code, HttpRequest request, HttpResponseBody responseBody) {
        HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders(new HashMap<>());
        httpResponseHeaders.setContentType(request);
        httpResponseHeaders.setContentLength(responseBody);
        return new HttpResponse(code, httpResponseHeaders, responseBody);
    }
}
