package org.apache.coyote.http11.controller.util;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.coyote.http11.request.RequestBody;

public class BodyExtractor {

    private static final String INVIDUAL_QUERY_PARAM_DIVIDER = "&";
    private static final String QUERY_PARAM_KEY_VALUE_SPLIT = "=";
    private static final int DONT_HAVE_VALUE = 1;

    private BodyExtractor() {
    }

    public static Map<String, String> convertBody(RequestBody requestBody) {
        if (requestBody.getBody().isEmpty()) {
            throw new IllegalArgumentException("Body 에 데이터가 없습니다.");
        }
        return Stream.of(requestBody.getBody().get().split(INVIDUAL_QUERY_PARAM_DIVIDER))
            .collect(Collectors.toMap(BodyExtractor::keyOf, BodyExtractor::valueOf));
    }

    private static String keyOf(String qp) {
        if (!qp.contains(QUERY_PARAM_KEY_VALUE_SPLIT)) {
            throw new IllegalArgumentException("유효하지 않은 key value 형식 입니다.");
        }
        String[] split = qp.split(QUERY_PARAM_KEY_VALUE_SPLIT);
        return split[0];
    }

    private static String valueOf(String qp) {
        String[] split = qp.split(QUERY_PARAM_KEY_VALUE_SPLIT);
        if (split.length == DONT_HAVE_VALUE) {
            return "";
        }
        return split[1];
    }
}
