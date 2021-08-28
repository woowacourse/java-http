package nextstep.jwp;

import nextstep.jwp.core.handler.FrontHandler;
import nextstep.jwp.request.basic.DefaultHttpRequest;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.DefaultHttpResponse;
import nextstep.jwp.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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
            HttpRequest httpRequest = new DefaultHttpRequest(inputStream);
            HttpResponse httpResponse = new DefaultHttpResponse();

            final String response = new FrontHandler("nextstep")
                    .getResponse(httpRequest, httpResponse).totalResponse();


//            final FrontHandler frontHandler =
//                    applicationContext.getBean("FrontHandler", FrontHandler.class);
//
//            String response = frontHandler.doRequest(httpRequest, httpResponse);

//            final String responseBody = "Hello world!";
//            final String response = String.join("\r\n",
//                    "HTTP/1.1 200 OK ",
//                    "Content-Type: text/html;charset=utf-8 ",
//                    "Content-Length: " + responseBody.getBytes().length + " ",
//                    "",
//                    responseBody);

            outputStream.write(response.getBytes());
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
