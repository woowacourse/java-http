package nextstep.jwp.framework;

import nextstep.jwp.framework.message.MessageBody;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.request.RequestHeader;
import nextstep.jwp.framework.message.request.RequestLine;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import nextstep.jwp.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;


public class HttpTranslator {

    private static final String NEW_LINE = "\r\n";

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Charset charSet;

    public HttpTranslator(InputStream inputStream, OutputStream outputStream) {
        this(inputStream, outputStream, Charset.defaultCharset());
    }

    public HttpTranslator(InputStream inputStream, OutputStream outputStream, Charset charSet) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.charSet = charSet;
    }

    public HttpRequestMessage translate() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        RequestLine requestLine = extractRequestLine(bufferedReader);
        RequestHeader requestHeader = extractHeaderMessage(bufferedReader);
        int messageBodyLength = requestHeader.takeContentLength();
        MessageBody messageBody = extractBodyMessage(bufferedReader, messageBodyLength);
        return new HttpRequestMessage(requestLine, requestHeader, messageBody);
    }

    private RequestLine extractRequestLine(BufferedReader bufferedReader) throws IOException {
        return RequestLine.from(bufferedReader.readLine());
    }

    private RequestHeader extractHeaderMessage(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while (!(line = bufferedReader.readLine()).equals("")) {
            stringBuilder.append(line).append(NEW_LINE);
        }
        return new RequestHeader(
                StringUtils.decode(stringBuilder.toString(), charSet)
        );
    }

    private MessageBody extractBodyMessage(BufferedReader bufferedReader, int bodyLength) throws IOException {
        char[] buffer = new char[bodyLength];
        bufferedReader.read(buffer, 0, bodyLength);
        String bodyString = new String(buffer);
        return MessageBody.from(StringUtils.decode(bodyString, charSet));
    }


    public void respond(HttpResponseMessage httpResponseMessage) throws IOException {
        outputStream.write(httpResponseMessage.toBytes());
        outputStream.flush();
    }
}
