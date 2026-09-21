package org.apache.coyote.http11;

import static org.reflections.Reflections.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();
            if (line == null) {
                return;
            }
            processRequestLine(line);

            line = br.readLine();
            while (!line.equals("")) {
                if (line.isEmpty() || line.equals("\r") || line.equals("\n")) {
                    break;
                }
                log.debug("header : {}", line);
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }

            if ("POST".equals(method)) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                char[] buffer = new char[contentLength];
                int bytesRead = br.read(buffer, 0, contentLength);
                String requestBody = new String(buffer, 0, bytesRead);
                params = HttpRequestUtils.parseQueryString(requestBody);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public int getParamSize() {
        return params.size();
    }

    public HttpCookie getCookies() {
        return new HttpCookie(getHeader("Cookie"));
    }

    public Session getSession() {
        return SessionManager.getSession(getCookies().getCookie("JSESSIONID"));
    }

    private void processRequestLine(String requestLine) {
        log.debug("request line : {}", requestLine);
        String[] tokens = requestLine.split(" ");
        method = tokens[0];

        if ("POST".equals(method)) {
            path = tokens[1];
            return;
        }

        int index = tokens[1].indexOf("?");
        path = tokens[1];
        if (index != -1) {
            path = tokens[1].substring(0, index);
            params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
        }
    }
}
