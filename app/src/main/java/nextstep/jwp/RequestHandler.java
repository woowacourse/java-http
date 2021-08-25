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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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

        try (final InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream())) {

            int statusCode = 200;
            String line = null;
            Map<String, String> httpRequestHeaders = new ConcurrentHashMap<>();
            boolean isFirstLine = true;
            String startLine = null;
            while (!"".equals(line)) {
                line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }
                if (isFirstLine) {
                    startLine = line;
                    isFirstLine = false;
                    continue;
                }
                if (line.contains(":")) {
                    final String key = line.split(":")[0];
                    final String value = line.split(":")[1];
                    httpRequestHeaders.put(key, value);
                }
            }

            final String method = startLine.split(" ")[0];
            LOG.debug("method : {}", method);

            String requestBody = null;
            if ("POST".equals(method) && httpRequestHeaders.containsKey("Content-Length")) {
                String contentLengthValue = httpRequestHeaders.get("Content-Length");
                contentLengthValue = contentLengthValue.trim();
                int contentLength = Integer.parseInt(contentLengthValue);
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
                LOG.debug("requestBody : {}", requestBody);
            }

            final String requestUri = startLine.split(" ")[1];
            LOG.debug("request URI : {}", requestUri);
            final int delimiterIndex = requestUri.indexOf("?");
            String uriPath = requestUri;
            String queryParam = null;
            if (delimiterIndex != -1) {
                LOG.debug("delimiterIndex : {}", delimiterIndex);
                uriPath = requestUri.substring(0, delimiterIndex);
                LOG.debug("uriPath : {}", uriPath);
                queryParam = requestUri.substring(delimiterIndex + 1);
                LOG.debug("queryParam : {}", queryParam);
            }
            String fileName = uriPath.substring(1);
            if ("GET".equals(method) && "/register".equals(uriPath)) {
                fileName = fileName + HTML_SUFFIX;
            }
            if ("POST".equals(method) && "/register".equals(uriPath)) {
                LOG.debug("body : {}", requestBody);
                if (!requestBody.isBlank()) {
                    String account = null;
                    String email = null;
                    String password = null;
                    final String[] splitQueryParam = requestBody.split("&");
                    for (String singleQueryParam : splitQueryParam) {
                        final String[] splitSingleQueryString = singleQueryParam.split("=");
                        final String key = splitSingleQueryString[0];
                        final String value = splitSingleQueryString[1];
                        if ("account".equals(key)) {
                            account = value;
                        }
                        if ("email".equals(key)) {
                            email = URLDecoder.decode(value, StandardCharsets.UTF_8);
                        }
                        if ("password".equals(key)) {
                            password = value;
                        }
                    }
                    final User signinRequestUser = new User(account, password, email);
                    LOG.debug("회원가입 요청 account : {}", signinRequestUser.getAccount());
                    LOG.debug("회원가입 요청 email : {}", signinRequestUser.getEmail());
                    LOG.debug("회원가입 요청 password : {}", signinRequestUser.getPassword());
                    InMemoryUserRepository.save(signinRequestUser);
                    fileName = "index.html";
                    statusCode = 302;
                }
            }
            if ("GET".equals(method) && "/login".equals(uriPath)) {
                fileName = fileName + HTML_SUFFIX;
            }
            if ("POST".equals(method) && "/login".equals(uriPath)) {
                LOG.debug("body : {}", requestBody);
                if (!requestBody.isBlank()) {
                    String account = null;
                    String password = null;
                    final String[] splitQueryParam = requestBody.split("&");
                    for (String singleQueryParam : splitQueryParam) {
                        final String[] splitSingleQueryString = singleQueryParam.split("=");
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

            String contentType = "text/html";
            String responseBody = null;
            if ("GET".equals(method)) {
                final URL url = getClass().getClassLoader().getResource("static/" + fileName);
                if (url == null) {
                    LOG.error("fileName : {}", fileName);
                    throw new IOException("fileName으로 찾은 url의 값이 null 입니다.");
                }

                String[] splitFileUrl = url.toString().split("\\.");
                final String fileNameExtension = splitFileUrl[splitFileUrl.length - 1];
                LOG.debug("파일 확장자 : {}", fileNameExtension);
                if ("html".equals(fileNameExtension)) {
                    contentType = "text/html";
                }
                if ("css".equals(fileNameExtension)) {
                    contentType = "text/css";
                }
                if ("js".equals(fileNameExtension)) {
                    contentType = "application/js";
                }
                final Path filePath = Paths.get(url.toURI());
                if (!"ico".equals(fileNameExtension)) {
                    final List<String> fileLines = Files.readAllLines(filePath);
                    responseBody = String.join(NEW_LINE, fileLines);
                }
                if ("ico".equals(fileNameExtension)) {
                    contentType = "image/x-icon";
                    responseBody = new File(filePath.toUri()).toString();
                }
            }

            String response = null;
            if (statusCode == 200) {
                response = String.join(NEW_LINE,
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }
            if (statusCode == 302) {
                response = String.join(NEW_LINE,
                        "HTTP/1.1 302 Found ",
                        "Location: http://localhost:8080/" + fileName);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
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
