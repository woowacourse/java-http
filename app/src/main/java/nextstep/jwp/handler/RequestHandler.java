package nextstep.jwp.handler;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Objects;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.handler.DefaultFileNotFoundException;
import nextstep.jwp.exception.handler.HttpMessageException;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
        this.requestMapping = new RequestMapping();
    }

    @Override
    public void run() {
        log.debug("Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final String response = handleRequest(reader);

            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(response);
            bufferedWriter.flush();
        } catch (DefaultFileNotFoundException e) {
            log.error("[ERROR]", e);
        } catch (IOException e) {
            log.error("[ERROR] Exception In Stream", e);
        } catch (Exception e) {
            log.error("[ERROR] Unknown Exception", e);
        } finally {
            close();
        }
    }

    public String handleRequest(BufferedReader reader) {
        try {
            HttpRequest httpRequest = HttpRequest.from(reader);
            HttpResponse httpResponse = new HttpResponse(httpRequest.getHttpVersion());

            if (httpRequest.isRequestStaticFile()) {
                httpResponse.ok(httpRequest.getRequestUrl());
                return httpResponse.makeHttpMessage();
            }

            Controller controller = requestMapping.findController(httpRequest);
            controller.handle(httpRequest, httpResponse);
            return httpResponse.makeHttpMessage();

        } catch (HttpMessageException e) {
            log.error("[ERROR]", e);
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
            httpResponse.badRequest("/400.html");
            return httpResponse.makeHttpMessage();

        } catch (FileNotFoundException | URISyntaxException e) {
            log.error("[ERROR]", e);
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
            httpResponse.notFound("/404.html");
            return httpResponse.makeHttpMessage();
            
        } catch (IOException e) {
            log.error("[ERROR] Exception reading socket", e);
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
            httpResponse.internalServerError("/500.html");
            return httpResponse.makeHttpMessage();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException e) {
            log.error("Exception closing socket", e);
        }
    }
}
