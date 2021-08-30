package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequestHeader;
import nextstep.jwp.model.User;
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
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequestHeader httpRequestHeader = new HttpRequestHeader(br);
            String method = httpRequestHeader.getMethod();
            String resource = httpRequestHeader.getResource();
            Map<String, String> httpRequestHeaders = httpRequestHeader.getHeaders();
            String responseHeader = "";
            String responseBody = "";

            if (resource.equals("/")) {
                responseBody = "Hello world!";
                responseHeader = "200 OK";
            }
            if (resource.equals("/index.html")) {
                String filePath = "static/index.html";
                final URL url = getClass().getClassLoader().getResource(filePath);
                File file = new File(Objects.requireNonNull(url).getFile());
                final Path path = file.toPath();
                responseBody = new String(Files.readAllBytes(path));
                responseHeader = "200 OK";
            }
            if (method.equals("GET") && resource.equals("/register")) {
                String requestUri = "static/register.html";
                final URL url = getClass().getClassLoader().getResource(requestUri);
                File file = new File(Objects.requireNonNull(url).getFile());
                final Path path = file.toPath();
                responseBody = new String(Files.readAllBytes(path));
                responseHeader = "200 OK";
            }
            if (method.equals("POST") && resource.equals("/register")) {
                String requestUri = "static/index.html";
                final URL url = getClass().getClassLoader().getResource(requestUri);
                File file = new File(Objects.requireNonNull(url).getFile());
                final Path path = file.toPath();
                responseBody = new String(Files.readAllBytes(path));
                responseHeader = "200 OK";

                int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                br.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);

                Map<String, String> queryMap = new HashMap<>();
                String[] queries = requestBody.split("&");
                for (String query : queries) {
                    int equalIndex = query.indexOf("=");
                    String key = query.substring(0, equalIndex);
                    String value = query.substring(equalIndex + 1);
                    queryMap.put(key, value);
                }

                InMemoryUserRepository.save(new User(InMemoryUserRepository.size() + 1,
                        queryMap.get("account"),
                        queryMap.get("email"),
                        queryMap.get("password")));
            }
            if (resource.contains("?")) {
                int index = resource.indexOf("?");
                String queryString = resource.substring(index + 1);
                Map<String, String> queryMap = new HashMap<>();
                String[] queries = queryString.split("&");
                for (String query : queries) {
                    int equalIndex = query.indexOf("=");
                    String key = query.substring(0, equalIndex);
                    String value = query.substring(equalIndex + 1);
                    queryMap.put(key, value);
                }
                Optional<User> account = InMemoryUserRepository.findByAccount(queryMap.get("account"));
                if (account.isPresent()) {
                    String requestUri = "static/index.html";
                    final URL url = getClass().getClassLoader().getResource(requestUri);
                    File file = new File(Objects.requireNonNull(url).getFile());
                    final Path path = file.toPath();
                    responseBody = new String(Files.readAllBytes(path));
                    responseHeader = "302 FOUND";
                } else {
                    String requestUri = "static/401.html";
                    final URL url = getClass().getClassLoader().getResource(requestUri);
                    File file = new File(Objects.requireNonNull(url).getFile());
                    final Path path = file.toPath();
                    responseBody = new String(Files.readAllBytes(path));
                    responseHeader = "401 UNAUTHORIZED";
                }
            }

            final String response = String.join("\r\n",
                    "HTTP/1.1 " + responseHeader + " ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);


            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
