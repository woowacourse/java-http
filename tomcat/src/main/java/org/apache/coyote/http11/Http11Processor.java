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
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String[] requestLineParts = bufferedReader.readLine().split(" ");
            final var responseBody = getResponseBody(requestLineParts[1]);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    getContentType(requestLineParts[1]),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String requestUri) throws IOException {
        if (requestUri.contains("/login")) {
            String[] value = getQueryParameterValues(requestUri);
            User user = InMemoryUserRepository.findByAccount(value[0]).orElseThrow();
            log.info(user.toString());

            String paths = getStaticResource("/login.html");
            if (paths != null) {
                return paths;
            }
        }

        if (!requestUri.equals("/")) {
            String paths = getStaticResource(requestUri);
            if (paths != null) {
                return paths;
            }
        }
        return "Hello world!";
    }

    @Nonnull
    private String[] getQueryParameterValues(String requestUri) {
        int index = requestUri.indexOf("?");
        String queryString = requestUri.substring(index + 1);
        String[] queryParameters = queryString.split("&");
        String[] queryParameterValues = new String[queryParameters.length];
        for (int i = 0; i < queryParameters.length; i++) {
            queryParameterValues[i] = queryParameters[i].split("=")[1];
        }
        return queryParameterValues;
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "Content-Type: text/javascript;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    @Nullable
    private String getStaticResource(String requestUri) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestUri);
        if (url != null) {
            File file = new File(url.getFile());
            Path paths = file.toPath();
            return Files.readString(paths);
        }
        return null;
    }
}
