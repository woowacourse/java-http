package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.BadRequestMessageException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UsernameConflictException;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.utils.HttpRequestConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream))) {

            Response response = messageConvert(bufferedReader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Response messageConvert(BufferedReader bufferedReader) throws IOException {
        try {
            final Request request = HttpRequestConverter.createdRequest(bufferedReader);
            Response response = new Response();
            Controller controller = REQUEST_MAPPING.getController(request);
            controller.service(request, response);
            return response;
        } catch (BadRequestMessageException exception) {
            return Response.createErrorRequest(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException | UnauthorizedException exception) {
            return Response.create302Found(exception.getMessage());
        } catch (UsernameConflictException exception) {
            return Response.createErrorRequest(exception.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception exception) {
            LOG.error("알수없는 에러가 발생 : {}", exception.getMessage());
            return Response.create302Found("500.html");
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
