package kokodak.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FormDataParser {

    public static Map<String, String> parse(final String requestBody) {
        final Map<String, String> formData = new HashMap<>();
        for (final String formDataSnippet : requestBody.split("&")) {
            final String[] keyValue = formDataSnippet.split("=");
            formData.put(keyValue[0], keyValue[1]);
        }
        return formData;
    }
}
