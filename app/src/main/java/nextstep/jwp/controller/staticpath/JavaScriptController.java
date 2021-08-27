package nextstep.jwp.controller.staticpath;

import nextstep.jwp.exception.StaticFileNotFoundException;
import nextstep.jwp.http.HttpStatusCode;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.util.function.Supplier;

public class JavaScriptController extends StaticResourceController {

    @Override
    protected Supplier<? extends RuntimeException> supplyExceptionHandler(String filePath) {
        return () -> new StaticFileNotFoundException(String.format("해당 파일을 찾을 수 없습니다.(%s)", filePath));
    }

    @Override
    protected void manageResponse(HttpResponseMessage httpResponseMessage, byte[] bytesAsFile) {
        MessageBody messageBody = new MessageBody(bytesAsFile);
        httpResponseMessage.setMessageBody(messageBody);

        httpResponseMessage.setStatusCode(HttpStatusCode.OK);
        httpResponseMessage.putHeader("Content-Type", "text/javascript");
        httpResponseMessage.putHeader("Content-Length", messageBody.contentLength());
    }
}
