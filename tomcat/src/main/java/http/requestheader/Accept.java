package http.requestheader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Accept {

    private static final String FIELD_DELIMITER = ", *";
    private final List<AcceptField> fields;

    public Accept() {
        fields = new ArrayList<>();
    }

    public Accept(String rawFieldValues) {
        String[] fieldValues = rawFieldValues.split(FIELD_DELIMITER);
        this.fields = Arrays.stream(fieldValues)
                .map(AcceptField::new)
                .toList();
    }

    public String processContentType(String path) {
        return fields.stream()
                .max(AcceptField::compare)
                .map(field -> field.buildMediaType(path))
                .orElse("text/html");
    }
}
