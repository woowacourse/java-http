package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Status;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(reader);

            String uri = request.getUri();
            HttpResponse response = new HttpResponse();

//            if ("/index.html".equals(uri)) {
            response.setStatusLine(Status.OK);
            response.setBodyByUri(uri);
//            }
            if ("/login".equals(uri)) {
                Map<String, String> params = request.getParams();
                Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
                if (user.isPresent() && user.get().checkPassword(params.get("password"))) {

                }
            }

//            final String responseBody = "";
//
//            final String response = String.join("\r\n",
//                    "HTTP/1.1 200 OK ",
//                    "Content-Type: text/html;charset=utf-8 ",
//                    "Content-Length: " + responseBody.getBytes().length + " ",
//                    "",
//                    responseBody);


            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
