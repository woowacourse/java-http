package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
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
        try (var input = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedOutputStream(connection.getOutputStream())) {

            HttpRequest request = new HttpRequest(input);
            HttpResponse response = new HttpResponse(out);

            if ("/login".equals(request.getPath())) {
                String account = request.getParams().get("account");
                String password = request.getParams().get("password");

                Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
                if (optionalUser.isEmpty()) {
                    sendError(response, HttpStatus.BAD_REQUEST);
                    return;
                }

                User user = optionalUser.get();
                if (!user.checkPassword(password)) {
                    sendError(response, HttpStatus.UNAUTHORIZED);
                    return;
                }

                log.info("Login success: {}", user);
                if (checkStaticFile("/index.html", response)) {
                    return;
                }
            }

            if (checkStaticFile(request.getPath(), response)) {
                return;
            }

            sendError(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            try (HttpResponse response = new HttpResponse(new BufferedOutputStream(connection.getOutputStream()))) {
                log.error(e.getMessage());
                sendError(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
    }

    private boolean checkStaticFile(String path, HttpResponse response) throws IOException {
        if (path.equals("/")) {
            path = "/index";
        }

        InputStream in = getClass().getClassLoader().getResourceAsStream("static" + path);
        if (in == null && !path.contains(".")) {
            in = getClass().getClassLoader().getResourceAsStream("static" + path + ".html");
        }

        if (in != null) {
            serveStaticFile(response, in, path);
            return true;
        }
        return false;
    }

    private void serveStaticFile(HttpResponse response, InputStream inputStream, String path) throws IOException {
        try (inputStream) {
            String ext = "";
            if (path.contains(".")) {
                ext = path.substring(path.lastIndexOf(".") + 1);
            }

            String contentType = MimeTypeResolver.resolve(ext);
            byte[] body = inputStream.readAllBytes();

            response.send(HttpStatus.OK, contentType, body, true);
        }
    }

    private void sendError(HttpResponse response, HttpStatus httpStatus) {
        String errorResource = "static/" + httpStatus.getCode() + ".html";

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(errorResource)) {
            byte[] errorBody = getErrorBody(inputStream, httpStatus);
            response.sendError(httpStatus, errorBody);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private byte[] getErrorBody(InputStream inputStream, HttpStatus httpStatus) throws IOException {
        if (inputStream != null) {
            return inputStream.readAllBytes();
        }
        return ("<h1>" + httpStatus.getCode() + " " + httpStatus.getMessage() + "</h1>").getBytes(
                StandardCharsets.UTF_8);
    }
}
