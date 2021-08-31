package nextstep.jwp.framework.http.response;

import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.response.details.ResponseBody;
import nextstep.jwp.framework.http.response.details.ResponseHttpHeader;
import nextstep.jwp.framework.http.response.details.ResponseStatus;
import nextstep.jwp.framework.http.response.util.FileUtil;

import java.io.File;

import static nextstep.jwp.framework.http.common.Constants.LINE_SEPARATOR;
import static nextstep.jwp.framework.http.common.Constants.NEWLINE;

public class HttpResponse {

    private final ProtocolVersion protocolVersion;
    private final ResponseStatus responseStatus;
    private final ResponseHttpHeader responseHttpHeader;
    private final ResponseBody responseBody;

    public HttpResponse(final ProtocolVersion protocolVersion, final ResponseStatus responseStatus,
                        final ResponseHttpHeader responseHttpHeader, final ResponseBody responseBody) {
        this.protocolVersion = protocolVersion;
        this.responseStatus = responseStatus;
        this.responseHttpHeader = responseHttpHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final File file, final ResponseStatus responseStatus) {
        final String content = FileUtil.read(file);
        final String extension = FileUtil.getExtension(file);
        final ResponseHttpHeader responseHttpHeader = new ResponseHttpHeader();
        responseHttpHeader.appendResponseBodyInfo(content, extension);

        return new HttpResponse(
                ProtocolVersion.defaultVersion(),
                responseStatus,
                responseHttpHeader,
                ResponseBody.of(content));
    }

    public void appendRedirectInfo(final String location) {
        responseHttpHeader.appendRedirectInfo(location);
    }

    public String generateResponse() {
        final String responseLine = protocolVersion.getProtocol() + LINE_SEPARATOR +
                responseStatus.getStatusCode() + LINE_SEPARATOR + responseStatus.getStatusMessage();

        return String.join(NEWLINE,
                responseLine,
                responseHttpHeader.generateResponse(),
                responseBody.getContent());
    }
}
