package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.HttpInputStreamReader;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
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
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {

            HttpInputStreamReader httpInputStreamReader = new HttpInputStreamReader(inputStream);
            HttpRequest httpRequest = httpInputStreamReader.readHttpRequest();

            if (isStaticResourceRequest(httpRequest)) {
                outputStream.write(Objects.requireNonNull(staticResourceController(httpRequest)).getBytes());
            } else if (httpRequest.getHttpMethod().equals("GET") && httpRequest.getUri().startsWith("/login")) {
                outputStream.write(getUserController(httpRequest).getBytes());
            } else if (httpRequest.getHttpMethod().equals("POST") && httpRequest.getUri().startsWith("/register")) {
                outputStream.write(postUserController(httpRequest).getBytes());
            }

            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private HttpResponse postUserController(HttpRequest httpRequest) throws IOException {
        Map<String, String> params = new HashMap<>();
        List<String> split = Arrays.asList(httpRequest.getBody().split("&"));
        split.stream().forEach(it -> {
            String[] split1 = it.split("=");
            params.put(split1[0], split1[1]);
        });

        InMemoryUserRepository.save(new User(params.get("account"), params.get("password"), params.get("email")));
        Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
        log.debug("회원 가입 완료! 유저 정보 : {}", user.get());

        HttpResponse httpResponse = new HttpResponse("HTTP/1.1", "302", "Found");
        httpResponse.addContentType("text/html;charset=utf-8");
        httpResponse.addLocation("index.html");
        return httpResponse;
    }

    private HttpResponse getUserController(HttpRequest httpRequest) throws IOException {
        Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getParameterValues("account"));
        HttpResponse httpResponse = new HttpResponse("HTTP/1.1", "302", "Found");
        httpResponse.addContentType("text/html;charset=utf-8");

        if (user.isEmpty() || !user.get().checkPassword(httpRequest.getParameterValues("password"))) {
            httpResponse.addLocation("401.html");
            return httpResponse;
        }

        httpResponse.addLocation("index.html");
        httpResponse.addBody(user.toString());

        log.debug("로그인 완료! 유저 정보 : {}", user);
        return httpResponse;
    }

    private HttpResponse staticResourceController(HttpRequest httpRequest) throws IOException {
        String fileName = httpRequest.getUri();
        URL resource = getClass().getResource("/static/" + fileName);

        if (Objects.isNull(resource)) {
            resource = getClass().getResource("/static/" + fileName + ".html");
        }

        HttpResponse httpResponse = new HttpResponse("HTTP/1.1", "200", "OK");
        if (fileName.endsWith(".css")) {
            httpResponse.addContentType("text/css,*/*;q=0.1");
        } else {
            httpResponse.addContentType("text/html;charset=utf-8");
        }

        String responseBody = readFile(resource);
        httpResponse.addBody(responseBody);
        return httpResponse;
    }

    private String readFile(URL resource) throws IOException {
        Path path = new File(resource.getPath()).toPath();
        List<String> file = Files.readAllLines(path);
        String responseBody = String.join("\r\n", file);
        return responseBody;
    }

    public boolean isStaticResourceRequest(HttpRequest httpRequest) {
        String uri = httpRequest.getUri();

        return httpRequest.getBody().isEmpty()
            && (!Objects.isNull(getClass().getResource("/static/" + uri))
            || !Objects.isNull(getClass().getResource("/static/" + uri + ".html")));
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
