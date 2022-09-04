package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";
    private static final int METHOD_SEQUENCE = 0;
    private static final int URL_SEQUENCE = 1;
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Socket connection;
    private final ResponseAssembler responseAssembler;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        responseAssembler = new ResponseAssembler();
    }

    @Override
    public void run() {
        process(connection);
    }

    public void get(Http11Request request, OutputStream outputStream) throws IOException {
        Http11Response response = getMapping(request);

        outputStream.write(response.toMessage().getBytes());
        outputStream.flush();
    }

    private Http11Response getMapping(Http11Request request) {
        if (request.isResource()) {
            return responseAssembler.resourceResponse(request.getUrl(), HttpStatus.OK);
        }
        if (request.getUrl().equals("/login")) {
            return generateLoginPage(request);
        }
        return generateDefaultResponse();
    }

    private Http11Response generateLoginPage(Http11Request request) {
        Map<String, String> queries = request.getQueryString();
        if (queries.get("account") != null && queries.get("password") != null) {
            return checkUserInformation(queries.get("account"), queries.get("password"));
        }

        return responseAssembler.resourceResponse("/login.html", HttpStatus.OK);
    }

    private Http11Response checkUserInformation(String account, String password) {
        Optional<User> userByAccount = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (userByAccount.isPresent()) {
            log.info("user : " + userByAccount.get());
            return responseAssembler.resourceResponse("/index.html", HttpStatus.REDIRECT);
        }
        return responseAssembler.resourceResponse("/401.html", HttpStatus.UNAUTHORIZED);
    }

    private Http11Response generateDefaultResponse() {
        final var responseBody = "Hello world!";
        return responseAssembler.rawStringResponse(responseBody);
    }

    @Override
    public void process(final Socket connection) {

        try (
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                OutputStream outputStream = connection.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            Http11Request request = makeRequest(bufferedReader);
            get(request, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Http11Request makeRequest(BufferedReader bufferedReader) throws IOException {
        String[] rawStart = bufferedReader.readLine()
                .split(" ");
        String method = rawStart[METHOD_SEQUENCE];
        String url = rawStart[URL_SEQUENCE];

        Map<String, String> headers = parseHeaders(bufferedReader);
        String body = parseBody(bufferedReader);

        return new Http11Request(method, url, headers, body);
    }

    private String parseBody(BufferedReader bufferedReader) throws IOException {
        StringBuilder bodyBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            String data = bufferedReader.readLine();
            if (data == null) {
                break;
            }
            bodyBuilder.append(data);
            bodyBuilder.append(CRLF);
        }

        String body = bodyBuilder.toString();
        return body;
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String data = bufferedReader.readLine();
            if (data == null || data.equals("")) {
                break;
            }
            String[] header = data.split(" ");
            headers.put(header[HEADER_NAME_INDEX].strip(), header[HEADER_VALUE_INDEX].strip());
        }
        return headers;
    }
}
