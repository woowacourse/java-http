package nextstep.jwp.http;

import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.request.RequestHeader;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTranslator {

    private static final String FORM_DATA_PIECE_SEPARATOR = "&";
    private static final String FORM_DATE_PARAM_SEPARATOR = "=";
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
        RequestHeader requestHeader = extractHeaderMessage(bufferedReader);
        int messageBodyLength = requestHeader.takeMessageBodyLength();
        MessageBody messageBody = extractBodyMessage(bufferedReader, messageBodyLength);
        return new HttpRequestMessage(requestHeader, messageBody);
    }

    private RequestHeader extractHeaderMessage(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while (!(line = bufferedReader.readLine()).equals("")) {
            stringBuilder.append(line).append(NEW_LINE);
        }
        return RequestHeader.from(
                StringUtils.decode(stringBuilder.toString(), charSet)
        );
    }

    private MessageBody extractBodyMessage(BufferedReader bufferedReader, int BodyLength) throws IOException {
        char[] buffer = new char[BodyLength];
        bufferedReader.read(buffer, 0, BodyLength);
        String bodyString = new String(buffer);
        return new MessageBody(StringUtils.decode(bodyString, charSet));
    }


    public void respond(HttpResponseMessage httpResponseMessage) throws IOException {
        outputStream.write(httpResponseMessage.toBytes());
        outputStream.flush();
    }

    // TODO : 추후에 분리
    public static Map<String, String> extractFormData(MessageBody messageBody) {
        String formData = messageBody.asString();
        String[] pieces = formData.split(FORM_DATA_PIECE_SEPARATOR);
        return Arrays.stream(pieces)
                .map(HttpTranslator::extractParam)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, String> extractParam(String formDataPiece) {
        int index = formDataPiece.indexOf(FORM_DATE_PARAM_SEPARATOR);
        String key = formDataPiece.substring(0, index);
        String value = formDataPiece.substring(index + 1);
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}
