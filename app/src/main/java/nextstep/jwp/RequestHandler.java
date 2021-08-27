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
            String line = br.readLine();

            if (line == null) {
                return;
            }

            String[] requestLine = line.split(" ");

            while (!line.isBlank()) {
                line = br.readLine();
            }

            String requestPath = requestLine[1];

            if (requestPath.startsWith("/login")) {
                int queryStartIndex = requestPath.indexOf("?");
                if (queryStartIndex == -1) {
                    outputStream.write(generateResponseBody("/login.html").getBytes());
                    return;
                }

                Map<String, String> queryMap = parseQuery(requestPath.substring(queryStartIndex + 1));
                User findUser = userService.login(queryMap.get("account"), queryMap.get("password"));
                outputStream.write(generateResponseBodyWithData(findUser.toString()).getBytes());
                outputStream.flush();
                return;
            }

            outputStream.write(generateResponseBody(requestPath).getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
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
