package nextstep.jwp;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

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

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();) {

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder stringBuilder = new StringBuilder();

            RequestLine requestLine = new RequestLine(bufferedReader.readLine());

            String line;
            do {
                line = bufferedReader.readLine();
                LOG.info("headers : {}", line);
                stringBuilder.append(line).append("\r\n");
            } while (!"".equals(line));


            String response = null;

            if (requestLine.isGet()) {
                response = GetRequestUri.createResponse(requestLine.getPath());
            } else if (requestLine.isPost()) {
                String requestBody = readRequestBody(bufferedReader, stringBuilder);
                response = PostRequestUri.createResponse(requestLine.getPath(), requestBody);
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

    private String readRequestBody(BufferedReader bufferedReader, StringBuilder stringBuilder) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders(stringBuilder.toString());
        int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
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
