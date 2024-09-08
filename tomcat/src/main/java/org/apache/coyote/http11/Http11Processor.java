package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.QueryParameters;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.FileTypeChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequest.from(inputStream);
            log.info("http request : {}", request);
            HttpResponse response = HttpResponse.from(request);

            handle(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException exception) {
            log.error("요청 처리 과정 중 에러 발생 : {}", exception.getMessage());
        }
    }

    private void handle(HttpRequest request, HttpResponse response) throws IOException {
        try {
            HttpMethod method = request.getMethod();
            String targetPath = request.getTargetPath();
            QueryParameters queryParameters = request.getTargetQueryParameters();
            if (method.isGet() && "/".equals(targetPath)) {
                getView("index.html", response);
                return;
            }
            if (method == HttpMethod.GET && "/login".equals(targetPath) && queryParameters.hasParameters()) {
                login(request, response);
                return;
            }
            if (method == HttpMethod.GET && FileTypeChecker.isSupported(targetPath)) {
                getView(targetPath, response);
                return;
            }
            if (method == HttpMethod.GET && "/login".equals(targetPath)) {
                getView("login.html", response);
                return;
            }
        } catch (Exception exception) {
            log.error("요청을 처리할 수 없습니다. detail : {}", exception.getMessage());
            getView("/404.html", response);
        }
    }

    private void getView(String fileName, HttpResponse response) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/" + fileName);
        if (resource == null) {
            throw new IllegalArgumentException(fileName + "  파일이 존재하지 않습니다.");
        }
        Path path = Path.of(resource.getPath());
        String contentType = Files.probeContentType(path);
        response.addContentType(contentType);
        response.addBody(new String(Files.readAllBytes(path)));
        response.updateHttpStatus(HttpStatus.OK);
        if (response.has5xxCode()) {
            throw new RuntimeException("서버 내부에 오류 발생가 발생했습니다.");
        }
    }

    private void login(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getTargetQueryParameters().getValueBy("account");
        String password = request.getTargetQueryParameters().getValueBy("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseGet(() -> {
                    log.debug("{} - 존재하지 않는 회원의 로그인 요청", account);
                    throw new IllegalArgumentException("존재하지 않는 회원 입니다.");
                });
        if (!user.checkPassword(password)) {
            log.debug("회원과 일치하지 않는 비밀번호 - 회원 정보 : {}, 입력한 비밀번호 {}", user, password);
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        log.info("{} - 회원 로그인 성공", user);
        getView("index.html", response);
    }
}
