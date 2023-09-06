package nextstep.org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.dto.LoginResponseDto;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String EMPTY_LINE = "";
    private static final String RESOURCES_PATH_PREFIX = "static";
    private static final int ACCEPT_HEADER_BEST_CONTENT_TYPE_INDEX = 0;

    private final Socket connection;
    private final HandlerMapper handlerMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.handlerMapper = new HandlerMapper();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = connection.getOutputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            StartLine startLine = new StartLine(bufferedReader.readLine());
            Map<String, String> requestHeaders = extractHeaders(bufferedReader);

            Map<String, String> parsedBody = new HashMap<>();
            if (requestHeaders.containsKey("Content-Length")) {
                String requestBody = extractRequestBody(bufferedReader, requestHeaders);
                Arrays.asList(requestBody.split("&")).forEach(parsedData -> {
                    String[] splited = parsedData.split("=");
                    parsedBody.put(splited[KEY_INDEX], splited[VALUE_INDEX]);
                });
            }

            HttpCookie httpCookie = new HttpCookie();
            if (requestHeaders.containsKey("Cookie")) {
                httpCookie.parseCookieHeaders(requestHeaders.get("Cookie"));
            }

            Map<String, String> queryParams = new HashMap<>();
            if (startLine.hasQueryString()) {
                Arrays.asList(startLine.getQueryString().split("&"))
                        .forEach(queryParam -> {
                            String[] splited = queryParam.split("=");
                            queryParams.put(splited[KEY_INDEX], splited[VALUE_INDEX]);
                        });
            }

            String response = null;
            String requestPath = startLine.getPath();

            Object handler = handlerMapper.mapHandler(requestPath);
            if (Objects.nonNull(handler) && startLine.getHttpMethod().equals("POST")
                    && requestPath.equals("/login")) {
                LoginController loginController = (LoginController) handler;
                LoginResponseDto loginDto = loginController.login(httpCookie,
                        parsedBody.get("account"),
                        parsedBody.get("password"));

                response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        String.format("Location: %s \r\n", loginDto.getRedirectUrl()));

                if (!httpCookie.isEmpty()) {
                    response += httpCookie.createSetCookieHeader();
                }
                response += "";
            }

            if (Objects.nonNull(handler) && startLine.getHttpMethod().equals("POST")
                    && requestPath.equals("/register")) {
                LoginController loginController = (LoginController) handler;
                LoginResponseDto loginDto = loginController.register(parsedBody.get("account"),
                        parsedBody.get("password"), parsedBody.get("email"));

                response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        String.format("Location: %s ", loginDto.getRedirectUrl()),
                        "");
            }

            if (startLine.getHttpMethod().equals("GET") && Objects.isNull(response)) {
                String contentType = negotiateContent(requestHeaders.get("Accept"));

                // Todo: createResponseBody() pageController로 위임해보기
                // Todo: 헤더에 담긴 sessionId 유효성 검증
                if (requestPath.contains("/login") && httpCookie.hasCookie("JSESSIONID")) {
                    response = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: /index.html ",
                            "");

                    writeResponse(outputStream, response);
                    return;
                }

                String responseBody = createResponseBody(requestPath);
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        String.format("Content-Type: %s;charset=utf-8 ", contentType),
                        String.format("Content-Length: %s ",
                                responseBody.getBytes(StandardCharsets.UTF_8).length),
                        "",
                        responseBody);
            }

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestBody(
            BufferedReader bufferedReader,
            Map<String, String> requestHeaders
    ) throws IOException {
        int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String negotiateContent(String acceptHeader) {
        if (Objects.isNull(acceptHeader)) {
            return "text/html";
        }
        List<String> splitUrl = Arrays.asList(acceptHeader.split(","));
        return splitUrl.get(ACCEPT_HEADER_BEST_CONTENT_TYPE_INDEX);
    }

    private Map<String, String> extractHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String line;
        while (!EMPTY_LINE.equals(line = bufferedReader.readLine())) {
            String[] splited = line.split(": ");
            requestHeaders.put(splited[KEY_INDEX], splited[VALUE_INDEX].strip());
        }
        return requestHeaders;
    }


    private String createResponseBody(String requestURL) {
        if (requestURL.equals("/")) {
            return "Hello world!";
        }

        String resourceName = RESOURCES_PATH_PREFIX + requestURL;
        if (!resourceName.contains(".")) {
            resourceName += ".html";
        }
        URL resource = getClass().getClassLoader().getResource(resourceName);

        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }
}
