package org.apache.coyote.http11;

import com.techcourse.application.LoginService;
import com.techcourse.presentation.LoginController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class RequestResolver {

    public String resolve(String request) throws IOException {
        if (request.contains("?")) {
            return resolveAPI(request);
        }
        return resolveStatic(request);
    }

    private String resolveAPI(final String request) throws IOException {
        String[] requests = request.split("\\?");

        String uri = requests[0];
        List<String> paramsEntry = Arrays.stream(requests[1].split("&")).toList();

        Map<String, String> params = paramsEntry.stream()
                .map(s -> Map.entry(s.split("=")[0], s.split("=")[1]))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        if (uri.equals("/login")) {
            String fileName = new LoginController(new LoginService()).login(params);

            final URL resource = getClass().getClassLoader().getResource("static" + fileName);

            Path filePath = new File(resource.getFile()).toPath();

            final String contentType = Files.probeContentType(filePath);
            final var responseBody = new String(Files.readAllBytes(filePath));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            return response;
        }

        return "";
    }

    private String resolveStatic(final String uri) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + uri);

        if ("/".equals(uri)) {
            final var responseBody = "Hello world!";

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            return response;
        }

        if (resource == null) {
            final URL notFound = getClass().getClassLoader().getResource("static/404.html");
            Path filePath = new File(notFound.getFile()).toPath();

            final String contentType = Files.probeContentType(filePath);
            final var responseBody = new String(Files.readAllBytes(filePath));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            return response;
        }

        Path filePath = new File(resource.getFile()).toPath();

        final String contentType = Files.probeContentType(filePath);
        final var responseBody = new String(Files.readAllBytes(filePath));

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        return response;
    }
}
