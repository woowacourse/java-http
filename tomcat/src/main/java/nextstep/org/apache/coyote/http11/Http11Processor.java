package nextstep.org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public static final int HTTP_METHOD_INDEX = 0;
    public static final int URL_INDEX = 1;
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    public static final String EMPTY_LINE = "";
    public static final String RESOURCES_PREFIX = "static";
    public static final int ACCEPT_HEADER_BEST_CONTENT_TYPE_INDEX = 0;

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
            List<String> startLine = Arrays.asList(bufferedReader.readLine().split(" "));
            Map<String, String> requestHeaders = extractHeaders(bufferedReader);

            Map<String, String> parsedBody = new HashMap<>();
            if (requestHeaders.containsKey("Content-Length")) {
                String requestBody = extractRequestBody(bufferedReader, requestHeaders);
                Arrays.asList(requestBody.split("&")).forEach(parsedData -> {
                    String[] splited = parsedData.split("=");
                    parsedBody.put(splited[KEY_INDEX], splited[VALUE_INDEX]);
                });
            }

            Map<String, String> queryParams = new HashMap<>();

            String httpMethod = startLine.get(HTTP_METHOD_INDEX);
            String requestedUrl = startLine.get(URL_INDEX);
            int queryParamIndex = requestedUrl.indexOf('?');

            if (0 < queryParamIndex) {
                String queryParamValues = requestedUrl.substring(queryParamIndex + 1);
                Arrays.asList(queryParamValues.split("&"))
                        .forEach(queryParam -> {
                            String[] splited = queryParam.split("=");
                            queryParams.put(splited[KEY_INDEX], splited[VALUE_INDEX]);
                        });
                requestedUrl = requestedUrl.substring(0, queryParamIndex);
            }

            String response = null;

            Object handler = handlerMapper.mapHandler(requestedUrl);
            if (Objects.nonNull(handler) && httpMethod.equals("POST")
                    && requestedUrl.equals("/login")) {
                LoginController loginController = (LoginController) handler;
                LoginResponseDto loginDto = loginController.login(parsedBody.get("account"),
                        parsedBody.get("password"));

                response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        String.format("Location: %s ", loginDto.getRedirectUrl()),
                        "");
            }

            if (Objects.nonNull(handler) && httpMethod.equals("POST")
                    && requestedUrl.equals("/register")) {
                LoginController loginController = (LoginController) handler;
                LoginResponseDto loginDto = loginController.register(parsedBody.get("account"),
                        parsedBody.get("password"), parsedBody.get("email"));

                response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        String.format("Location: %s ", loginDto.getRedirectUrl()),
                        "");
            }

            if (httpMethod.equals("GET") && Objects.isNull(response)) {
                String contentType = negotiateContent(requestHeaders.get("Accept"));
                // Todo: createResponseBody() pageController로 위임해보기
                String responseBody = createResponseBody(requestedUrl);
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

    private String extractRequestBody(BufferedReader bufferedReader,
            Map<String, String> requestHeaders)
            throws IOException {
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

        String resourceName = RESOURCES_PREFIX + requestURL;
        if (!resourceName.contains(".")) {
            resourceName += ".html";
        }
        URL resource = getClass().getClassLoader().getResource(resourceName);
        Path path = Paths.get(resource.getPath().substring(1));

        try {
            return Files.readString(path);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }
}
