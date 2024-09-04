package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
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

            // parse request
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            int contentLength = -1;
            String s;
            while ((s = bufferedReader.readLine()) != null && !s.isEmpty()) {
                sb.append(s).append("\r\n");
                if (s.contains("Content-Length")) {
                    contentLength = Integer.parseInt(s.split(":")[1].strip());
                }
            }

            String requestBody = null;
            if (contentLength != -1) {
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
            }

            if (sb.isEmpty()) {
                return;
            }

            // handle request
            String request = sb.toString().split("\r\n")[0];

            // handle post
            if (request.contains("POST")) {
                outputStream.write(handleStaticPostRequest(request, requestBody));
                outputStream.flush();
                return;
            }

            log.info("request = {}", request);

            outputStream.write(handleStaticRequest(request));
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] handleStaticPostRequest(String request, String requestBody) {

        String[] requestBodySplit = requestBody.split("&");
        Map<String, String> requestBodyMap = new HashMap<>();
        for (int i = 0; i < requestBodySplit.length; i++) {
            String[] split = requestBodySplit[i].split("=");
            requestBodyMap.put(split[0], split[1]);
        }
        if(request.contains("login")){
            if (InMemoryUserRepository.findByAccount(requestBodyMap.get("account")).isEmpty()) {
                throw new IllegalArgumentException("account already exists");
            }
            return String.join("\r\n",
                            "HTTP/1.1 " + "302 Found" + " ",
                            "Content-Type: " + "text/html" + " ",
                            "Location: /index.html" + " ")
                    .getBytes();
        }

        if (InMemoryUserRepository.findByAccount(requestBodyMap.get("account")).isPresent()) {
            throw new IllegalArgumentException("account already exists");
        }

        InMemoryUserRepository.save(
                new User(
                        requestBodyMap.get("account"),
                        requestBodyMap.get("password"),
                        requestBodyMap.get("email")
                )
        );

        return String.join("\r\n",
                        "HTTP/1.1 " + "302 Found" + " ",
                        "Content-Type: " + "text/html" + " ",
                        "Location: /index.html" + " ")
                .getBytes();
    }

    private byte[] handleStaticRequest(String request) throws URISyntaxException, IOException {
        String file = request.split(" ")[1];
        validateFileName(file);
        String[] split = file.split("\\.");
        String fileName = split[0];
        if (fileName.contains("?")) {
            return getQueryParameterResponseBody(fileName);
        }

        if (fileName.equals("/")) {
            fileName = "/index.html";
        } else if (split.length == 1) {
            fileName += ".html";
        } else if (split.length == 2) {
            fileName = fileName + "." + split[1];
        }

        return getResponseBody(fileName);
    }

    private byte[] getQueryParameterResponseBody(String query) throws URISyntaxException, IOException {
        String[] split = query.split("\\?");
        String fileName = split[0] + ".html";
        String[] keyValueArray = split[1].split("&");
        Map<String, String> keyValues = new HashMap<>();
        for (int i = 0; i < keyValueArray.length; i++) {
            String[] keyValueSplit = keyValueArray[i].split("=");
            keyValues.put(keyValueSplit[0], keyValueSplit[1]);
        }

        String statusCode = "200 OK";

        log.info("account={}", keyValues.get("account"));
        log.info("password={}", keyValues.get("password"));

        if (fileName.equals("/login.html")) {
            if (keyValues.get("account").equals("gugu") && keyValues.get("password").equals("password")) {
                statusCode = "302 Found";
                return String.join("\r\n",
                                "HTTP/1.1 " + statusCode + " ",
                                "Content-Type: " + "text/html" + " ",
                                "Location: /index.html" + " ")
                        .getBytes();
            } else {
                fileName = "/401.html";
                statusCode = "401 Unauthorized";
            }
        }

//        if (fileName.equals("/register.html")) {
////            System.out.println();
//            statusCode = "302 Found";
//            return String.join("\r\n",
//                            "HTTP/1.1 " + statusCode + " ",
//                            "Content-Type: " + "text/html" + " ",
//                            "Location: /index.html" + " ")
//                    .getBytes();
//        }

        String extension = fileName.split("\\.")[1];
        if (extension.equals("js")) {
            extension = "javascript";
        }
        String responseBody = Files.readString(
                Path.of(getClass().getClassLoader().getResource("static" + fileName).toURI())
        );
        return String.join("\r\n",
                        "HTTP/1.1 " + statusCode + " ",
                        "Content-Type: " + "text/" + extension + " ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "", responseBody)
                .getBytes();
    }

    private byte[] getResponseBody(String fileName) throws URISyntaxException, IOException {
        String responseBody = Files.readString(
                Path.of(getClass().getClassLoader().getResource("static" + fileName).toURI())
        );
        String extension = fileName.split("\\.")[1];
        if (extension.equals("js")) {
            extension = "javascript";
        }
        return String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + "text/" + extension + " ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "", responseBody)
                .getBytes();
    }

    private void validateFileName(String fileName) {
        try {
            getClass().getClassLoader().getResource("static" + fileName);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("file does not exist");
        }
    }
}
