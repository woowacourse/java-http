package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.StreamUtils;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final String uri = StreamUtils.readAllLines(inputStream).split(" ")[1];

            if (uri.contains(".")) {
                routeForStaticFile(outputStream, uri);
                return;
            } else if ("/".equals(uri)) {
                routeForRoot(outputStream);
                return;
            } else if (uri.contains("/login")) {
                routeForLogin(outputStream, uri);
            }
            return;
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void routeForLogin(final OutputStream outputStream, final String uri) {
        final Map<String, String> userMap = parseUserMap(uri);
        final String account = userMap.get("account");
        final String password = userMap.get("password");

        final User findUser = InMemoryUserRepository.findByAccount(account)
                .stream()
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(">>>>>>> not found user >>>>>>>"));

        if (findUser.checkPassword(password)) {
            log.info("Correct ! {}", findUser);
            return;
        }

        log.info("user not found ! user : {}", findUser);
    }

    /**
     * 다음과 같은 query string에 대해  parsing을 합니다
     * <p>
     * 1. "/login?account=gugu&password=password"
     * <p>
     * 2. "account=gugu&password=password"
     * <p>
     * 3. ["account=gugu", "password=password"]
     * <p>
     * 4. {"account" : "gugu", "password" : "password"}
     */
    private Map<String, String> parseUserMap(final String uri) {
        final String[] queryStringArr = uri.split("\\?")[1].split("\\&");
        final Map<String, String> map = new HashMap<>();
        for (final String queryString : queryStringArr) {
            final String[] split = queryString.split("\\=");
            final String key = split[0];
            final String value = split[1];
            map.put(key, value);
        }
        return map;
    }

    private void routeForStaticFile(final OutputStream outputStream, final String uri) throws IOException {
        final String[] fileNameSeperated = uri.split("\\.");
        final String extension = fileNameSeperated[fileNameSeperated.length - 1];
        final String responseBody = getResponseBody(uri);
        String fileType = getFileType(extension);

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void routeForRoot(final OutputStream outputStream) {
        final String welcomeMessage = "Hello world!";
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: " + welcomeMessage.getBytes().length + " ",
                "",
                welcomeMessage);
        try {
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileType(final String extension) {
        switch (extension) {
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            default:
                return "text/plain";
        }
    }

    private String getResponseBody(final String fileName) {
        final List<String> actual;
        try {
            if ("/".equals(fileName)) {
                return "Hello world!";
            }
            final Path path = getPath(fileName);
            final File file = path.toFile();
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            actual = getLines(bufferedReader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return actual.stream().collect(Collectors.joining("\r\n"));
    }

    private LinkedList<String> getLines(final BufferedReader bufferedReader) {
        return bufferedReader
                .lines()
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Path getPath(final String fileName) {
        return Paths
                .get("tomcat", "src", "main", "resources", "static", fileName)
                .toAbsolutePath();
    }
}
