package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.util.List;

import org.apache.catalina.mvc.Controller;
import org.apache.catalina.parser.HttpRequestParser;
import org.apache.catalina.parser.RequestMapping;
import org.apache.catalina.reader.RequestReader;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestBody;
import org.apache.catalina.request.RequestHeader;
import org.apache.catalina.request.RequestLine;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;

public class RequestProcessor {

    private static final String QUERY_PARAMETER_SEPARATOR = "&";

    public HttpRequest readAndParseRequest(BufferedReader reader) {
        RequestReader requestReader = new RequestReader(reader);

        List<String> request = requestReader.readRequest();
        RequestLine requestLine = new RequestLine(request.get(0));
        RequestHeader requestHeader = new RequestHeader(HttpRequestParser.parseHeaders(request));
        String body = requestReader.readBody(requestHeader.getContentLength());

        List<String> params = List.of(body.split(QUERY_PARAMETER_SEPARATOR));
        RequestBody requestBody = new RequestBody(HttpRequestParser.parseParamValues(params));
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    public HttpResponse findHttpResponse(HttpRequest request) {
        HttpResponse response = HttpResponse.of(request);
        try {
            Controller controller = new RequestMapping().getController(request);
            controller.handleRequest(request, response);
        } catch (IllegalArgumentException e) {
            response.setError(HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            response.setError(HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            response.setError(HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
