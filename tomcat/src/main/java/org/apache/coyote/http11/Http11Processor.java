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
import java.util.HashMap;
import java.util.Map;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();

            if (line == null || line.isEmpty()) {
                return;
            }

            HttpRequest httpRequest = new HttpRequest(line);

            String response = response(httpRequest.getPath());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String response(String uri) throws URISyntaxException, IOException {
        String responseBody;
        String contentType = "text/html;charset=utf-8 ";

        if (uri.equals("/")) {
            responseBody = "Hello world!";
        } else {
            int index = uri.indexOf("?");
            String path = uri;
            String queryString = "";
            if (index >= 0) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            }
            Map<String, String> map = new HashMap<>();

            if (!queryString.isEmpty()) {
                String[] strings = queryString.split("&");
                for (String string : strings) {
                    String[] keyValue = string.split("=");
                    map.put(keyValue[0], keyValue[1]);
                }

                User user = InMemoryUserRepository.findByAccount(map.get("account"))
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
                System.out.println(user);
            }

            if (path.contains("login")) {
                path += ".html";
            }

            path = "static" + path;
            System.out.println(path);
            URL resource = getClass().getClassLoader().getResource(path);
            if (resource.getPath().endsWith(".css")) {
                contentType = "text/css;charset=utf-8 ";
            }
            responseBody = new String(Files.readAllBytes(Path.of(resource.toURI())));
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
