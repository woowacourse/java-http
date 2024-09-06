package org.apache.coyote.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParameterParser {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int PARAM_KEY_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    private QueryParameterParser() {
    }

    public static Map<String, List<String>> parse(String queryParameter) {
        return Arrays.stream(queryParameter.split(PARAMETER_DELIMITER))
                .map(queryString -> queryString.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        query -> query[PARAM_KEY_INDEX],
                        query -> List.of(query[PARAM_VALUE_INDEX]),
                        (existingList, newList) -> {
                            existingList.addAll(newList);
                            return existingList;
                        }
                ));
    }
}
