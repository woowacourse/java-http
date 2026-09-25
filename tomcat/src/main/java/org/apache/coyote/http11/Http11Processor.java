package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.UuidGenerator;
import org.apache.coyote.http11.model.FormParameters;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
        String requestPath = "unknown";
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            HttpRequest httpRequest = HttpRequest.from(reader);
            SessionManager sessionManager = new SessionManager();

            String jsessionid = httpRequest.getRequestHeader().cookie().getCookie("JSESSIONID");
            boolean isGenerated = false;
            Session session;
            if (jsessionid == null) {
                session = null;
            } else {
                session = sessionManager.findSession(jsessionid);
            }
            if (session == null) {
                session = new Session(UuidGenerator.generate());
                sessionManager.add(session);
                isGenerated = true;
            }

            String url = httpRequest.getRequestLine().requestUrl();
            String method = httpRequest.getRequestLine().httpMethod();
            UriInfo uriInfo = UriInfo.makeUriInfo(url);

            if ("GET".equals(method)) {
                getProcess(outputStream, uriInfo, isGenerated, session);
            } else if ("POST".equals(method)) {
                String requestBodyForm = httpRequest.getRequestBody().value();
                FormParameters requestBody = FormParameters.from(requestBodyForm);
                postProcess(outputStream, uriInfo, requestBody, isGenerated, session);
            }

        } catch (IOException | URISyntaxException | RuntimeException e) {
            log.error("HTTP 요청 처리 실패. path={}", requestPath, e);
        }
    }

    private void getProcess(
            OutputStream outputStream,
            UriInfo uriInfo,
            boolean isSessionGenerated,
            Session session
    ) throws IOException, URISyntaxException {
        String redirectPath;
        if ((redirectPath = RequestHandler.findGetRedirectPath(uriInfo.path(), session)) != null) {
            String responseHeader = buildResponseHeader(redirectPath);
            responseHeader = finishResponseHeader(responseHeader);
            outputStream.write(responseHeader.getBytes());
            outputStream.flush();
            return;
        }
        byte[] responseBody = RequestHandler.get(uriInfo.path());
        String responseHeader = buildResponseHeader(responseBody, findContentType(uriInfo.path()));
        if (isSessionGenerated) {
            responseHeader = addCookieToResponseHeader(responseHeader, session);
        }
        responseHeader = finishResponseHeader(responseHeader);
        outputStream.write(responseHeader.getBytes());
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private void postProcess(
            OutputStream outputStream,
            UriInfo uriInfo,
            FormParameters formParameters,
            boolean isSessionGenerated,
            Session session
    ) throws IOException, URISyntaxException {
        String redirectPath = RequestHandler.post(uriInfo, formParameters, session);
        String responseHeader = buildResponseHeader(redirectPath);
        if (isSessionGenerated) {
            responseHeader = addCookieToResponseHeader(responseHeader, session);
        }
        responseHeader = finishResponseHeader(responseHeader);
        outputStream.write(responseHeader.getBytes());
        outputStream.flush();
    }

    private String buildResponseHeader(String redirectPath) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + redirectPath,
                "Content-Length: 0");
    }

    private String addCookieToResponseHeader(String responseHeader, Session session) {
        return responseHeader +
                "\r\n" +
                "Set-Cookie: JSESSIONID=" + session.getId();
    }

    private String finishResponseHeader(String responseHeader) {
        return responseHeader + "\r\n\r\n";
    }

    private String buildResponseHeader(
            byte[] responseBody,
            String contentType
    ) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.length + " ");
    }

    private String findContentType(String url) {
        if (url.endsWith(".html")) {
            return "text/html";
        }
        if (url.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
