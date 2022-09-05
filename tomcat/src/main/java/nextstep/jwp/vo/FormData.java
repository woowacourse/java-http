package nextstep.jwp.vo;

import java.util.Map;

public class FormData {

    private final Map<String, String> formData;

    public FormData(Map<String, String> formData) {
        this.formData = formData;
    }

    public boolean isEmpty() {
        return this.formData.isEmpty();
    }

    public String get(String key) {
        return this.formData.get(key);
    }
}
