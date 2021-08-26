package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.http.request.*;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private static final HttpRequestParser httpRequestParser = new HttpRequestParser();
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

            final HttpRequest request = httpRequestParser.parse(inputStream);
            final RequestLine requestLine = request.getRequestLine();
            final RequestHeaders requestHeaders = request.getHeaders();
            final RequestBody requestBody = request.getBody();

            int statusCode = 200;

            String fileName = requestLine.getUriWithoutRootPath();
            if (request.hasMethod(HttpMethod.GET) && request.hasUri("/register")) {
                fileName = fileName + HTML_SUFFIX;
            }
            if (request.hasMethod(HttpMethod.POST) && request.hasUri("/register")) {
                final String account = requestBody.getParameter("account");
                final String email = requestBody.getParameter("email");
                final String password = requestBody.getParameter("password");
                final User signupUser = new User(account, password, email);
                LOG.debug("회원가입 요청 account : {}", signupUser.getAccount());
                LOG.debug("회원가입 요청 email : {}", signupUser.getEmail());
                LOG.debug("회원가입 요청 password : {}", signupUser.getPassword());
                InMemoryUserRepository.save(signupUser);
                fileName = "index.html";
                statusCode = 302;
            }
            if (request.hasMethod(HttpMethod.GET) && request.hasUri("/login")) {
                fileName = fileName + HTML_SUFFIX;
            }
            if (request.hasMethod(HttpMethod.POST) && request.hasUri("/login")) {
                final String account = requestBody.getParameter("account");
                final String password = requestBody.getParameter("password");
                final User signinUser = new User(account, password);
                LOG.debug("로그인 요청 account : {}", signinUser.getAccount());
                LOG.debug("로그인 요청 password : {}", signinUser.getPassword());
                try {
                    final User foundUser = InMemoryUserRepository.findByAccount(signinUser.getAccount())
                            .orElseThrow(() -> new UnAuthorizedException("존재하지 않는 account 입니다."));
                    foundUser.validatePassword(signinUser.getPassword());
                    LOG.debug("로그인 성공!!");
                    fileName = "index.html";
                } catch (UnAuthorizedException e) {
                    LOG.debug("로그인 실패");
                    fileName = "401.html";
                }
                statusCode = 302;
            }

            String contentType = "text/html";
            String responseBody = null;
            if (request.hasMethod(HttpMethod.GET)) {
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

            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(response.getBytes());
            bufferedOutputStream.flush();
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
