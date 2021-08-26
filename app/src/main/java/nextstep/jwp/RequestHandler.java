package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.http.request.*;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private static final HttpRequestParser HTTP_REQUEST_PARSER = new HttpRequestParser();
    private static final StaticResourceFinder STATIC_RESOURCE_FINDER = new StaticResourceFinder();
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

            final HttpRequest httpRequest = HTTP_REQUEST_PARSER.parse(inputStream);
            final RequestLine requestLine = httpRequest.getRequestLine();
            final RequestBody requestBody = httpRequest.getBody();

            HttpResponse httpResponse = null;

            if (httpRequest.hasMethod(HttpMethod.GET) && httpRequest.isUriPatternMatchedTo(".+\\.(html|css|js|ico)$")) {
                final String filePath = requestLine.getUri();
                final StaticResource staticResource = STATIC_RESOURCE_FINDER.findStaticResource(filePath);
                httpResponse = new HttpResponse(
                        200,
                        staticResource.getContentType(),
                        staticResource.getContent());
            }
            if (httpRequest.hasMethod(HttpMethod.GET) && httpRequest.hasUri("/register")) {
                final String filePath = requestLine.getUri() + HTML_SUFFIX;
                final StaticResource staticResource = STATIC_RESOURCE_FINDER.findStaticResource(filePath);
                httpResponse = new HttpResponse(
                        200, 
                        staticResource.getContentType(), 
                        staticResource.getContent());
            }
            if (httpRequest.hasMethod(HttpMethod.POST) && httpRequest.hasUri("/register")) {
                final String account = requestBody.getParameter("account");
                final String email = requestBody.getParameter("email");
                final String password = requestBody.getParameter("password");
                final User signupUser = new User(account, password, email);
                LOG.debug("회원가입 요청 account : {}", signupUser.getAccount());
                LOG.debug("회원가입 요청 email : {}", signupUser.getEmail());
                LOG.debug("회원가입 요청 password : {}", signupUser.getPassword());
                InMemoryUserRepository.save(signupUser);
                httpResponse = new HttpResponse(
                        302,
                        "http://localhost:8080/index.html");
            }
            if (httpRequest.hasMethod(HttpMethod.GET) && httpRequest.hasUri("/login")) {
                final String filePath = requestLine.getUri() + HTML_SUFFIX;
                final StaticResource staticResource = STATIC_RESOURCE_FINDER.findStaticResource(filePath);
                httpResponse = new HttpResponse(
                        200,
                        staticResource.getContentType(),
                        staticResource.getContent());
            }
            if (httpRequest.hasMethod(HttpMethod.POST) && httpRequest.hasUri("/login")) {
                final String account = requestBody.getParameter("account");
                final String password = requestBody.getParameter("password");
                final User signinUser = new User(account, password);
                LOG.debug("로그인 요청 account : {}", signinUser.getAccount());
                LOG.debug("로그인 요청 password : {}", signinUser.getPassword());
                String filePath = null;
                try {
                    final User foundUser = InMemoryUserRepository.findByAccount(signinUser.getAccount())
                            .orElseThrow(() -> new UnAuthorizedException("존재하지 않는 account 입니다."));
                    foundUser.validatePassword(signinUser.getPassword());
                    LOG.debug("로그인 성공!!");
                    filePath = "/index.html";
                } catch (UnAuthorizedException e) {
                    LOG.debug("로그인 실패");
                    filePath = "/401.html";
                } finally {
                    httpResponse = new HttpResponse(
                            302,
                            "http://localhost:8080" + filePath);
                }
            }

            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(Objects.requireNonNull(httpResponse).getBytes());
            bufferedOutputStream.flush();
        } catch (IOException exception) {
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
