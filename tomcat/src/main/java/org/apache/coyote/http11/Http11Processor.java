package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String httpLine = readHttpLine(reader);
            String httpMethod = readHttpMethod(httpLine);
            String resourceName = getResourceName(httpLine);
            Map<String, String> httpRequestHeaders = readRequestHeaders(reader);

            HttpRequests httpRequests = HttpRequests.ofResourceNameAndMethod(resourceName, httpMethod);

            String response = toHttpResponse(httpRequests, resourceName, httpRequestHeaders, reader).serialize();
            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException | URISyntaxException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readHttpLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private String readHttpMethod(String httpLine) {
        String resourceName = httpLine.split(" ")[0];
        return resourceName;
    }

    private String getResourceName(String httpLine) {
        String resourceName = httpLine.split(" ")[1];
        return resourceName;
    }

    private Map<String, String> readRequestHeaders(BufferedReader reader) throws IOException {
        List<String> headers = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if ("".equals(line)) {
                break;
            }
            headers.add(line);
        }
        return headers.stream()
                .map(header -> header.split(": "))
                .collect(Collectors.toMap(value -> value[0], value -> value[1])
                );
    }

    public HttpResponse toHttpResponse(HttpRequests httpRequests, String resourceName, Map<String, String> httpRequestHeaders, BufferedReader reader) throws IOException, URISyntaxException {
        if (httpRequests.equals(HttpRequests.HELLO)) {
            String responseBody = "Hello world!";
            return HttpResponse.of(httpRequests, responseBody);
        }
        if (httpRequests.equals(HttpRequests.LOGIN) && resourceName.contains("?")) {
            int index = resourceName.indexOf("?");
            String queryString = resourceName.substring(index + 1);
            Map<String, String> queryParams = new HashMap<>();
            List<String> querys = Arrays.stream(queryString.split("&")).collect(Collectors.toList());
            for (String query : querys) {
                String[] keyAndValue = query.split("=");
                queryParams.put(keyAndValue[0], keyAndValue[1]);
            }
            Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.get("account"));
            if (user.isEmpty() || !user.get().checkPassword(queryParams.get("password"))) {
                Path path = HttpRequests.UNAUTHORIZED.readPath();
                byte[] fileBytes = readBytes(path);
                String responseBody = new String(fileBytes);
                return HttpResponse.of(httpRequests, responseBody);
            }
            return HttpResponse.ofLoginRedirect(HttpRequests.FOUND);

        }
        if (httpRequests.equals(HttpRequests.REGISTER_MEMBER)) {
            if (httpRequestHeaders.containsKey("Content-Length")) {
                String body = readHttpBody(httpRequestHeaders, reader);
                Map<String, String> userInfo = Arrays.stream(body.split("&"))
                        .map(value -> value.split("="))
                        .collect(Collectors.toMap(value -> value[0], value -> value[1]));
                User user = new User(userInfo.get("account"), userInfo.get("password"), userInfo.get("email"));
                log.info("user = {}", user);
                InMemoryUserRepository.save(user);
                byte[] fileBytes = readBytes(HttpRequests.INDEX.readPath());
                String responseBody = new String(fileBytes);
                return HttpResponse.of(httpRequests, responseBody);
            }
        }

        Path path = httpRequests.readPath();
        byte[] fileBytes = readBytes(path);
        String responseBody = new String(fileBytes);
        return HttpResponse.of(httpRequests, responseBody);
    }

    private String readHttpBody(Map<String, String> httpRequestHeaders, BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }


}
