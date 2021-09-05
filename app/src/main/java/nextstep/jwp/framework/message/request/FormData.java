package nextstep.jwp.framework.message.request;

import nextstep.jwp.framework.message.MessageBody;
import nextstep.jwp.utils.StringUtils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FormData {

    private static final String FORM_DATA_PIECE_SEPARATOR = "&";
    private static final String FORM_DATE_PARAM_SEPARATOR = "=";

    private final Map<String, String> params;

    private FormData() {
        this(new LinkedHashMap<>());
    }

    public FormData(Map<String, String> params) {
        this.params = params;
    }

    public static FormData from(MessageBody messageBody) {
        if (messageBody.isEmpty()) {
            return new FormData();
        }
        return new FormData(extractFormData(messageBody));
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
