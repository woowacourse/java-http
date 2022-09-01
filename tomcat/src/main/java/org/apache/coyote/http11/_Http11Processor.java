//package org.apache.coyote.http11;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.Socket;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import nextstep.jwp.exception.UncheckedServletException;
//import org.apache.coyote.Processor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class Http11Processor implements Runnable, Processor {
//
//    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
//
//    private final Socket connection;
//
//    public Http11Processor(final Socket connection) {
//        this.connection = connection;
//    }
//
//    @Override
//    public void run() {
//        process(connection);
//    }
//
//    @Override
//    public void process(final Socket connection) {
//        try (final var inputStream = connection.getInputStream();
//             final var outputStream = connection.getOutputStream()) {
//
//            final String responseBody = convertStaticResourceToString(inputStream);
//
//            final var response = String.join("\r\n",
//                    "HTTP/1.1 200 OK ",
//                    "Content-Type: text/html;charset=utf-8 ",
//                    "Content-Length: " + responseBody.getBytes().length + " ",
//                    "",
//                    responseBody);
//
//            log.info("HTTP Response Message\n" + response.split("\r\n")[0]);
//
//            outputStream.write(response.getBytes());
//            outputStream.flush();
//        } catch (IOException | UncheckedServletException e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    private String convertStaticResourceToString(final InputStream inputStream) throws IOException {
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        String headerFirstLine = bufferedReader.readLine();
//        String staticResourceLocation = headerFirstLine.split(" ")[1];
//
//        log.info("HTTP Request Message\n" + headerFirstLine);
//
//        if (staticResourceLocation.equals("/")) {
//            return "Hello world!";
//        }
//        return Files.readString(Paths.get("./tomcat/src/main/resources/static" + staticResourceLocation));
//    }
//}
