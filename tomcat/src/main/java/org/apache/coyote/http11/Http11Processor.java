package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            Map<String, String> httpRequest = readHttpRequest(inputStream);
            if (httpRequest.isEmpty()) {
                return;
            }

            Map<String, String> queryParameters = extractQueryParameters(httpRequest);
            String uri = httpRequest.get("Uri");
            if("/login".equals(uri) && !queryParameters.isEmpty()){
                login(queryParameters, outputStream);
                return;
            }

            Path resourcePath = findResourcePath(uri, outputStream);
            if (resourcePath == null) {
                return;
            }

            String responseBody = readResponseBody(resourcePath);
            String contentType = Files.probeContentType(resourcePath);
            String httpResponse = createHttpResponse(contentType, responseBody);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> values = new HashMap<>();

        String firstLine = bufferedReader.readLine();
        if(firstLine == null) return values;

        String[] requestLine = firstLine.split(" ");

        if(requestLine.length < 3) return values;
        values.put("Method", requestLine[0]);
        values.put("Uri", requestLine[1]);
        values.put("Version", requestLine[2]);

        int queryParameterIndex = requestLine[1].indexOf("?");
        if(queryParameterIndex != -1){
            values.put("Uri", requestLine[1].substring(0, queryParameterIndex));
            values.put("QueryParameters", requestLine[1].substring(queryParameterIndex + 1, requestLine[1].length()));
        }


        String line = bufferedReader.readLine();
        while (!"".equals(line)){
            String[] header = line.split(": ");
            if(header.length != 2){
                return Map.of();
            }

            values.put(header[0], header[1]);
            line = bufferedReader.readLine();
        }

        return values;
    }

    private Map<String, String> extractQueryParameters(Map<String, String> httpRequest) {
        if (!httpRequest.containsKey("QueryParameters")) {
            return Map.of();
        }

        String queryParameterTexts = httpRequest.get("QueryParameters");
        return Arrays.stream(queryParameterTexts.split("&"))
                .map(queryParameterText -> queryParameterText.split("="))
                .collect(Collectors.toMap(
                        keyAndValue -> keyAndValue[0],
                        keyAndValue -> keyAndValue[1]
                ));
    }

    private void login(Map<String, String> queryParameters, OutputStream outputStream) throws IOException {
        String account = queryParameters.getOrDefault("account", "");
        String password = queryParameters.getOrDefault("password", "");

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if(optionalUser.isEmpty()){
            outputStream.write("HTTP/1.1 401 UNAUTHORIZED \r\n\r\n".getBytes());
            outputStream.flush();
            return;
        }

        User user = optionalUser.get();
        if(!user.checkPassword(password)){
            outputStream.write("HTTP/1.1 401 UNAUTHORIZED \r\n\r\n".getBytes());
            outputStream.flush();
            return;
        }

        log.info("user: {}", user);
    }

    private Path findResourcePath(String uri, OutputStream outputStream) throws IOException, URISyntaxException {
        URL url = findResourceURL(uri);
        if(url == null){
            String helloWorldHttpResponse = helloWorldHttpResponse();
            outputStream.write(helloWorldHttpResponse.getBytes());
            outputStream.flush();
            return null;
        }

        Path resourcePath = Path.of(url.toURI());
        Path normalizedResourcePath = resourcePath.normalize();
        if(!normalizedResourcePath.startsWith("/")){
            outputStream.write("HTTP/1.1 401 UNAUTHORIZED \r\n\r\n".getBytes());
            outputStream.flush();
            return null;
        }

        if(!Files.isRegularFile(normalizedResourcePath)){
            String helloWorldHttpResponse = helloWorldHttpResponse();
            outputStream.write(helloWorldHttpResponse.getBytes());
            outputStream.flush();
            return null;
        }


        return normalizedResourcePath;
    }

    private URL findResourceURL(String uri) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("static" + uri);

        if (url == null) {
            return classLoader.getResource("static" + uri + ".html");
        }
        return url;
    }

    private String helloWorldHttpResponse() {
        return String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
    }

    private String readResponseBody(Path resourcePath) throws IOException {
        return String.join("\n", Files.readAllLines(resourcePath)) + "\n";
    }

    private String createHttpResponse(String contentType, String responseBody) {
        return String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: "+ contentType +";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }
}
