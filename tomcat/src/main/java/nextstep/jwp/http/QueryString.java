package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class QueryString {

    private final Map<String, String> params;

    private QueryString(Map<String, String> params) {
        this.params = params;
    }

    public static QueryString from(String line) {
        if ("".equals(line)) {
            return new QueryString(Collections.emptyMap());
        }

        String[] params = line.split("&");

        Map<String, String> paramMap = Arrays.stream(params)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));

        return new QueryString(paramMap);
    }

    public boolean containsKey(String key) {
        return params.containsKey(key);
    }

    // TODO: 2023/09/04 예외처리 수정
    public String get(String key) {
        if (containsKey(key)) {
            return params.get(key);
        }

        throw new NoSuchElementException();
    }

    public boolean hasValues() {
        return params.size() != 0;
    }
}
