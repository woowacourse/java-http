package org.apache.coyote.http11;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_URL = "/default.html";
    private static final String EMPTY_URL = "/";
    private static final String PATH = "static";
    private static final String END_OF_LINE = "";

    private boolean isActive = false;
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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            // read input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String request = readAsUTF8(reader);
            // parse headers
            Map<String, String> headers = parseHeaders(request);

            // Query String
            String response = makeResponse(headers, reader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponse(Map<String, String> headers, BufferedReader reader) throws IOException {
        if (Objects.equals(headers.get("HTTP Method"), "POST")) {
            String url = headers.get("URL");

            if (url.contains("/login")) {
                String code = "302 Found";
                String location = "index.html";

                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);

                List<String> parameters = Arrays.stream(requestBody.split("&")).collect(toList());
                Map<String, String> parametersMap = new HashMap<>();
                for (String parameter : parameters) {
                    int delimiterIndex = parameter.indexOf("=");
                    String field = parameter.substring(0, delimiterIndex);
                    String value = parameter.substring(delimiterIndex + 1);
                    parametersMap.put(field, value);
                }

                String account = parametersMap.get("account");
                String password = parametersMap.get("password");

                try {
                    checkUserCredential(account, password);
                } catch (Exception e) {
                    code = "401 Unauthorized ";
                    location = "401.html";
                }

                File file = mapToResourceFile(location);

                // file type
                String fileName = file.getName();
                String fileExtension = extractFileExtension(fileName);
                String type = mapMimeType(fileExtension);

                // resource
                String responseBody = new String(Files.readAllBytes(file.toPath()));

                return String.join(
                        System.lineSeparator(),
                        "HTTP/1.1 " + code,
                        "Location:" + location,
                        "Content-Type: " + type + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody
                );
            }

            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);

            List<String> parameters = Arrays.stream(requestBody.split("&")).collect(toList());
            Map<String, String> parametersMap = new HashMap<>();
            for (String parameter : parameters) {
                int delimiterIndex = parameter.indexOf("=");
                String field = parameter.substring(0, delimiterIndex);
                String value = parameter.substring(delimiterIndex + 1);
                parametersMap.put(field, value);
            }
            User user = new User(parametersMap.get("account"), parametersMap.get("password"),
                    parametersMap.get("email"));
            InMemoryUserRepository.save(user);

            String location = "index.html";
            String code = "200 OK ";

            File file = mapToResourceFile(location);

            // file type
            String fileName = file.getName();
            String fileExtension = extractFileExtension(fileName);
            String type = mapMimeType(fileExtension);

            // resource
            String responseBody = new String(Files.readAllBytes(file.toPath()));

            return String.join(
                    System.lineSeparator(),
                    "HTTP/1.1 " + code,
                    "Location:" + location,
                    "Content-Type: " + type + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody
            );
        }
        String url = headers.get("URL");

        if (Objects.equals(url, "/")) {
            return makeRootResponse();
        }

        File file = mapToResourceFile(url);

        // file type
        String fileName = file.getName();
        String fileExtension = extractFileExtension(fileName);
        String type = mapMimeType(fileExtension);

        // resource
        String responseBody = new String(Files.readAllBytes(file.toPath()));

        return String.join(
                System.lineSeparator(),
                "HTTP/1.1 " + "200 OK ",
                "Content-Type: " + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private void checkUserCredential(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정이 없거나 비밀번호가 틀렸어요"));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("계정이 없거나 비밀번호가 틀렸어요");
        }
        isActive = true;
        log.info("user : {}", user);
    }

    private String makeRootResponse() {
        String responseBody = "Hello world!";
        return String.join(
                System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private String mapMimeType(String fileExtension) {
        if (Objects.equals(fileExtension, "html")) {
            return "text/html";
        }
        if (Objects.equals(fileExtension, "css")) {
            return "text/css";
        }
        if (Objects.equals(fileExtension, "svg")) {
            return "image/svg+xml";
        }
        if (Objects.equals(fileExtension, "js")) {
            return "text/javascript";
        }
        return "text/plain";
    }

    private String extractFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    private String readAsUTF8(BufferedReader reader) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null & !Objects.equals(END_OF_LINE, line)) {
            builder.append(line).append(System.lineSeparator());
        }

        return builder.toString();
    }

    private Map<String, String> parseHeaders(String request) {
        Map<String, String> headers = new HashMap<>();
        List<String> lines = Arrays.stream(request.split(System.lineSeparator())).collect(toList());

        List<String> httpInformation = Arrays.stream(lines.get(0).split(" ")).collect(toList());
        headers.put("HTTP Method", httpInformation.get(0));
        headers.put("URL", httpInformation.get(1));
        headers.put("HTTP version", httpInformation.get(2));

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int standardIndex = line.indexOf(":");
            String header = line.substring(0, standardIndex).strip();
            String content = line.substring(standardIndex + 1).strip();
            headers.put(header, content);
        }
        return headers;
    }

    private File mapToResourceFile(String url) {
        if (!url.contains(".")) {
            url = url + ".html";
        }
        if (!url.contains("/")) {
            url = "/" + url;
        }

        URL path = getClass().getClassLoader().getResource(PATH + url);
        return new File(Objects.requireNonNull(path).getFile());
    }
}
