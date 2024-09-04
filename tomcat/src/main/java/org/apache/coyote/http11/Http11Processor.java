package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
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
            String request = bufferedReader.readLine();
            System.out.println(request);

            outputStream.write(handleStaticRequest(request));
            outputStream.flush();

//        }
//

//
//            String request = bufferedReader.readLine();
//            if (request == null || request.isEmpty()) {
//                return;
//            }

//            if (request.contains("/login")) {
//                System.out.println(request);
//
//                String fileName = request.split(" ")[1];
//
//                File directory = new File(getClass().getClassLoader().getResource("static/login").getFile());
//                System.out.println(directory.getPath());

//                resources.hasMoreElements();
            return;
//                Path resourcePath = Files.find(path, 100,
//                                (p, a) -> p.toString().toLowerCase().startsWith(fileName))
//                        .findAny().orElseThrow();
//
//                System.out.println(resourcePath);
//
//
//                String responseBody = Files.readString(resourcePath);
//                final var response = String.join("\r\n",
//                        "HTTP/1.1 200 OK ",
//                        "Content-Type: " + +";charset=utf-8 ",
//                        "Content-Length: " + responseBody.getBytes().length + " ",
//                        "",
//                        responseBody);
//
//                outputStream.write(response.getBytes());
//                outputStream.flush();
//                return;
//            }
//
//            String fileName = request.split(" ")[1];
//
//            String path = getClass().getClassLoader().getResource("static" + fileName).getPath();
//
//            String responseBody = Files.readString(Path.of(path));
//
//            if (request.equals("/")) {
//                outputStream.write("helllo world!".getBytes());
//                outputStream.flush();
//                return;
//            }
//
//            final var response = String.join("\r\n",
//                    "HTTP/1.1 200 OK ",
//                    "Content-Type: text/html;charset=utf-8 ",
//                    "Content-Length: " + responseBody.getBytes().length + " ",
//                    "",
//                    responseBody);
//
//            outputStream.write(response.getBytes());
//            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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

        String responseBody = Files.readString(
                Path.of(getClass().getClassLoader().getResource("static" + fileName).toURI())
        );

        log.info("account={}", keyValues.get("account"));
        log.info("password={}", keyValues.get("password"));

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
