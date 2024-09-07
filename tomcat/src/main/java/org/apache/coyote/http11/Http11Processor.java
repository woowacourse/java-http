package org.apache.coyote.http11;

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
            HttpResponseBody httpResponseBody = new HttpResponseBody(
                    fileReader.readFile(httpRequest.getHttpRequestPath()));
            if (httpRequest.getHttpRequestPath().equals("/login")) {
                login(httpRequest);
            }

            HttpResponse httpResponse = mapToHttpResponse(HttpStatusCode.OK, httpRequest, httpResponseBody);
            String response = httpResponseParser.parseResponse(httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse mapToHttpResponse(HttpStatusCode code, HttpRequest request, HttpResponseBody responseBody) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", request.getContentType() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.body().getBytes().length));
        HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders(headers);
        return new HttpResponse(code, httpResponseHeaders, responseBody);
    }

    private void login(HttpRequest httpRequest) {
        String account = httpRequest.getQueryParameter("account");
        String password = httpRequest.getQueryParameter("password");
        User foundUser = InMemoryUserRepository.findByAccount(account).orElseThrow(IllegalArgumentException::new);
        if (foundUser.checkPassword(password)) {
            log.info("user : " + foundUser);
        }
    }
}
