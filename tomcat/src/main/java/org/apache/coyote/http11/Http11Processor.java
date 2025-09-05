package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            Map<String, String> header = getHeader(inputStream);

            String requestResource = header.get("resource");

            String response;

            if (requestResource.startsWith("/login")) {
                int index = requestResource.indexOf("?");
                String matchedResource = requestResource.substring(0, index) + ".html";
                String queryString = requestResource.substring(index + 1);

                Map<String, String> queryParameters = new HashMap<>();
                String[] parameters = queryString.split("&");
                for (String parameter : parameters) {
                    String key = parameter.split("=")[0];
                    String value = parameter.split("=")[1];
                    queryParameters.put(key, value);
                }
                response = getResponse(matchedResource);

                User user = InMemoryUserRepository.findByAccount(queryParameters.get("account")).orElse(null);
                if (user != null) {
                    System.out.println(user);
                }
            } else {
                response = getResponse(requestResource);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getHeader(InputStream inputStream) throws IOException {

        Map<String, String> httpHeader = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = reader.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            throw new IOException("request is empty");
        }

        String[] requestLineSplit = requestLine.split(" ");
        String method = requestLineSplit[0];
        httpHeader.put("method", method);
        String resource = requestLineSplit[1];
        httpHeader.put("resource", resource);
        String version = requestLineSplit[2];
        httpHeader.put("version", version);

        String line;
        while(!"".equals((line = reader.readLine()))) {
            String[] split = line.split(":");
            String fieldName = split[0];
            String value = split[1];
            httpHeader.put(fieldName, value);
        }

        return httpHeader;
    }

    private String getResponse(String uri) throws IOException {
        if ("/".equals(uri)) {
            String body = null;
            body = "Hello world!";
            String contentType = "text/html";
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);
        }

        String resourcePath = "./static" + uri;
        URL systemResource = ClassLoader.getSystemResource(resourcePath);
        Path path = Path.of(systemResource.getPath());
        final var responseBody = Files.readAllBytes(path);

        String body = new String(responseBody);
        String extension = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
        String contentType = String.join("/","text",extension);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
