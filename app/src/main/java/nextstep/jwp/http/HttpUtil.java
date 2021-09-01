package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> queryMap = new HashMap<>();

        String[] data = query.split("&");
        for (String each : data) {
            String[] keyAndValue = each.split("=");
            queryMap.put(keyAndValue[0], keyAndValue[1]);
        }

        return queryMap;
    }


}
