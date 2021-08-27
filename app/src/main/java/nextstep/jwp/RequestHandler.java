package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = br.readLine();
            log.debug("request line : {}", line);

            if (line == null) {
                return;
            }

            String url = line;
            String uri = line.split(" ")[1];

            Map<String, String> requestHeaders = new HashMap<>();
            while (!Objects.equals(line, "")) {
                line = br.readLine();
                log.debug("header : {}", line);
                if (!line.isBlank()) {
                    requestHeaders.put(line.split(": ")[0], line.split(": ")[1]);
                }
            }

            String response = "";
            if (uri.contains(".")) {
                Path path = getPath(uri);
                String responseBody = new String(Files.readAllBytes(path));
                if (uri.endsWith(".css")) {
                    response = responseCssHeaderOfStatusOK(responseBody);
                } else if (uri.contains(".html")) {
                    response = responseHeaderOfStatusOK(responseBody);
                } else if (uri.contains(".js")) {
                    response = responseJsHeaderOfStatusOK(responseBody);
                }
                writeBody(outputStream, response);
            }

            if (uri.startsWith("/register")) {
                if ("GET".equals(url.split(" ")[0])) {
                    Path path = getPath("register.html");
                    String responseBody = new String(Files.readAllBytes(path));
                    response = responseHeaderOfStatusOK(responseBody);
                    writeBody(outputStream, response);
                    return;
                }
                int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                br.read(buffer, 0, contentLength);
                String body = new String(buffer);
                Map<String, String> maps = HttpRequestUtils.parseQueryString(body);
                int count = InMemoryUserRepository.countIds();
                InMemoryUserRepository.save(new User(count + 1, maps.get("account"), maps.get("email"), maps.get("password")));
                response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /index.html",
                        "");
                writeBody(outputStream, response);
                return;
            }

            if (uri.startsWith("/login")) {
                String contentLength = requestHeaders.get("Content-Length");
                if (Objects.isNull(contentLength) || Integer.parseInt(contentLength) <= 0) {
                    Path path = getPath("login.html");
                    String responseBody = new String(Files.readAllBytes(path));
                    response = responseHeaderOfStatusOK(responseBody);
                    writeBody(outputStream, response);
                    return;
                }

                int length = Integer.parseInt(requestHeaders.get("Content-Length"));
                char[] buffer = new char[length];
                br.read(buffer, 0, length);
                String body = new String(buffer);
                Map<String, String> params = HttpRequestUtils.parseQueryString(body);

                Optional<User> findUser = InMemoryUserRepository.findByAccount(params.get("account"))
                        .filter(user -> user.checkPassword(params.get("password")))
                        .stream().findAny();

                if (findUser.isEmpty()) {
                    response = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: /401.html",
                            "");
                    writeBody(outputStream, response);
                    return;
                }
                response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /index.html",
                        "");
                writeBody(outputStream, response);
                return;
            }
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Path getPath(String uri) {
        URL resource = getClass().getClassLoader().getResource("static/" + uri);
        String file = Objects.requireNonNull(resource).getFile();
        Path path = new File(file).toPath();
        return path;
    }

    private void writeBody(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String responseJsHeaderOfStatusOK(String body) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: */*;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private String responseCssHeaderOfStatusOK(String body) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private String responseHeaderOfStatusOK(String body) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
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
