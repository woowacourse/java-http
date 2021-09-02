package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.BadRequestMessageException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UsernameConflictException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream))) {

            Response response = messageConvert(bufferedReader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Response messageConvert(BufferedReader bufferedReader) throws IOException {
        try {
            final Request request = createdRequest(bufferedReader);
            Response response = new Response();
            Controller controller = REQUEST_MAPPING.getController(request);
            controller.service(request, response);
            return response;
        } catch (BadRequestMessageException exception) {
            return Response.createErrorRequest(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException exception) {
            return Response.create302Found("/404.html");
        } catch (UsernameConflictException exception) {
            return Response.createErrorRequest(exception.getMessage(), HttpStatus.CONFLICT);
        } catch (UnauthorizedException exception) {
            return Response.create302Found("/401.html");
        } catch (Exception exception) {
            log.error("알수없는 에러가 발생 : {}", exception.getMessage());
            return Response.create302Found("500.html");
        }
    }

    private Request createdRequest(BufferedReader bufferedReader) {
        try {
            String line = bufferedReader.readLine();

            String[] requestUri = line.split(" ");
            HttpMethod httpMethod = HttpMethod.valueOf(requestUri[0]);
            String uri = requestUri[1];
            String httpVersion = requestUri[2];
            Map<String, String> header = getHeader(bufferedReader);
            Map<String, String> body = getBody(bufferedReader, header, httpMethod);

            return new Request.Builder()
                .method(httpMethod)
                .uri(uri)
                .httpVersion(httpVersion)
                .header(header)
                .body(body)
                .build();
        } catch (Exception e) {
            log.error("Request Error : {}", e.getMessage());
            throw new BadRequestMessageException();
        }
    }

    private Map<String, String> getHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();

        String line = bufferedReader.readLine();
        while (!"".equals(line) && !Objects.isNull(line)) {
            int index = line.indexOf(":");
            String key = line.substring(0, index);
            String value = line.substring(index + 2);
            header.put(key, value);
            line = bufferedReader.readLine();
        }
        return header;
    }

    private Map<String, String> getBody(BufferedReader bufferedReader, Map<String, String> header,
        HttpMethod httpMethod) throws IOException {

        Map<String, String> requestBody = new HashMap<>();

        if (httpMethod.isBody() && header.containsKey(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(header.get(CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            int read = bufferedReader.read(buffer, 0, contentLength);
            if (read == -1) {
                return requestBody;
            }
            String line = new String(buffer);
            String[] bodyDates = line.split("&");
            for (String body : bodyDates) {
                String[] keyValue = body.split("=");
                requestBody.put(keyValue[0], keyValue[1]);
            }
        }
        return requestBody;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
