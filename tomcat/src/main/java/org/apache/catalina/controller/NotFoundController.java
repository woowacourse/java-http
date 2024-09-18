//package org.apache.catalina.controller;
//
//import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.HTML;
//import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.PLAIN;
//import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_LENGTH;
//import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_TYPE;
//import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.LOCATION;
//
//import java.net.URISyntaxException;
//import java.util.Map;
//import java.util.Optional;
//
//import org.apache.coyote.http11.FileFinder;
//import org.apache.coyote.http11.message.body.HttpBody;
//import org.apache.coyote.http11.message.header.HttpHeaders;
//import org.apache.coyote.http11.message.request.HttpRequest;
//import org.apache.coyote.http11.message.request.HttpRequestLine;
//import org.apache.coyote.http11.message.response.HttpResponse;
//import org.apache.coyote.http11.message.response.HttpStatus;
//import org.apache.coyote.http11.message.response.HttpStatusLine;
//
//public class NotFoundController implements Controller {
//
//    private static final String ERROR_MESSAGE = "파일을 찾는 과정에서 문제가 발생하였습니다.";
//
//    @Override
//    public void service(final HttpRequest request, final HttpResponse response) {
//        final HttpRequestLine requestLine = request.getRequestLine();
//        final FileFinder fileFinder = new FileFinder();
//        try {
//            final Optional<String> fileContent = fileFinder.readFileContent("/404.html");
//            fileContent.ifPresentOrElse(
//                    content -> setNotFoundResponse(requestLine, response, content),
//                    () -> setServerErrorResponse(requestLine, response)
//            );
//        } catch (URISyntaxException e) {
//            setServerErrorResponse(requestLine, response);
//        }
//    }
//
//    private void setNotFoundResponse(
//            final HttpRequestLine requestLine,
//            final HttpResponse response,
//            final String content
//    ) {
//        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.NOT_FOUND);
//        final HttpHeaders responseHeader = new HttpHeaders(Map.of(
//                CONTENT_TYPE.getValue(), HTML.getValue(),
//                CONTENT_LENGTH.getValue(), String.valueOf(content.length())
//        ));
//        final HttpBody httpBody = new HttpBody(content);
//        response.setStatusLine(httpStatusLine);
//        response.setHeader(responseHeader);
//        response.setHttpBody(httpBody);
//    }
//
//    private void setServerErrorResponse(final HttpRequestLine requestLine, final HttpResponse response) {
//        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(),
//                HttpStatus.INTERNAL_SERVER_ERROR);
//        final HttpHeaders responseHeader = new HttpHeaders(Map.of(
//                LOCATION.getValue(), ERROR_MESSAGE,
//                CONTENT_TYPE.getValue(), PLAIN.getValue(),
//                CONTENT_LENGTH.getValue(), String.valueOf(ERROR_MESSAGE.length())
//        ));
//        final HttpBody httpBody = new HttpBody(ERROR_MESSAGE);
//        response.setStatusLine(httpStatusLine);
//        response.setHeader(responseHeader);
//        response.setHttpBody(httpBody);
//    }
//}
