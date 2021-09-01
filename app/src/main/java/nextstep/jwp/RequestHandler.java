package nextstep.jwp;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import nextstep.jwp.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    public static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream();
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {

            HttpRequest httpRequest = new HttpRequest(bufferedReader);

            String response = null;

            if (httpRequest.isGet()) {
                response = GetRequestUri.createResponse(httpRequest.getPath());
            } else if (httpRequest.isPost()) {
                String requestBody = readRequestBody(bufferedReader, httpRequest);
                response = PostRequestUri.createResponse(httpRequest.getPath(), requestBody);
            }
            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage());
        } finally {
            close();
        }
    }

    private String readRequestBody(BufferedReader bufferedReader, HttpRequest request) throws IOException {
        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
