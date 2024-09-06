package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.handler.GetLoginHandler;
import com.techcourse.handler.GetRegisterHandler;
import com.techcourse.handler.HelloHandler;
import com.techcourse.handler.NotFoundHandler;
import com.techcourse.handler.PostLoginHandler;
import com.techcourse.handler.PostRegisterHandler;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager;

    public Http11Processor(Socket connection, Manager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = createHttpRequest(inputStream);
            HttpResponse httpResponse = respondResource(httpRequest);
            outputStream.write(httpResponse.serialize());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = bufferedReader.readLine();
        Header header = createHeader(bufferedReader);
        QueryParameter queryParameter = createQueryParameter(bufferedReader, header);

        return new HttpRequest(requestLine, header, queryParameter);
    }

    private Header createHeader(BufferedReader bufferedReader) throws IOException {
        List<String> headerTokens = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!line.isBlank()) {
            line = bufferedReader.readLine();
            headerTokens.add(line);
        }

        return new Header(headerTokens);
    }

    private QueryParameter createQueryParameter(BufferedReader bufferedReader, Header header) throws IOException {
        int length = Integer.parseInt(header.get(HttpHeaderKey.CONTENT_LENGTH.getName()).orElse("0"));
        char[] body = new char[length];
        bufferedReader.read(body);
        String bodyString = new String(body);

        return new QueryParameter(bodyString);
    }

    private HttpResponse respondResource(HttpRequest httpRequest) throws IOException {
        AbstractHandler helloHandler = new HelloHandler();
        AbstractHandler staticResourceHandler = new StaticResourceHandler();
        AbstractHandler postLoginHandler = new PostLoginHandler();
        AbstractHandler postRegisterHandler = new PostRegisterHandler();
        AbstractHandler getLoginHandler = new GetLoginHandler();
        AbstractHandler getRegisterHandler = new GetRegisterHandler();
        AbstractHandler notFoundHandler = new NotFoundHandler();

        List<AbstractHandler> handlers = List.of(
                helloHandler,
                staticResourceHandler,
                postLoginHandler,
                postRegisterHandler,
                getLoginHandler,
                getRegisterHandler
        );
        AbstractHandler targetHandler = handlers.stream()
                .filter(it -> it.canHandle(httpRequest))
                .findFirst()
                .orElse(notFoundHandler);

        return targetHandler.handle(httpRequest, sessionManager);
    }
}
