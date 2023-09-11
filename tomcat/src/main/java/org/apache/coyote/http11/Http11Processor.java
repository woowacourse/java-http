package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.HttpSession;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Map<String, Function<HttpRequest, HttpResponse>> getMethodControllerMapper = Map.of(
            "/", this::loadRootPage,
            "/login", this::loadLoginPage
    );
    private final Map<String, Function<HttpRequest, HttpResponse>> postMethodControllerMapper = Map.of(
            "/register", this::register,
            "/login", this::login
    );
    private final Map<HttpMethod, Map<String, Function<HttpRequest, HttpResponse>>> controllerMapperByMethod = Map.of(
            HttpMethod.GET, getMethodControllerMapper,
            HttpMethod.POST, postMethodControllerMapper
    );
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info(
                "connect host: {}, port: {}",
                connection.getInetAddress(),
                connection.getPort()
        );
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequestHeader httpRequestHeader = getHttpRequestHeader(bufferedReader);
            HttpRequestBody httpRequestBody = getHttpRequestBody(httpRequestHeader, bufferedReader);
            HttpRequest request = new HttpRequest(httpRequestHeader, httpRequestBody);
            String response = mapController(request);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestHeader getHttpRequestHeader(BufferedReader bufferedReader) throws IOException {
        StringBuilder header = new StringBuilder();
        String line;

        do {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            header.append(line).append("\n");
        } while (!"".equals(line));

        return extractHttpRequest(header);
    }

    private HttpRequestHeader extractHttpRequest(StringBuilder content) {
        String[] lines = content.toString()
                .split("\n");
        String[] methodAndRequestUrl = lines[0].split(" ");

        return HttpRequestHeader.of(
                methodAndRequestUrl[0],
                methodAndRequestUrl[1],
                Arrays.copyOfRange(lines, 1, lines.length)
        );
    }

    private HttpRequestBody getHttpRequestBody(HttpRequestHeader httpRequestHeader, BufferedReader bufferedReader) throws IOException {
        String header = httpRequestHeader.get("Content-Length");
        if (header.isBlank()) {
            return HttpRequestBody.from("");
        }

        int contentLength = Integer.parseInt(header);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        return HttpRequestBody.from(requestBody);
    }

    private String mapController(HttpRequest httpRequest) {
        HttpMethod method = httpRequest.getMethod();
        String path = httpRequest.getPath();

        return controllerMapperByMethod.getOrDefault(method, getMethodControllerMapper)
                .getOrDefault(path, this::loadExtraPage)
                .apply(httpRequest)
                .toString();
    }

    private HttpResponse loadExtraPage(HttpRequest httpRequest) {
        String responseBody = readForFilePath(convertAbsoluteUrl(httpRequest));
        HttpResponse httpResponse = new HttpResponse(200, "OK", responseBody);
        httpResponse.setAttribute(
                CONTENT_TYPE.getValue(),
                httpRequest.getContentType() + ";charset=utf-8"
        );
        httpResponse.setAttribute(
                CONTENT_LENGTH.getValue(),
                String.valueOf(responseBody.getBytes().length)
        );

        return httpResponse;
    }

    private HttpResponse loadLoginPage(HttpRequest httpRequest) {
        HttpSession httpSession = httpRequest.getHttpSession();
        User user = (User) httpSession.get("user");

        if (!Objects.isNull(user)) {
            HttpResponse httpResponse = new HttpResponse(302, "FOUND");
            httpResponse.sendRedirect("/index.html");
            return httpResponse;
        }

        String responseBody = readForFilePath(convertAbsoluteUrl(httpRequest));
        HttpResponse httpResponse = new HttpResponse(200, "OK", responseBody);
        httpResponse.setAttribute(
                CONTENT_TYPE.getValue(),
                httpRequest.getContentType() + ";charset=utf-8"
        );
        httpResponse.setAttribute(
                CONTENT_LENGTH.getValue(),
                String.valueOf(responseBody.getBytes().length)
        );
        return httpResponse;
    }

    private HttpResponse loadRootPage(HttpRequest httpRequest) {
        String responseBody = "Hello world!";
        HttpResponse httpResponse = new HttpResponse(200, "OK", responseBody);
        httpResponse.setAttribute(
                CONTENT_TYPE.getValue(),
                httpRequest.getContentType() + ";charset=utf-8"
        );
        httpResponse.setAttribute(
                CONTENT_LENGTH.getValue(),
                String.valueOf(responseBody.getBytes().length)
        );

        return httpResponse;
    }

    private HttpResponse register(HttpRequest httpRequest) {
        String account = httpRequest.getBodyAttribute("account");
        String password = httpRequest.getBodyAttribute("password");
        String email = httpRequest.getBodyAttribute("email");
        String redirectionUrl = "/index.html";
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        HttpResponse httpResponse = new HttpResponse(302, "FOUND");
        httpResponse.sendRedirect(redirectionUrl);

        return httpResponse;
    }

    private HttpResponse login(HttpRequest httpRequest) {
        String account = httpRequest.getBodyAttribute("account");
        String password = httpRequest.getBodyAttribute("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        HttpResponse httpResponse = new HttpResponse(302, "FOUND");
        String redirectionUrl = "/401.html";

        if (user.isPresent() && user.get().checkPassword(password)) {
            redirectionUrl = "/index.html";
            HttpSession httpSession = httpRequest.getHttpSession(true);
            httpResponse.addCookie("JSESSIONID=" + httpSession.getId());
            httpSession.add("user", user.get());
            log.info("로그인 성공! 아이디 : {}", account);
        }

        httpResponse.sendRedirect(redirectionUrl);

        return httpResponse;
    }

    private URL convertAbsoluteUrl(HttpRequest httpRequest) {
        return getClass().getClassLoader()
                .getResource("static" + httpRequest.getFilePath());
    }

    private String readForFilePath(URL path) {
        try (FileInputStream fileInputStream = new FileInputStream(path.getPath())) {
            return readFile(fileInputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    private String readFile(FileInputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder fileContent = new StringBuilder();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            fileContent.append(line).append("\n");
        }
        return fileContent.toString();
    }

}
