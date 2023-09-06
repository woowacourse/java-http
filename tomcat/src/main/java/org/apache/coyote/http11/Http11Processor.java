package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.MemberRegisterHandler;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCE_PATH = "static";
    private final Socket connection;
    private final List<RequestHandler> handlers = new ArrayList<>();

    public Http11Processor(final Socket connection) {
        this.connection = connection;

        final RequestHandler loginHandler = new LoginHandler("/login");
        final RequestHandler memberRegisterHandler = new MemberRegisterHandler("/register");
        handlers.add(loginHandler);
        handlers.add(memberRegisterHandler);
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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final ResponseInfo responseInfo = handleRequest(httpRequest);
            final String response = buildResponse(responseInfo);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseInfo handleRequest(final HttpRequest httpRequest) {
        for (RequestHandler handler : handlers) {
            if (handler.isMatch(httpRequest)) {
                return handler.doService(httpRequest);
            }
        }

        final String requestUri = httpRequest.getRequestLine().getRequestURI();
        final String resourcePath = RESOURCE_PATH + requestUri;
        return new ResponseInfo(getClass().getClassLoader().getResource(resourcePath), 200, "OK");
    }

    private String buildResponse(ResponseInfo responseInfo) throws IOException {
        final File location = new File(responseInfo.getResource().getFile());
        String responseBody = "";

        if (location.isDirectory()) {
            responseBody = "Hello world!";
        }

        if (location.isFile()) {
            responseBody = new String(Files.readAllBytes(location.toPath()));
        }
        if (responseInfo.getCookie() != null) {
            return String.join(
                    "\r\n",
                    "HTTP/1.1 " + responseInfo.getHttpStatusCode() + " " + responseInfo.getStatusMessage(),
                    "Content-Type: " + contentType(responseInfo.getResource().getPath()) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "Set-Cookie: " + "JSESSIONID=" + responseInfo.getCookie() + " ",
                    "",
                    responseBody);
        }

        return String.join(
                "\r\n",
                "HTTP/1.1 " + responseInfo.getHttpStatusCode() + " " + responseInfo.getStatusMessage() + " \r\n" +
                        "Content-Type: " + contentType(responseInfo.getResource().getPath()) + ";charset=utf-8 \r\n" +
                        "Content-Length: " + responseBody.getBytes().length + " \r\n\r\n" + responseBody);

    }

    private String contentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
