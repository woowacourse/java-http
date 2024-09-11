package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestDispatcher requestDispatcher;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestDispatcher = new RequestDispatcher();
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = parseInput(inputStream);
            HttpResponse response = new HttpResponse();

            requestDispatcher.requestMapping(request, response);
            String resourceName = response.getResourceName();
            if (resourceName == null) {
                resourceName = request.getPath();
                response.setResourceName(resourceName);
            }
            String responseBody = getResource(resourceName);
            String resourceExtension = getExtension(resourceName);

            if (!request.hasCookieFrom("JSESSIONID")) {
                Map<String, String> parameter = Map.of("JSESSIONID", UUID.randomUUID().toString());
                response.setHttpCookie(new HttpCookie(parameter));
            }
            response.setResponseBody(responseBody);
            response.setContentType(resourceExtension);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseInput(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String tokens[] = bufferedReader.readLine()
                .split(" ");
        if (tokens.length != 3) {
            throw new RuntimeException();
        }

        String path = parsePath(tokens[1]);
        Map<String, String> queryString = parseQueryString(tokens[1]);
        Map<String, String> headers = parseHeaders(bufferedReader);
        HttpCookie httpCookie = parseCookie(headers);
        Session session = parseSession(httpCookie);
        String body = parseBody(bufferedReader, headers);
        return new HttpRequest(tokens[0], path, queryString, tokens[2], headers, httpCookie, session, body);
    }

    private String parsePath(String token) {
        int separatorIndex = token.indexOf('?');
        if (separatorIndex == -1) {
            return token;
        }
        return token.substring(0, separatorIndex);
    }

    private Map<String, String> parseQueryString(String token) {
        Map<String, String> queryString = new HashMap<>();
        int separatorIndex = token.indexOf('?');
        if (separatorIndex == -1) {
            return queryString;
        }
        Stream.of(token.substring(separatorIndex + 1)
                        .split("&"))
                .forEach(data -> parseData(data, queryString));
        return queryString;
    }

    private void parseData(String s, Map<String, String> queryString) {
        String data[] = s.split("=");
        if (data.length == 2) {
            queryString.put(data[0], data[1]);
        }
    }

    private String getExtension(String resourceName) {
        if (resourceName == null) {
            return "html";
        }
        int extensionIndex = resourceName.indexOf('.') + 1;
        if (extensionIndex == 0) {
            return "html";
        }
        return resourceName.substring(extensionIndex);
    }

    private String getResource(String resourceName) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader()
                .getResource("static" + resourceName);

        if (resource == null) {
            return "Hello world!";
        }
        try {
            return Files.readString(Paths.get(resource.toURI()));
        } catch (IOException e) {
            return "Hello world!";
        }
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        Map<String, String> headers = new LinkedHashMap<>();

        while (!"".equals(line)) {
            if (line == null) {
                return headers;
            }
            int index = line.indexOf(':');
            String key = line.substring(0, index);
            String value = line.substring(index + 2);
            headers.put(key, value);
            line = bufferedReader.readLine();
        }
        return headers;
    }

    private HttpCookie parseCookie(Map<String, String> headers) {
        Map<String, String> cookies = new HashMap<>();
        if (!headers.containsKey("Cookie")) {
            return new HttpCookie(cookies);
        }
        String data = headers.get("Cookie");
        Stream.of(data.split(";"))
                .map(String::trim)
                .forEach(s -> {
                    int separatorIndex = s.indexOf("=");
                    if (separatorIndex != -1) {
                        cookies.put(s.substring(0, separatorIndex), s.substring(separatorIndex + 1));
                    }
                });
        return new HttpCookie(cookies);
    }

    private Session parseSession(HttpCookie httpCookie) {
        String sessionId = httpCookie.getValue("JSESSIONID");
        Session found = sessionManager.findSession(sessionId);
        if (found == null && sessionId != null) {
            Session session = new Session(sessionId);
            sessionManager.add(new Session(sessionId));
            return session;
        }
        return found;
    }

    private String parseBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        String length = headers.get("Content-Length");
        if (length == null) {
            return "";
        }
        int contentLength = Integer.parseInt(length);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
