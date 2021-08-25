package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String HTML_SUFFIX = ".html";

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            int statusCode = 200;
            final List<String> header = getHeader(inputStream);

            final String headerFirstLine = header.get(0);
            final String requestUri = headerFirstLine.split(" ")[1];
            final int delimiterIndex = requestUri.indexOf("?");
            String uriPath = requestUri;
            String queryString = null;
            if (delimiterIndex != -1) {
                LOG.debug("delimiterIndex : {}", delimiterIndex);
                uriPath = requestUri.substring(0, delimiterIndex);
                LOG.debug("uriPath : {}", uriPath);
                queryString = requestUri.substring(delimiterIndex + 1);
                LOG.debug("queryString : {}", queryString);
            }
            String fileName = uriPath.substring(1);;
            if ("/login".equals(uriPath)) {
                fileName = fileName + HTML_SUFFIX;
                if (queryString != null) {
                    String account = null;
                    String password = null;
                    final String[] splitQueryString = queryString.split("&");
                    for (String singleQueryString : splitQueryString) {
                        final String[] splitSingleQueryString = singleQueryString.split("=");
                        final String key = splitSingleQueryString[0];
                        final String value = splitSingleQueryString[1];
                        if ("account".equals(key)) {
                            account = value;
                        }
                        if ("password".equals(key)) {
                            password = value;
                        }
                    }
                    final User loginRequestUser = new User(account, password);
                    LOG.debug("로그인 요청 account : {}", loginRequestUser.getAccount());
                    LOG.debug("로그인 요청 password : {}", loginRequestUser.getPassword());
                    try {
                        final User foundUser = InMemoryUserRepository.findByAccount(loginRequestUser.getAccount())
                                .orElseThrow(() -> new UnAuthorizedException("존재하지 않는 account 입니다."));
                        foundUser.validatePassword(loginRequestUser.getPassword());
                        LOG.debug("로그인 성공!!");
                        LOG.debug("account : {}", foundUser.getAccount());
                        LOG.debug("email : {}", foundUser.getEmail());
                        fileName = "index.html";
                    } catch (UnAuthorizedException e) {
                        LOG.debug("로그인 실패");
                        fileName = "401.html";
                    }
                    statusCode = 302;
                }
            }

            final URL url = getClass().getClassLoader().getResource("static/" + fileName);
            if (url == null) {
                LOG.error("fileName : {}", fileName);
                throw new IOException("fileName으로 찾은 url의 값이 null 입니다.");
            }

            final Path filePath = Paths.get(url.toURI());
            final List<String> fileLines = Files.readAllLines(filePath);
            final String responseBody = String.join(NEW_LINE, fileLines);

            String response = null;
            if (statusCode == 200) {
                response = String.join(NEW_LINE,
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }
            if (statusCode == 302) {
                response = String.join(NEW_LINE,
                        "HTTP/1.1 302 Found ",
                        "Location: http://localhost:8080/" + fileName,
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private List<String> getHeader(InputStream inputStream) throws IOException {
        final StringBuilder stringBuilder = getStringBuilderOfInputStreamContent(inputStream);
        final String[] splitStringBuilderContent = stringBuilder.toString().split(NEW_LINE);

        return Arrays.stream(splitStringBuilderContent)
                .collect(Collectors.toList());
    }

    private StringBuilder getStringBuilderOfInputStreamContent(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = getBufferedReader(inputStream);
        final StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while (!"".equals(line)) {
            line = bufferedReader.readLine();
            validateLineIsNotNull(line);
            stringBuilder.append(line);
            stringBuilder.append(NEW_LINE);
        }

        return stringBuilder;
    }

    private BufferedReader getBufferedReader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    private void validateLineIsNotNull(String line) throws IOException {
        if (line == null) {
            throw new IOException("BufferedReader에서 readLine()으로 읽은 line의 값이 null입니다.");
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
