package nextstep.jwp.server.http.request;

import nextstep.jwp.server.RequestHandler;
import nextstep.jwp.server.exception.BadRequestException;
import nextstep.jwp.server.http.common.HttpCookie;
import nextstep.jwp.server.http.common.HttpHeaders;
import nextstep.jwp.server.http.common.HttpSession;
import nextstep.jwp.server.http.common.HttpSessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String FORM_TYPE = "application/x-www-form-urlencoded";

    private final BufferedReader bufferedReader;
    private RequestLine requestLine;
    private HttpHeaders headers;
    private Parameters parameters;
    private HttpCookie httpCookie;

    public HttpRequest(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        readLines();
    }

    private void readLines() {
        try {
            String line = bufferedReader.readLine();
            if (line == null) return;
            this.requestLine = new RequestLine(line);
            this.headers = parseHeaders();
            this.parameters = parseParameters();
            this.httpCookie = parseCookie();
        } catch (IOException e) {
            log.error("http request read lines exception", e);
            throw new BadRequestException();
        }
    }

    private HttpCookie parseCookie() {
        String cookies = getHeaders().get("Cookie");
        if (Objects.isNull(cookies)) {
            return new HttpCookie();
        }
        return new HttpCookie(cookies);
    }

    private HttpHeaders parseHeaders() throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !"".equals(line)) {
            String[] splitHeader = line.split(": ");
            headers.put(splitHeader[0].trim(), splitHeader[1].trim());
        }
        return new HttpHeaders(headers);
    }

    private Parameters parseParameters() throws IOException {
        Parameters parameters = new Parameters();
        parameters.addParameters(getQueryString());
        String length = getHeaders().get("Content-Length");
        if (!FORM_TYPE.equals(getHeaders().get("Content-Type")) || length == null) {
            return parameters;
        }
        int contentLength = Integer.parseInt(length);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        parameters.addParameters(new String(buffer));
        return parameters;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getQueryString() {
        return requestLine.getQueryString();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public Map<String, String> getCookie() {
        return httpCookie.getCookie();
    }

    public HttpSession getSession() {
        String sessionId = getCookie().get("JSESSIONID");
        if (sessionId == null) {
            String id = UUID.randomUUID().toString();
            HttpSession httpSession = new HttpSession(id);
            HttpSessions.addSession(id, httpSession);
            return httpSession;
        }
        return HttpSessions.findSession(sessionId);
    }
}
