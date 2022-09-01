package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_MESSAGE = "Hello world!";

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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = getFirstLine(bufferedReader);
            if (line == null) {
                return ;
            }

            final String response = generateResponse(line);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getFirstLine(final BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        log.info("request line : {}", line);
        return line;
    }

    private String generateResponse(final String line) throws IOException {
        final String url = getUrl(line);

        if (url.startsWith("/login")) {
            if (url.contains("?")) {
                return responseLoginUser(url);
            }
            return responseLoginPage(url);
        }

        final String fileName = getFileName(url);
        return response(fileName);
    }

    private String responseLoginPage(final String url) throws IOException {
        final String fileName = url + ".html";
        return response(fileName);
    }

    private String responseLoginUser(final String url) {
        final HashMap<String, String> params = getParams(url);
        final String account = params.get("account");
        final String password = params.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("해당하는 유저를 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            return createResponse("", "password missMatch");
//            throw new IllegalArgumentException("password가 올바르지 않습니다.");
        }

        return createResponse("", user.toString());
    }

    private HashMap<String, String> getParams(final String url) {
        final String queryParameter = url.substring(url.indexOf("?") + 1);
        final String[] params = queryParameter.split("&");
        final HashMap<String, String> paramStore = new HashMap<>();
        for (String param : params) {
            final String[] keyValue = param.split("=");
            paramStore.put(keyValue[0], keyValue[1]);
        }
        return paramStore;
    }

    private String response(final String fileName) throws IOException {
        final String contentType = getContentType(fileName);
        final String responseBody = getResponseBody(fileName);
//            readAllLine(bufferedReader, line); // 첫줄 다음의 다른 헤더들도 읽기
        return createResponse(contentType, responseBody);
    }

    private String createResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text" + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getFileName(final String url) {
        return url;
    }

    private String getUrl(final String line) {
        return line.split(" ")[1];
    }

    private String getContentType(final String fileName) {
        final String[] fileNameParse = fileName.split("\\.");
        if (fileNameParse.length < 2) {
            return "/html";
        }
        return "/" + fileNameParse[1];
    }

    private String getResponseBody(final String fileName) throws IOException {
        if (fileName.equals("/")) {
            return DEFAULT_MESSAGE;
        }
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource("static" + fileName);
        final Path path = Path.of(resource.getPath());

        return Files.readString(path);
    }

    private void readAllLine(final BufferedReader bufferedReader, String line) throws IOException {
        while (!line.equals("")) {
            line = bufferedReader.readLine();
            log.debug(line);
        }
    }
}
