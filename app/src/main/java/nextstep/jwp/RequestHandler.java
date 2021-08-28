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
import nextstep.jwp.db.InMemoryUserRepository;
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

            final String[] parsedRequest = getParsedRequest(inputStream);
            String responseBody = responseBody = getStaticFileContents(parsedRequest);
            Map<String, String> requestUserData = extractQueryFromLoginUri(parsedRequest);

            final String uri = parsedRequest[1];
            if (uri.equals("/index") || uri.equals("/index.html")) {
                responseBody = getStaticFileContents(parsedRequest);
            } else if (uri.equals("/login.html") || uri.equals("/login")) {
                responseBody = getStaticFileContents(parsedRequest);
            } else if (uri.matches("/login.*account.*password.*")) {
                responseBody = getStaticFileContents(parsedRequest);
            }

            if (!requestUserData.isEmpty()) {
                log.debug(InMemoryUserRepository.findByAccount(requestUserData.get("account")).toString());
            }

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
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

    private Map<String, String> extractQueryFromLoginUri(String[] parsedRequest) {
        Map<String, String> extractedQuery = new HashMap<>();
        String uri = parsedRequest[1];

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

    private String getStaticFileContents(String[] parsedRequest) throws IOException {
        String contents = "";
        String method = parsedRequest[0];
        String uri = parsedRequest[1];
        String path;
        String queryString;
        if (method.equals("GET") && uri.equals("/")) { // To main
            contents = "Hello world!";
        }

        int index = uri.indexOf("?");
        if (index == -1) {
            path = uri;
        } else {
            path = uri.substring(0, index);
            queryString = uri.substring(index + 1);
        }

        if (method.equals("GET") && !uri.equals("/")) {
            URL resource = getClass().getClassLoader().getResource("static" + path);
            if (resource == null) {
                resource = getClass().getClassLoader().getResource("static" + path + ".html");
            }
            if (resource == null) {
                return contents;
            }
            contents = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        }
        return contents;
    }

    private String[] getParsedRequest(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final StringBuilder stringBuilder = new StringBuilder();

        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine()).append("\r\n");
        }
        String request = stringBuilder.toString();
        String[] parsedRequest = request.split(" ");
        return parsedRequest;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
