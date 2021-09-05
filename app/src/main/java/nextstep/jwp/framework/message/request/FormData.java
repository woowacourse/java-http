package nextstep.jwp.framework.message.request;

import nextstep.jwp.framework.message.MessageBody;
import nextstep.jwp.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class FormData {

    private static final String FORM_DATA_PIECE_SEPARATOR = "&";
    private static final String FORM_DATE_PARAM_SEPARATOR = "=";

    private static final FormData EMPTY_FORM_DATA = new FormData(Collections.emptyMap());

    private final Map<String, String> params;

    private FormData(Map<String, String> params) {
        this.params = params;
    }

    public static FormData empty() {
        return EMPTY_FORM_DATA;
    }

    public static FormData from(Map<String, String> params) {
        if (params.isEmpty()) {
            return empty();
        }
        return new FormData(params);
    }

    public static FormData from(MessageBody messageBody) {
        if (messageBody.isEmpty()) {
            return empty();
        }
        return from(extractFormData(messageBody));
    }

    private static Map<String, String> extractFormData(MessageBody messageBody) {
        String formData = messageBody.asString();
        return StringUtils.extractMap(formData, FORM_DATA_PIECE_SEPARATOR, FORM_DATE_PARAM_SEPARATOR);
    }

    public String take(String key) {
        return params.get(key);
    }

    public Map<String, String> toMap() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormData formData1 = (FormData) o;
        return Objects.equals(params, formData1.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }
}
