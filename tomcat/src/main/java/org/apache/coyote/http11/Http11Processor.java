package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);

            String line = br.readLine();
            String mimeType = "";
            String responseBody = "";
            if (line == null) {
                return;
            }

            String[] lines = line.split(" ");
            if (lines[1].equals("/")) {
                mimeType = "text/html";
                responseBody = "Hello world!";
            } else if (lines[1].startsWith("/css")) {
                mimeType = "text/css";

                URL resource = getClass().getClassLoader().getResource("static" + lines[1]);
                File file = new File(resource.getFile());
                Path path = file.toPath();
                responseBody = new String(Files.readAllBytes(path));
            } else if (lines[1].startsWith("/login?")) {
                int index = lines[1].indexOf("?");
                String loginPath = lines[1].substring(0, index);
                String queryString = lines[1].substring(index + 1);
                String account = queryString.split("&")[0].split("=")[1];
                String password = queryString.split("&")[1].split("=")[1];

                User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
                if (user.checkPassword(password)) {
                    log.info(user.toString());
                }

                URL resource = getClass().getClassLoader().getResource("static" + loginPath + ".html");
                File file = new File(resource.getFile());
                Path path = file.toPath();
                responseBody = new String(Files.readAllBytes(path));
            } else {
                mimeType = "text/html";

                URL resource = getClass().getClassLoader().getResource("static" + lines[1]);
                File file = new File(resource.getFile());
                Path path = file.toPath();
                responseBody = new String(Files.readAllBytes(path));
            }

            while (!"".equals(line)) {
                line = br.readLine();
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + mimeType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
