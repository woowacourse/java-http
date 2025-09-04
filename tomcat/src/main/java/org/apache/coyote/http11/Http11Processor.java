package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
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

            Map<String, String> requestValues = readRequest(inputStream);
            if(!requestValues.containsKey("Uri")){
                return;
            }

            String uriAndQueryParameters = requestValues.get("Uri");
            String uri = uriAndQueryParameters;

            Map<String, String> queryParameters = new HashMap<>();
            int queryParameterIndex = uriAndQueryParameters.indexOf("?");
            if (queryParameterIndex != -1) {
                uri = uriAndQueryParameters.substring(0, queryParameterIndex);
                String queryParameterTexts = uriAndQueryParameters.substring(queryParameterIndex + 1,
                        uriAndQueryParameters.length());

                for (String queryParameterText : queryParameterTexts.split("&")){
                    String[] keyAndValue = queryParameterText.split("=");
                    queryParameters.put(keyAndValue[0], keyAndValue[1]);
                }
            }

            if("/login".equals(uri) && !queryParameters.isEmpty()){
                String account = queryParameters.getOrDefault("account", "");
                String password = queryParameters.getOrDefault("password", "");

                Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
                if(optionalUser.isEmpty()){
                    outputStream.write("HTTP/1.1 401 UNAUTHORIZED ".getBytes());
                    outputStream.flush();
                    return;
                }

                User user = optionalUser.get();
                if(!user.checkPassword(password)){
                    outputStream.write("HTTP/1.1 401 UNAUTHORIZED ".getBytes());
                    outputStream.flush();
                    return;
                }

                log.info("user: "+ user);
                return;
            }

            ClassLoader classLoader = getClass().getClassLoader();
            URL url = classLoader.getResource("static" + uri);

            if (url == null) {
                url = classLoader.getResource("static" + uri + ".html");
            }

            var responseBody = "Hello world!";
            var contentType = "text/html";
            if(url != null){
                Path resourcePath = Path.of(url.toURI());

                if(Files.isRegularFile(resourcePath)){
                    List<String> resourceValues = Files.readAllLines(resourcePath);
                    responseBody = resourceValues.stream()
                            .collect(Collectors.joining("\n")) + "\n";
                    contentType = Files.probeContentType(resourcePath);
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: "+ contentType +";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> values = new HashMap<>();

        String firstLine = bufferedReader.readLine();
        if(firstLine == null) return values;

        String[] requestLine = firstLine.split(" ");
        values.put("Method", requestLine[0]);
        values.put("Uri", requestLine[1]);
        values.put("Version", requestLine[2]);

        String line = bufferedReader.readLine();
        while (!"".equals(line)){
            String[] header = line.split(": ");
            values.put(header[0], header[1]);
            line = bufferedReader.readLine();
        }

        return values;
    }
}
