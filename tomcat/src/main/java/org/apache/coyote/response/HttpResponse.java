package org.apache.coyote.response;

import static common.ResponseStatus.INTERNAL_SERVER_ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.response.header.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.response.header.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.response.header.HttpHeader.LOCATION;

import common.ResponseStatus;
import java.util.LinkedHashMap;
import org.apache.coyote.response.body.ResponseBody;
import org.apache.coyote.response.header.HttpContentType;
import org.apache.coyote.response.header.HttpHeader;
import org.apache.coyote.response.header.ResponseHeader;
import org.apache.coyote.response.line.ResponseLine;

public class HttpResponse {

    private static final String DELIMITER = " " + System.lineSeparator();

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public HttpResponse(String httpVersion) {
        this.responseLine = new ResponseLine(httpVersion, INTERNAL_SERVER_ERROR);
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
        responseHeader.put(CONTENT_TYPE, HttpContentType.TEXT_HTML.mimeTypeWithCharset(UTF_8));
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
        responseHeader.put(CONTENT_TYPE, HttpContentType.mimeTypeWithCharset(fileExtension, UTF_8));
        responseHeader.put(CONTENT_LENGTH, String.valueOf(responseBody.measureContentLength()));
    }

    public void setResponseRedirect(ResponseStatus responseStatus, String redirectUri) {
        responseLine.setResponseStatus(responseStatus);
        responseHeader.put(LOCATION, redirectUri);
    }

    public void setResponseHeader(HttpHeader field, String value) {
        responseHeader.put(field, value);
    }
}
