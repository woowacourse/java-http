package com.java.http.request_response;

import com.java.http.session.Session;
import com.java.http.session.SessionManager;
import com.java.http.session.SessionStore;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.java.http.request_response.Headers.EMPTY;

public record HttpRequest(
        HttpMethod method,
        String uri,
        Map<String, String> paramMap,
        Headers headers,
        Body body
) {

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        List<String> startLineAndHeaders = readUntilNewLine(reader);

        String[] methodAndUri = startLineAndHeaders.getFirst().split(" ");
        HttpMethod httpMethod = HttpMethod.parse(methodAndUri[0]);
        String uri;
        Map<String, String> paramMap;
        if (!methodAndUri[1].contains("?")) {
            uri = methodAndUri[1];
            paramMap = Collections.emptyMap();
        } else {
            String[] uriAndParams = methodAndUri[1].split("\\?");
            uri = uriAndParams[0];
            paramMap = Arrays.stream(uriAndParams[1].split("&"))
                    .map(param -> param.split("="))
                    .filter(param -> param.length == 2)
                    .collect(Collectors.toMap(param -> param[0], param -> param[1]));
        }

        Headers headers = EMPTY;
        if (startLineAndHeaders.size() >= 2) {
            headers = Headers.from(startLineAndHeaders.subList(1, startLineAndHeaders.size() - 1));
        }

        Body body = Body.EMPTY;
        String contentLength = headers.valueOf("Content-Length");
        if (contentLength != null) {
            String bodyValue = readBy(reader, Integer.parseInt(contentLength));
            body = Body.plaintext(bodyValue);
        }

        return new HttpRequest(httpMethod, uri, paramMap, headers, body);
    }

    private static List<String> readUntilNewLine(BufferedReader reader) throws IOException {
        List<String> result = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
            if (line.isEmpty()) {
                break;
            }
        }

        return result;
    }

    private static String readBy(BufferedReader reader, int contentLength) throws IOException {
        char[] bodyChars = new char[contentLength];
        int read = reader.read(bodyChars, 0, contentLength);
        return new String(bodyChars, 0, read);
    }

    public String param(String key) {
        return paramMap.get(key);
    }

    public String cookie(String key) {
        return headers.cookies().get(key);
    }

    public Session session(boolean create) {
        SessionStore sessionStore = SessionManager.getSessionStore();
        if (create) {
            return sessionStore.create();
        }
        String sessionId = cookie("JSESSIONID");
        return sessionId == null ? null : sessionStore.get(sessionId);
    }

    public void changeSessionId() {
        SessionStore sessionStore = SessionManager.getSessionStore();
        Session session = session(false);
        sessionStore.changeSessionId(session);
    }
}
