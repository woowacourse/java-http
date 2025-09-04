package org.apache.coyote.http11;

import com.techcourse.controller.HttpController;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.http.common.startline.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpController httpController;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.httpController = new HttpController();
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
            HttpRequest httpRequest = HttpRequest.from(inputStream);

            String response = findTargetMethod(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String findTargetMethod(final HttpRequest httpRequest) throws IOException, URISyntaxException {
        HttpMethod method = httpRequest.getMethod();
        String path = httpRequest.getPath();

        if (method == HttpMethod.GET && path.equals("/")) {
            return helloWorld();
        }
        if (method == HttpMethod.GET && path.equals("/index.html")) {
            return getIndexHtml();
        }
        if (method == HttpMethod.GET && path.equals("/css/styles.css")) {
            return getCssStyles();
        }
        if (method == HttpMethod.GET && path.equals("/js/scripts.js")) {
            return getJsScripts();
        }
        if (method == HttpMethod.GET && path.equals("/login")) {
            return getLoginHtml(httpRequest);
        }
        throw new IllegalArgumentException("대상 경로 메서드가 존재하지 않습니다: %s".formatted(method + " " + path));
    }

    private String getLoginHtml(final HttpRequest httpRequest) {
        final String account = httpRequest.getTargetQueryParameter("account");
        final String password = httpRequest.getTargetQueryParameter("password");

        final HttpResponse httpResponse = httpController.login(account, password);
        return httpResponse.getResponseFormat();
    }

    private String getJsScripts() {
        final HttpResponse httpResponse = httpController.getScripts();
        return httpResponse.getResponseFormat();
    }

    private String getCssStyles() {
        final HttpResponse httpResponse = httpController.getCssStyles();
        return httpResponse.getResponseFormat();
    }

    private String getIndexHtml() {
        final HttpResponse httpResponse = httpController.getIndex();
        return httpResponse.getResponseFormat();
    }

    private String helloWorld() {
        final HttpResponse httpResponse = httpController.helloWorld();
        return httpResponse.getResponseFormat();
    }
}
