package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class RequestHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final StringBuilder header = new StringBuilder();

            String line = bufferedReader.readLine();

            if (line == null) {
                return;
            }
            final String[] request = line.split(" ");
            final String uri = request[1];
            String fileName = uri;
            String queryString = null;

            if (uri.startsWith("/login")) {
                int index = uri.indexOf("?");
                if (index == -1) {
                    fileName = "/login.html";
                } else {
                    fileName = uri.substring(0, index) + ".html";
                    queryString = uri.substring(index + 1);

                    int andIndex = queryString.indexOf("&");
                    String account = getDataFromQueryString(queryString.substring(0, andIndex));
                    String password = getDataFromQueryString(queryString.substring(andIndex));

                    User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
                    user.checkPassword(password);
                }
            }

            while (!"".equals(line)) {
                line = bufferedReader.readLine();
                header.append(line);
                header.append("\r\n");
            }

            byte[] body = new byte[0];
            body = Files.readAllBytes(getResources(fileName).toPath());

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.length + " ",
                    "",
                    new String(body));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }
    private String getDataFromQueryString(String data) {
        return data.substring(data.indexOf("=") + 1);
    }

    private File getResources(String fileName) {
        final URL resource = getClass().getClassLoader().getResource("static" + fileName);
        if (resource != null) {
            return new File(resource.getPath());
        }
        return getResources("/404.html");
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
