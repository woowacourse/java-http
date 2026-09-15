package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCES_PREFIX = "static";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    private static String getUri(String line) {
        return line.split(" ")[1];
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
            String line = reader.readLine();
            String method = line.split(" ")[0];
            String path = getPath(line);

            final var responseBody = getResponseBody(method, line);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + getExtension(path) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getPath(String line) {
        String uri = getUri(line);
        int idx = uri.indexOf('?');
        if(idx != -1) {
            uri = uri.substring(0, idx);
        }
        return uri;
    }

    private Map<String, String> getParameters(String line) {
        String uri = getUri(line);
        int idx = uri.indexOf('?');
        if(idx != -1) {
            return getStringStringMap(uri.substring(idx + 1));
        }
        throw new IllegalArgumentException("URI에 파라미터가 없습니다.");
    }

    @Nonnull
    private Map<String, String> getStringStringMap(String queryString) {
        String[] splitQuery = queryString.split("&");
        Map<String, String> map = new HashMap<>();
        for (String s : splitQuery) {
            String[] kv = s.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    private String getResponseBody(String method, String line) {
        String path = getPath(line);

        if (path.equals("/") && method.equals("GET")) {
            return "Hello world!";
        }
        if (path.equals("/login") && method.equals("GET")) {
            Map<String, String> params = getParameters(line);
            User user = InMemoryUserRepository.findByAccount(params.get("account")).orElse(null);
            if(user == null) {
                return "없는 유저입니다. 다시 입력해주세요";
            }
            log.info(user.toString());
            return modelToView("/login.html");
        }
        return modelToView(path);
    }

    private String getExtension(String path) {
        if (path.endsWith(".html")) {
            return "html";
        }
        if (path.endsWith(".css")) {
            return "css";
        }
        return "html";
    }

    @Nonnull
    private String modelToView(String uri) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + uri);
        if (resource == null) {
            log.info("존재하지 않는 파일 명입니다. 파일 경로를 확인해주세요." + uri);
            return "경로가 잘못됐습니다!!!";
        }
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
