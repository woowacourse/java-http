package nextstep.jwp.framework.message.request;

import nextstep.jwp.framework.message.MessageBody;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FormData {

    private static final String FORM_DATA_PIECE_SEPARATOR = "&";
    private static final String FORM_DATE_PARAM_SEPARATOR = "=";

    private final Map<String, String> fields;

    public FormData(Map<String, String> formData) {
        this.fields = formData;
    }

    public static FormData from(MessageBody messageBody) {
        return new FormData(extractFormData(messageBody));
    }

    private static Map<String, String> extractFormData(MessageBody messageBody) {
        String formData = messageBody.asString();
        String[] pieces = formData.split(FORM_DATA_PIECE_SEPARATOR);
        return Arrays.stream(pieces)
                .map(FormData::extractParam)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, String> extractParam(String formDataPiece) {
        int index = formDataPiece.indexOf(FORM_DATE_PARAM_SEPARATOR);
        String key = formDataPiece.substring(0, index);
        String value = formDataPiece.substring(index + 1);
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public String take(String key) {
        return fields.get(key);
    }

    public Map<String, String> toMap() {
        return Collections.unmodifiableMap(fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormData formData1 = (FormData) o;
        return Objects.equals(fields, formData1.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }
}
