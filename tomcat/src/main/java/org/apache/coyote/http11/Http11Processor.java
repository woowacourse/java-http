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
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, String> mimeTypes = Map.of(
            "html", "text/html; charset=UTF-8",
            "css", "text/css; charset=UTF-8",
            "svg", "image/svg+xml",
            "js", "application/javascript; charset=UTF-8"
    );

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
            log.info(request.toString());

            if ("/login".equals(request.getPath())) {
                String account = request.getParams().get("account");
                String password = request.getParams().get("password");

                Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
                if (optionalUser.isEmpty()) {
                    sendError(out, HttpStatus.BAD_REQUEST);
                    return;
                }

                User user = optionalUser.get();
                if (!user.checkPassword(password)) {
                    sendError(out, HttpStatus.UNAUTHORIZED);
                    return;
                }

                log.info("Login success: {}", user);
                if (checkStaticFile("/index.html", out)) {
                    return;
                }
            }

            if (checkStaticFile(request.getPath(), out)) {
                return;
            }

            sendError(out, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            try (var out = new BufferedOutputStream(connection.getOutputStream())) {
                log.error(e.getMessage());
                sendError(out, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IOException ioException) {
                log.error(ioException.getMessage());
            }
        }
    }

    private boolean checkStaticFile(String path, BufferedOutputStream out) throws IOException {
        if (path.equals("/")) {
            path = "/index";
        }

        InputStream in = getClass().getClassLoader().getResourceAsStream("static" + path);
        if (in == null && !path.contains(".")) {
            in = getClass().getClassLoader().getResourceAsStream("static" + path + ".html");
        }

        if (in != null) {
            serveStaticFile(out, in, path);
            return true;
        }
        return false;
    }

    private void serveStaticFile(BufferedOutputStream out, InputStream inputStream, String path) throws IOException {
        try (inputStream) {
            String ext = "";
            if (path.contains(".")) {
                ext = path.substring(path.lastIndexOf(".") + 1);
            }

            String contentType = mimeTypes.get(ext);
            byte[] body = inputStream.readAllBytes();

            write(out, 200, "OK", contentType, body, true);
        }
    }

    private void write(BufferedOutputStream out, int status, String reason,
                       String contentType, byte[] body, boolean keepAlive) throws IOException {
        String head = "HTTP/1.1 " + status + " " + reason + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + (keepAlive ? "Connection: keep-alive\r\n" : "Connection: close\r\n")
                + "\r\n";
        out.write(head.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private void sendError(BufferedOutputStream out, HttpStatus httpStatus) {
        String errorResource = "static/" + httpStatus.getCode() + ".html";

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(errorResource)) {
            byte[] errorBody = getErrorBody(inputStream, httpStatus);

            String header = String.format("""
                            HTTP/1.1 %d %s\r
                            Content-Type: text/html; charset=UTF-8\r
                            Content-Length: %d\r
                            Connection: keep-alive\r
                            \r
                            """,
                    httpStatus.getCode(),
                    httpStatus.getMessage(),
                    errorBody.length
            );

            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.write(errorBody);
            out.flush();
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
