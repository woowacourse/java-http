package nextstep.jwp.infrastructure;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.CustomHttpRequest;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            CustomHttpRequest request = requestFromInputStream(reader);
            String parsedUri = request.getUri();

            if ("/".equals(parsedUri)) {
                parsedUri = "/index.html";
            }
            if (!suffixExists(parsedUri)) {
                parsedUri += DEFAULT_SUFFIX;
            }

            if (parsedUri.equals("/login.html")) {
                loginProcess(request, outputStream);
                return;
            }

            URL resource = this.getClass().getClassLoader().getResource(DEFAULT_PATH + parsedUri);
            File file = new File(resource.toURI());
            String fileSource = new String(Files.readAllBytes(file.toPath()));

            final String response = htmlResponse(fileSource);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void loginProcess(CustomHttpRequest request, OutputStream outputStream) throws IOException {
        Map<String, String> params = request.getParams();
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
        }
    }

    private String htmlResponse(String fileSource) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + fileSource.getBytes().length + " ",
                "",
                fileSource);
    }

    private boolean suffixExists(String parsedUri) {
        return parsedUri.contains(".");
    }

    private CustomHttpRequest requestFromInputStream(BufferedReader reader) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        Map<String, String> params = new LinkedHashMap<>();

        String[] requestInfo = reader.readLine().split(" ");
        String method = requestInfo[0];
        String uri = requestInfo[1];
        String httpVersion = requestInfo[2];
        String path = "";

        String line = "";
        while (!("".equals(line = reader.readLine()))) {
            String[] keyAndValue = line.split(": ");
            headers.put(keyAndValue[0].trim(), keyAndValue[1].trim());
        }

        if (method.equals("GET")) {
            path = dividePathFromUri(uri);
            parseParams(params, uri.substring(path.length() + 1));
        }

        if (method.equals("POST")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            requestBody = URLDecoder.decode(requestBody, "UTF-8");
            parseParams(params, requestBody);
        }

        return new CustomHttpRequest(method, uri, httpVersion, headers, params);
    }

    private String dividePathFromUri(String uri) {
        if (!uri.contains("?")) {
            return uri;
        }
        return uri.substring(0, uri.indexOf('?'));
    }

    private void parseParams(Map<String, String> params, String rawParams) {
        String[] paramPairs = rawParams.split("&");

        for (String paramPair : paramPairs) {
            int delimiterIdx = paramPair.indexOf("=");
            String key = paramPair.substring(0, delimiterIdx);
            String value = paramPair.substring(delimiterIdx + 1);
            params.put(key, value);
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
