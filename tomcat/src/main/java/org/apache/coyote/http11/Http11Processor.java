package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.parsher.QueryParser;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
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
            HttpResponse response = HttpResponse.create();

            handle(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String endpoint = request.getEndpoint();
            HttpMethod method = request.getMethod();
            if (method == HttpMethod.GET && "/".equals(endpoint)) {
                getView("/index.html", response);
                return;
            }
            if (method == HttpMethod.GET && "/index.html".equals(endpoint)) {
                getView("/index.html", response);
                return;
            }
            if (method == HttpMethod.GET && "/login".equals(endpoint)) {
                getView("/login.html", response);
                return;
            }
            if (method == HttpMethod.POST && "/login".equals(endpoint)) {
                login(request, response);
                return;
            }
            getView("/401.html", response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            getView("/404.html", response);
        }
    }

    private void getView(String request, HttpResponse response) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + request);
        if (resource == null) {
            throw new IllegalArgumentException("유효하지 하지 않은 요청 입니다.");
        }
        Path path = Path.of(resource.getPath());
        String contentType = Files.probeContentType(path);
        response.addBody(new String(Files.readAllBytes(path)));
        response.addContentType(contentType);
    }

    private void login(HttpRequest request, HttpResponse response) throws IOException {
        String body = request.getBody();
        Map<String, List<String>> queryStrings = QueryParser.parse(body);
        String account = queryStrings.get("account").getFirst();

        InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));

        getView("/index.html", response);
    }
}
