package nextstep.jwp;

import nextstep.jwp.model.User;
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
                doGetAction(requestPath, outputStream);
            }

            if ("POST".equals(method)) {
                String body = parseBody(br, Integer.parseInt(header.get("Content-Length")));
                doPostAction(requestPath, body, outputStream);
            }

            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void doGetAction(String requestPath, OutputStream outputStream) throws IOException {
        if (requestPath.startsWith("/login")) {
            outputStream.write(generateResponseBody("/login.html").getBytes());
            return;
        }

        if (requestPath.startsWith("/register")) {
            outputStream.write(generateResponseBody("/register.html").getBytes());
            return;
        }

        outputStream.write(generateResponseBody(requestPath).getBytes());
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
            return;
        }

        if (requestPath.startsWith("/register")) {
            try {
                userService.save(queryMap.get("account"), queryMap.get("password"), queryMap.get("email"));
                outputStream.write(generateResponseBody302("/index.html").getBytes());
            } catch (RuntimeException e) {
                outputStream.write(generateResponseBody302("/401.html").getBytes());
            }
            return;
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

    private String generateResponseBody(String resourcePath) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        byte[] body = new byte[0];
        if (resource != null) {
            final Path path = new File(resource.getPath()).toPath();
            body = Files.readAllBytes(path);
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.length + " ",
                "",
                new String(body));
    }

    private String generateResponseBody302(String locationUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Redirect ",
                "Location: " + locationUrl + " ",
                "");
    }

    private String generateResponseBodyWithData(String body) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.length() + " ",
                "",
                body);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
