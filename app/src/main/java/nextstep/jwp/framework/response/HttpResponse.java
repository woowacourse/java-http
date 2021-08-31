package nextstep.jwp.framework.response;

import nextstep.jwp.framework.request.details.ProtocolVersion;
import nextstep.jwp.framework.response.details.ResponseBody;
import nextstep.jwp.framework.response.details.ResponseHttpHeader;
import nextstep.jwp.framework.response.details.Status;
import nextstep.jwp.framework.response.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class HttpResponse {

    private static final String NEWLINE = "\r\n";
    public static final String RESPONSE_LINE_SEPARATOR = " ";

    private ProtocolVersion protocolVersion;
    private Status status;
    private ResponseHttpHeader responseHttpHeader;
    private ResponseBody responseBody;

    public HttpResponse(ProtocolVersion protocolVersion, Status status, ResponseHttpHeader responseHttpHeader, ResponseBody responseBody) {
        this.protocolVersion = protocolVersion;
        this.status = status;
        this.responseHttpHeader = responseHttpHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final File file, final Status status) {
        final String content = FileUtil.read(file);
        final String extension = FileUtil.getExtension(file);
        final ResponseHttpHeader responseHttpHeader = new ResponseHttpHeader();
        responseHttpHeader.appendResponseBodyInfo(content, extension);

        return new HttpResponse(
                ProtocolVersion.defaultVersion(),
                status,
                responseHttpHeader,
                ResponseBody.of(content));
    }

    public String generateResponse() {
        final String responseLine = protocolVersion.getProtocol() + RESPONSE_LINE_SEPARATOR +
                status.getStatusCode() + RESPONSE_LINE_SEPARATOR + status.getStatusMessage();

        return String.join(NEWLINE,
                responseLine,
                responseHttpHeader.generateResponse(),
                responseBody.getContent());
    }
}
