package nextstep.jwp;

import nextstep.jwp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static nextstep.jwp.AcceptType.TEXT_CSS;
import static nextstep.jwp.Header.*;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    private final UserService userService;

    public RequestHandler(Socket connection, UserService userService) {
        this.connection = Objects.requireNonNull(connection);
        this.userService = userService;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            Map<String, String> header = parseHeader(br);
            String requestLine = header.get("Request-Line");
            String[] parsedRequestLine = requestLine.split(" ");
            String method = parsedRequestLine[0];
            String requestPath = parsedRequestLine[1];


            if ("GET".equals(method)) {
                doGetAction(header, requestPath, outputStream);
            }

            if ("POST".equals(method)) {
                String body = parseBody(br, Integer.parseInt(header.get(CONTENT_LENGTH)));
                doPostAction(requestPath, body, outputStream);
            }

            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void doGetAction(Map<String, String> header, String requestPath, OutputStream outputStream) throws IOException {
        if (requestPath.startsWith("/login")) {
            if (header.get(ACCEPT) != null) {
                String accept = header.get(ACCEPT);
                if (accept.contains("text/css")) {
                    Map<String, String> responseHeader = new HashMap<>();
                    responseHeader.put(CONTENT_TYPE, TEXT_CSS);
                    outputStream.write(ok(responseHeader, "/login.html").getBytes());
                    return;
                }
            }
            outputStream.write(ok("/login.html").getBytes());
            return;
        }

        if (requestPath.startsWith("/register")) {
            if (header.get(ACCEPT) != null) {
                String accept = header.get(ACCEPT);
                if (accept.contains(TEXT_CSS)) {
                    Map<String, String> responseHeader = new HashMap<>();
                    responseHeader.put(CONTENT_TYPE, TEXT_CSS);
                    outputStream.write(ok(responseHeader, "/register.html").getBytes());
                    return;
                }
            }
            outputStream.write(ok("/register.html").getBytes());
            return;
        }

        if (header.get(ACCEPT) != null) {
            String accept = header.get(ACCEPT);
            if (accept.contains(TEXT_CSS)) {
                Map<String, String> responseHeader = new HashMap<>();
                responseHeader.put(CONTENT_TYPE, TEXT_CSS);
                outputStream.write(ok(responseHeader, requestPath).getBytes());
                return;
            }
        }
        outputStream.write(ok(requestPath).getBytes());
    }

    private Map<String, String> parseHeader(BufferedReader request) throws IOException {
        HashMap<String, String> header = new HashMap<>();
        String line = request.readLine();
        header.put("Request-Line", line);
        while (true) {
            line = request.readLine();
            if (line.isBlank()) {
                break;
            }
            String[] keyAndValue = line.split(":");
            header.put(keyAndValue[0], keyAndValue[1].trim());
        }
        return header;
    }

    private String parseBody(BufferedReader request, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        request.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private void doPostAction(String requestPath, String body, OutputStream outputStream) throws IOException {
        Map<String, String> queryMap = parseQuery(body);

        if (requestPath.startsWith("/login")) {
            try {
                userService.login(queryMap.get("account"), queryMap.get("password"));
                outputStream.write(generateResponseBody302("/index.html").getBytes());
            } catch (RuntimeException e) {
                outputStream.write(generateResponseBody302("/401.html").getBytes());
            }
        }

        if (requestPath.startsWith("/register")) {
            try {
                userService.save(queryMap.get("account"), queryMap.get("password"), queryMap.get("email"));
                outputStream.write(generateResponseBody302("/index.html").getBytes());
            } catch (RuntimeException e) {
                outputStream.write(generateResponseBody302("/401.html").getBytes());
            }
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> queryMap = new HashMap<>();

        String[] data = query.split("&");
        for (String each : data) {
            String[] keyAndValue = each.split("=");
            queryMap.put(keyAndValue[0], keyAndValue[1]);
        }

        return queryMap;
    }


    private String ok(String resourcePath) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(CONTENT_TYPE, "text/html;charset=utf-8 ");
        return ok(headerMap, resourcePath);
    }

    private String ok(Map<String, String> header, String resourcePath) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        byte[] body = new byte[0];
        if (resource != null) {
            final Path path = new File(resource.getPath()).toPath();
            body = Files.readAllBytes(path);
        }

        header.put(CONTENT_LENGTH, String.valueOf(body.length));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/1.1 200 OK \r\n");

        for (Map.Entry<String, String> entry : header.entrySet()) {
            stringBuilder.append(entry.getKey() + ": " + entry.getValue() + " \r\n");
        }
        stringBuilder.append("\r\n");
        stringBuilder.append(new String(body) + "\r\n");

        return stringBuilder.toString();
    }

    private String generateResponseBody302(String locationUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Redirect ",
                "Location: " + locationUrl + " ",
                "");
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
