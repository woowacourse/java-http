package org.apache.coyote.http11;

import com.techcourse.controller.HttpController;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.http.common.startline.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestBody;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpController httpController;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.httpController = new HttpController();
        this.sessionManager = SessionManager.INSTANCE;
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
            HttpRequest httpRequest = HttpRequest.from(inputStream, sessionManager);

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
        try {
            return handleTargetMethod(httpRequest, method, path);
        } catch (UnauthorizedException e) {
            log.error("UnauthorizedException catch: {} {}", method, path, e);
            return HttpResponse.unauthorized().getResponseFormat();
        }
    }

    private String handleTargetMethod(final HttpRequest httpRequest, final HttpMethod method, final String path) {
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
        if (method == HttpMethod.GET && path.equals("/assets/chart-area.js")) {
            return getChartArea();
        }
        if (method == HttpMethod.GET && path.equals("/assets/chart-bar.js")) {
            return getChartBar();
        }
        if (method == HttpMethod.GET && path.equals("/assets/chart-pie.js")) {
            return getChartPie();
        }
        if (method == HttpMethod.GET && path.equals("/login")) {
            return getLoginHtml(httpRequest);
        }
        if (method == HttpMethod.POST && path.equals("/login")) {
            return login(httpRequest);
        }
        if (method == HttpMethod.GET && path.equals("/register")) {
            return getRegisterHtml();
        }
        if (method == HttpMethod.POST && path.equals("/register")) {
            return getRegister(httpRequest);
        }
        throw new IllegalArgumentException("대상 경로 메서드가 존재하지 않습니다: %s".formatted(method + " " + path));
    }

    private String getRegister(final HttpRequest httpRequest) {
        final HttpRequestBody body = httpRequest.getBody();
        byte[] bodyValue = body.getValue();
        String bodyLine = new String(bodyValue, StandardCharsets.UTF_8);
        Map<String, String> bodyElement = parseBodyValue(bodyLine);
        String account = bodyElement.get("account");
        String email = bodyElement.get("email");
        String password = bodyElement.get("password");
        final HttpResponse httpResponse = httpController.getRegister(account, email, password);
        return httpResponse.getResponseFormat();
    }

    private Map<String, String> parseBodyValue(final String target) {
        log.info("target: {}", target);
        final Map<String, String> bodyValue = new HashMap<>();
        final String[] elements = target.split("&");

        for (String element : elements) {
            final String[] values = element.split("=");
            final String key = URLDecoder.decode(values[0], StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(values[1], StandardCharsets.UTF_8);
            bodyValue.put(key, value);
        }

        return bodyValue;
    }

    private String getRegisterHtml() {
        final HttpResponse httpResponse = httpController.getRegisterHtml();
        return httpResponse.getResponseFormat();
    }

    private String login(final HttpRequest httpRequest) {
        final HttpRequestBody body = httpRequest.getBody();
        byte[] bodyValue = body.getValue();
        String jsonBody = new String(bodyValue, StandardCharsets.UTF_8);
        Map<String, String> jsonValue = parseBodyValue(jsonBody);
        String account = jsonValue.get("account");
        String password = jsonValue.get("password");
        final HttpResponse httpResponse = httpController.login(account, password);
        handleSessionCreation(httpResponse);
        return httpResponse.getResponseFormat();
    }

    private String getLoginHtml(final HttpRequest httpRequest) {
        final HttpResponse httpResponse = httpController.getLoginHtml(httpRequest);
        return httpResponse.getResponseFormat();
    }

    private String getJsScripts() {
        final HttpResponse httpResponse = httpController.getScripts();
        return httpResponse.getResponseFormat();
    }

    private String getChartBar() {
        final HttpResponse httpResponse = httpController.getChartBar();
        return httpResponse.getResponseFormat();
    }

    private String getChartPie() {
        final HttpResponse httpResponse = httpController.getChartPie();
        return httpResponse.getResponseFormat();
    }

    private String getChartArea() {
        final HttpResponse httpResponse = httpController.getChartArea();
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

    private void handleSessionCreation(final HttpResponse httpResponse) {
        Object userAttribute = httpResponse.getAttribute("session_user");
        if (userAttribute instanceof User) {
            final String sessionId = UUID.randomUUID().toString();
            final HttpSession session = new Session(sessionId);

            session.setAttribute("user", userAttribute);
            sessionManager.add(session);

            httpResponse.setCookie("JSESSIONID", sessionId);
        }
    }
}
