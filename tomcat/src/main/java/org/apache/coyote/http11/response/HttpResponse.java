package org.apache.coyote.http11.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.http11.response.header.HttpContentType.TEXT_HTML;
import static org.apache.coyote.http11.response.header.HttpContentType.mimeTypeWithCharset;
import static org.apache.coyote.http11.response.header.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.header.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.response.header.HttpHeader.LOCATION;
import static org.apache.coyote.http11.response.line.ResponseStatus.BAD_REQUEST;

import java.util.LinkedHashMap;
import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.HttpHeader;
import org.apache.coyote.http11.response.header.ResponseHeader;
import org.apache.coyote.http11.response.line.ResponseLine;
import org.apache.coyote.http11.response.line.ResponseStatus;

public class HttpResponse {

    private static final String DELIMITER = " " + System.lineSeparator();

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public HttpResponse(String httpVersion) {
        this.responseLine = new ResponseLine(httpVersion, BAD_REQUEST);
        this.responseHeader = new ResponseHeader(new LinkedHashMap<>());
    }

    public String responseMessage() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(responseLine.responseLineMessage()).append(DELIMITER);

        for (String headerMessage : responseHeader.headerMessages()) {
            messageBuilder.append(headerMessage).append(DELIMITER);
        }

        if (responseBody != null) {
            messageBuilder.append(System.lineSeparator()).append(responseBody.bodyMessage());
        }

        return messageBuilder.toString();
    }

    public void setResponseMessage(ResponseStatus responseStatus, String bodyMessage) {
        responseLine.setResponseStatus(responseStatus);
        setBodyAndHeaderByMessage(bodyMessage);
    }

    private void setBodyAndHeaderByMessage(String message) {
        responseBody = new ResponseBody(message);
        responseHeader.put(CONTENT_TYPE, TEXT_HTML.mimeTypeWithCharset(UTF_8));
        responseHeader.put(CONTENT_LENGTH, String.valueOf(responseBody.measureContentLength()));
    }

    public void setResponseResource(ResponseStatus responseStatus, String resourceUri) {
        responseLine.setResponseStatus(responseStatus);
        setBodyAndHeaderByResource(resourceUri);
    }

    private void setBodyAndHeaderByResource(String uri) {
        FileManager fileManager = FileManager.from(uri);
        String fileContent = fileManager.readFileContent();
        String fileExtension = fileManager.extractFileExtension();

        responseBody = new ResponseBody(fileContent);
        responseHeader.put(CONTENT_TYPE, mimeTypeWithCharset(fileExtension, UTF_8));
        responseHeader.put(CONTENT_LENGTH, String.valueOf(responseBody.measureContentLength()));
    }

    public void setResponseRedirect(ResponseStatus responseStatus, String redirectUri) {
        responseLine.setResponseStatus(responseStatus);
        responseHeader.put(LOCATION, redirectUri);
    }

    public void setResponseHeader(HttpHeader field, String value) {
        responseHeader.put(field, value);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = new ResponseBody(responseBody);
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        responseLine.setResponseStatus(responseStatus);
    }
}
