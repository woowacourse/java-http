package nextstep.jwp.http.utils;

import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.request.RequestHeader;
import nextstep.jwp.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpParseUtils {

    private static final String FORM_DATA_PIECE_SEPARATOR = "&";
    private static final String FORM_DATE_PARAM_SEPARATOR = "=";

    public static Map<String, String> extractFormData(MessageBody messageBody) {
        String formData = messageBody.asString();
        String[] pieces = formData.split(FORM_DATA_PIECE_SEPARATOR);
        return Arrays.stream(pieces)
                .map(HttpParseUtils::extractParam)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, String> extractParam(String formDataPiece) {
        int index = formDataPiece.indexOf(FORM_DATE_PARAM_SEPARATOR);
        String key = formDataPiece.substring(0, index);
        String value = formDataPiece.substring(index + 1);
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public static RequestHeader extractHeaderMessage(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while (!(line = bufferedReader.readLine()).equals("")) {
            stringBuilder.append(line).append("\r\n");
        }
        return RequestHeader.from(
                StringUtils.decode(stringBuilder.toString())
        );
    }

    public static MessageBody extractBodyMessage(BufferedReader bufferedReader, int BodyLength) throws IOException {
        char[] buffer = new char[BodyLength];
        bufferedReader.read(buffer, 0, BodyLength);
        String bodyString = new String(buffer);
        return new MessageBody(StringUtils.decode(bodyString));
    }
}
