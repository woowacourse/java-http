package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String DEFAULT_PATH = "static";
    private static final String DEFAULT_SUFFIX = ".html";

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            RequestHeader header = header(inputStream);
            String parsedUri = header.uri();

            if ("/".equals(parsedUri)) {
                parsedUri = "/index.html";
            }
            if (!suffixExists(parsedUri)) {
                parsedUri += DEFAULT_SUFFIX;
            }

            if (parsedUri.equals("/login.html")) {
                Map<String, String> params = header.params();
                String account = params.get("account");
                String password = params.get("password");
                Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
                if (byAccount.isPresent()) {
                    if (byAccount.get().checkPassword(password)) {
                        final String response = String.join("\r\n",
                                "HTTP/1.1 302 FOUND ",
                                "Location: http://localhost:8080/index.html ",
                                "",
                                "");

                        outputStream.write(response.getBytes());
                        outputStream.flush();
                        return;
                    }
                    final String response = String.join("\r\n",
                            "HTTP/1.1 302 FOUND ",
                            "Location: http://localhost:8080/401.html ",
                            "",
                            "");

                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }
            }

            URL resource = this.getClass().getClassLoader().getResource(DEFAULT_PATH + parsedUri);
            File file = new File(resource.toURI());
            String fileSource = new String(Files.readAllBytes(file.toPath()));

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + fileSource.getBytes().length + " ",
                    "",
                    fileSource);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private boolean suffixExists(String parsedUri) {
        return parsedUri.contains(".");
    }

    private RequestHeader header(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String[] requestInfo = reader.readLine().split(" ");
        String method = requestInfo[0];
        String httpVersion = requestInfo[2];

        Map<String, String> headers = new LinkedHashMap<>();
        Map<String, String> params = new LinkedHashMap<>();
        String uri = dividePathAndQueryString(requestInfo[1], params);
        String line = "";

        while (!("".equals(line = reader.readLine()))) {
            String[] keyAndValue = line.split(" ");
            headers.put(keyAndValue[0], keyAndValue[1]);
        }

        return new RequestHeader(method, uri, httpVersion, headers, params);
    }

    private String dividePathAndQueryString(String uri, Map<String, String> params) {
        if (!uri.contains("?")) {
            return uri;
        }
        int idx = uri.indexOf('?');
        String path = uri.substring(0, idx);
        String queryString = uri.substring(idx + 1);
        String[] paramPairs = queryString.split("&");
        for (String paramPair : paramPairs) {
            int delimiterIdx = paramPair.indexOf("=");
            String key = paramPair.substring(0, delimiterIdx);
            String value = paramPair.substring(delimiterIdx + 1);
            params.put(key, value);
        }
        return path;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
