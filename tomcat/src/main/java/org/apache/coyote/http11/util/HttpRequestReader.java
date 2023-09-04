package org.apache.coyote.http11.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.exception.InvalidHttpFormException;
import org.apache.coyote.http11.exception.SessionNotFoundException;

public class HttpRequestReader {

    private HttpRequestReader() {
    }

    public static HttpRequest read(InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var httpRequest = new HttpRequest();

        readRequestLine(httpRequest, bufferedReader);
        readRequestHeaders(httpRequest, bufferedReader);
        readRequestBody(httpRequest, bufferedReader);

        return httpRequest;
    }

    private static void readRequestLine(HttpRequest httpRequest, BufferedReader bufferedReader)
            throws IOException {
        final var requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            throw new InvalidHttpFormException();
        }
        final var splitLine = requestLine.split(" ");
        httpRequest.setHttpMethod(HttpMethod.valueOf(splitLine[0]));
        parseUri(httpRequest, splitLine[1]);
        httpRequest.setProtocol(splitLine[2]);
    }

    private static void readRequestHeaders(HttpRequest httpRequest, BufferedReader bufferedReader)
            throws IOException {
        while (bufferedReader.ready()) {
            final var header = bufferedReader.readLine();
            if (header.isEmpty()) {
                break;
            }
            final var keyValuePair = header.split(": ");
            httpRequest.addHeader(keyValuePair[0], keyValuePair[1]);
        }
        if (httpRequest.containsHeader(HttpHeaders.COOKIE)) {

            String[] cookies = httpRequest.getHeader(HttpHeaders.COOKIE).split("; ");
            for (String cookie : cookies) {
                String[] keyValuePair = cookie.split("=");
                httpRequest.addCookie(new HttpCookie(keyValuePair[0], keyValuePair[1]));
            }
        }
        if (httpRequest.hasCookie(HttpCookie.JSESSIONID)) {
            HttpCookie sessionCookie = httpRequest.getCookie(HttpCookie.JSESSIONID);
            Session session = SessionManager.findSession(sessionCookie.getValue());
            if (session == null) {
                throw new SessionNotFoundException();
            }
            httpRequest.setSession(session);
        }
    }

    private static void readRequestBody(HttpRequest httpRequest, BufferedReader bufferedReader)
            throws IOException {
        if (!httpRequest.containsHeader(HttpHeaders.CONTENT_LENGTH)) {
            return;
        }
        int contentLength = Integer.parseInt(httpRequest.getHeader(HttpHeaders.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        httpRequest.setBody(requestBody);
    }

    private static void parseUri(HttpRequest httpRequest, String uri) {
        if (uri.contains("?")) {
            final var queryStartIndex = uri.indexOf("?");
            final var queryString = uri.substring(queryStartIndex + 1);
            final var parameters = parseParameters(queryString);
            httpRequest.addParameters(parameters);
        }

        final var path = getFilePath(uri);
        httpRequest.setUri(uri);
        httpRequest.setPath(path);
    }

    private static Map<String, String> parseParameters(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        String[] parametersArray = queryString.split("&");
        for (String parameter : parametersArray) {
            String[] split = parameter.split("=");
            parameters.put(split[0], split[1]);
        }
        return parameters;
    }

    private static String getFilePath(String uri) {
        String filePath = uri.split("\\?")[0];
        if (filePath.matches(".+\\.[a-zA-Z]+$") || filePath.equals("/")) {
            return filePath;
        }
        return filePath + ".html";
    }
}
