package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.ContentTypeMapper;
import nextstep.jwp.http.HttpRequestHeader;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
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
            System.out.println(resource);
            Map<String, String> httpRequestHeaders = httpRequestHeader.getHeaders();

            String responseHeader = "";
            String responseBody = "";

            if (resource.equals("/")) {
                responseBody = "Hello world!";
                responseHeader = "200 OK";
            }

            if (resource.contains(".")) {
                responseBody = createResponseBody(resource);
                responseHeader = "200 OK";
            }

            if (method.equals("GET") && resource.equals("/register")) {
                responseBody = createResponseBody("/register.html");
                responseHeader = "200 OK";
            }
            if (method.equals("POST") && resource.equals("/register")) {
                responseBody = createResponseBody("/index.html");
                responseHeader = "200 OK";

                int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                br.read(buffer, 0, contentLength);
                String queries = new String(buffer);

                Map<String, String> queryMap = createQueryMap(queries);
                User user = new User(InMemoryUserRepository.size() + 1,
                        queryMap.get("account"),
                        queryMap.get("email"),
                        queryMap.get("password"));
                InMemoryUserRepository.save(user);
            }
            if (resource.contains("?")) {
                int index = resource.indexOf("?");
                String queries = resource.substring(index + 1);
                Map<String, String> queryMap = createQueryMap(queries);
                Optional<User> account = InMemoryUserRepository.findByAccount(queryMap.get("account"));
                if (account.isPresent()) {
                    responseBody = createResponseBody("/index.html");
                    responseHeader = "302 FOUND";
                } else {
                    responseBody = createResponseBody("/401.html");
                    responseHeader = "401 UNAUTHORIZED";
                }
            }

            outputStream.write(createResponse(responseHeader, responseBody, ContentTypeMapper.extractContentType(resource)).getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Map<String, String> createQueryMap(String queryString) {
        Map<String, String> queryMap = new HashMap<>();
        String[] queries = queryString.split("&");
        for (String query : queries) {
            int equalIndex = query.indexOf("=");
            String key = query.substring(0, equalIndex);
            String value = query.substring(equalIndex + 1);
            queryMap.put(key, value);
        }
        return queryMap;
    }

    private String createResponseBody(String render) throws IOException {
        String filePath = "static" + render;
        final URL url = getClass().getClassLoader().getResource(filePath);
        File file = new File(Objects.requireNonNull(url).getFile());
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }

    private String createResponse(String responseHeader, String responseBody, String contentType) {
        final String response = String.join("\r\n",
                "HTTP/1.1 " + responseHeader + " ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return response;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
