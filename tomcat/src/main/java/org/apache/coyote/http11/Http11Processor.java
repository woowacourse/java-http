package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.ContentType;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.StatusCode;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            log.info("path: {}", httpRequest.getPath());
            log.info("{}", httpRequest.getHeader());
            final HttpResponse httpResponse = new HttpResponse();

            RequestMapping.handle(httpRequest, httpResponse);
            log.info("after: {}", httpResponse);

//            int index = requestLine.indexOf("?");
//            final var path = getPath(requestLine);
//
//            if (path.equals("/login")) {
//                final var queryString = requestLine.substring(index + 1).split("&");
//                final var account = queryString[0].substring(queryString[0].lastIndexOf("=") + 1);
//                final var password = queryString[1].substring(queryString[1].lastIndexOf("=") + 1);
//
//                final User user = InMemoryUserRepository.findByAccount(account)
//                        .orElseThrow();
//
//                if (user.checkPassword(password)) {
//                    log.info("{}", user);
//                    statusCode = StatusCode.FOUND;
//                }
//                else {
//                    statusCode = StatusCode.UNAUTHORIZED;
//                }
//            }
//            final var contentType = parseContentType(requestLine.getPath());
//            final var responseBody = createResponseBody(requestLine.getPath());
//
//            final var response = createResponse(statusCode, contentType, responseBody);

            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getPath(final String uri) {
        final int index = uri.indexOf("?");

        if (index != -1) {
            return uri.substring(0, index);
        }
        return uri;
    }

    private ContentType parseContentType(final String uri) {
        final var extension = uri.substring(uri.lastIndexOf(".") + 1);
        return ContentType.from(extension);
    }

//    private String createResponseBody(String uri) throws IOException {
//        if (uri.equals("/")) {
//            return "Hello world!";
//        } else if (!uri.contains(".")) {
//            uri = uri + ".html";
//        }
//
//        URL resource = getClass().getClassLoader().getResource("static/" + uri);
//        return new String(Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));
//    }

    private String createResponse(final StatusCode statusCode, final ContentType contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getReasonPhrase() + " ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
