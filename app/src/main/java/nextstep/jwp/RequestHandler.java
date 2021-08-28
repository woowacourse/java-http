package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()) {

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Map<String, String> parsedRequestHeaders = getParsedRequestHeaders(inputStream, bufferedReader);
            final String httpMethod = parsedRequestHeaders.get("httpMethod");
            final String uri = parsedRequestHeaders.get("uri");
            String responseBody = "Hello world!";

            if (uri.equals("/")) {
                replyOkResponse(responseBody, outputStream);
            } else if (uri.equals("/index") || uri.equals("/index.html")) {
                responseBody = getStaticFileContents("/index.html");
                replyOkResponse(responseBody, outputStream);
            } else if (uri.equals("/login.html") || uri.equals("/login")) {
                responseBody = getStaticFileContents("/login.html");
                replyOkResponse(responseBody, outputStream);
            } else if (uri.matches("/login.*account.*password.*")) {
                Map<String, String> requestUserData = extractQueryFromLoginUri(uri);
                String account = requestUserData.getOrDefault("account", null);
                String password = requestUserData.getOrDefault("password", null);
                log.debug(InMemoryUserRepository.findByAccount(requestUserData.get("account")).toString());
                Optional<User> user = InMemoryUserRepository.findByAccountAndPassword(account, password);

                if (user.isEmpty()) {
                    responseBody = getStaticFileContents("/401.html");
                    reply302Response(responseBody, outputStream);
                } else {
                    responseBody = getStaticFileContents("/index.html");
                    replyOkResponse(responseBody, outputStream);
                }
            } else if (httpMethod.equals("GET") && (uri.equals("/register") || uri.equals("/register.html"))) {
                responseBody = getStaticFileContents("/register.html");
                replyOkResponse(responseBody, outputStream);
            } else if (httpMethod.equals("POST") && uri.equals("/register")) {
                int contentLength = Integer.parseInt(parsedRequestHeaders.get("Content-Length").strip());
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);
                Map<String, String> registerData = extractRegisterDataFromRequestBody(requestBody);

                InMemoryUserRepository.save(new User(InMemoryUserRepository.autoIncrementId,
                        registerData.get("account"),
                        registerData.get("password"),
                        registerData.get("email")));
                InMemoryUserRepository.autoIncrementId += 1;

                responseBody = getStaticFileContents("/index.html");
                replyOkResponse(responseBody, outputStream);
            }

        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Map<String, String> extractRegisterDataFromRequestBody(String requestBody) {
        Map<String, String> extractData = new HashMap<>();
        String[] splitRequestBody = requestBody.split("=|&");

        for (int i = 0; i < splitRequestBody.length; i += 2) {
            extractData.put(splitRequestBody[i], splitRequestBody[i + 1]);
        }
        return extractData;
    }

    private void reply302Response(String responseBody, OutputStream outputStream) throws IOException {
        final String response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void replyOkResponse(String responseBody, OutputStream outputStream) throws IOException {
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private Map<String, String> extractQueryFromLoginUri(String uri) {
        Map<String, String> extractedQuery = new HashMap<>();

        int index = uri.indexOf("?");
        if (index == -1) {
            return extractedQuery;
        }
        String queryString = uri.substring(index + 1);

        String[] splitQuery = queryString.split("[&=]");

        for (int i = 0; i < splitQuery.length; i += 2) {
            extractedQuery.put(splitQuery[i], splitQuery[i + 1]);
        }

        return extractedQuery;
    }

    private String getStaticFileContents(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private Map<String, String> getParsedRequestHeaders(InputStream inputStream, BufferedReader bufferedReader)
            throws IOException {

        final Map<String, String> parsedRequests = new HashMap<>();

        if (bufferedReader.ready()) {
            String headers = bufferedReader.readLine();
            String[] splitHeader = headers.split(" ");
            parsedRequests.put("httpMethod", splitHeader[0]);
            parsedRequests.put("uri", splitHeader[1]);
            parsedRequests.put("httpVersion", splitHeader[2]);
        }

        while (bufferedReader.ready()) {
            String headers = bufferedReader.readLine();
            String[] splitHeader = headers.split(": ");
            if (splitHeader[0].equals("")) {
                break;
            }
            parsedRequests.put(splitHeader[0], splitHeader[1]);
        }

        return parsedRequests;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
