package nextstep.jwp.http;

import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.request.RequestHeader;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.http.utils.HttpParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequestMessage httpRequestMessage = makeHttpRequestMessage(bufferedReader);

            log.debug(httpRequestMessage.getHeader().requestUri());

            HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(httpRequestMessage);
            HttpResponseMessage httpResponseMessage = httpResponseBuilder.build();
            outputStream.write(httpResponseMessage.toBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception e) {
            log.error("Exception", e.getMessage());
        } finally {
            close();
        }
    }

    private HttpRequestMessage makeHttpRequestMessage(BufferedReader bufferedReader) throws IOException {
        RequestHeader requestHeader = HttpParseUtils.extractHeaderMessage(bufferedReader);
        int messageBodyLength = requestHeader.takeMessageBodyLength();
        MessageBody messageBody = HttpParseUtils.extractBodyMessage(bufferedReader, messageBodyLength);
        return new HttpRequestMessage(requestHeader, messageBody);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
