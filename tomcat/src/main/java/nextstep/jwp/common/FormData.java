package nextstep.jwp.common;

import static nextstep.jwp.exception.FormDataExceptionType.INVALID_FORM;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.FormDataException;

public class FormData {

    private static final String AND = "&";
    private static final String EQUAL = "=";

    private final Map<String, String> formTable;

    private FormData(Map<String, String> formTable) {
        this.formTable = formTable;
    }

    public static FormData from(String formData) {
        Map<String, String> formTable = new HashMap<>();
        String[] splitByAnd = formData.split(AND);
        for (String pair : splitByAnd) {
            String[] splitByEqual = pair.split(EQUAL, 2);
            String key = splitByEqual[0];
            String value = splitByEqual[1];
            formTable.put(key, value);
        }
        return new FormData(formTable);
    }

    public String get(String key) {
        if (formTable.containsKey(key)) {
            return formTable.get(key);
        }
        throw new FormDataException(INVALID_FORM);
    }
}
