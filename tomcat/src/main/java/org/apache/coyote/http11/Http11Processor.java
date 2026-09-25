package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.UuidGenerator;
import org.apache.coyote.http11.model.request.FormParameters;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.UriInfo;
import org.apache.coyote.http11.model.response.Http11Response;
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

            Http11Response response;
            if ("GET".equals(method)) {
                response = getProcess(uriInfo, session);
            } else if ("POST".equals(method)) {
                String requestBodyForm = httpRequest.getRequestBody().value();
                FormParameters requestBody = FormParameters.from(requestBodyForm);
                response = postProcess(uriInfo, requestBody, session);
            } else {
                return;
            }

            if (isGenerated) {
                response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            }
            response.writeTo(outputStream);

        } catch (IOException | URISyntaxException | RuntimeException e) {
            log.error("HTTP 요청 처리 실패. path={}", requestPath, e);
        }
    }

    private Http11Response getProcess(
            UriInfo uriInfo,
            Session session
    ) throws IOException, URISyntaxException {
        String redirectPath;
        if ((redirectPath = RequestHandler.findGetRedirectPath(uriInfo.path(), session)) != null) {
            return Http11Response.redirect(redirectPath);
        }
        byte[] responseBody = RequestHandler.get(uriInfo.path());
        return Http11Response.ok(responseBody, findContentType(uriInfo.path()));
    }

    private Http11Response postProcess(
            UriInfo uriInfo,
            FormParameters formParameters,
            Session session
    ) {
        String redirectPath = RequestHandler.post(uriInfo, formParameters, session);
        return Http11Response.redirect(redirectPath);
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
