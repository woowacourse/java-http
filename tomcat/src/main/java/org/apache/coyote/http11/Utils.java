package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class Utils {

    private Utils() {
    }

    public static Map<String, String> parseToQueryParms(String queryString) {
        String[] keyValues = queryString.split("&");

        Map<String, String> queryParms = new HashMap<>();

        for (String keyValue : keyValues) {
            String[] queryParm = keyValue.split("=");
            String key = queryParm[0];
            String value = queryParm[1];
            queryParms.put(key, value);
        }
        return queryParms;
    }

    public static String readFile(String directory, String fileName) throws IOException {
        URL resource = Utils.class.getClassLoader().getResource(directory + "/" + fileName);
        Path path = Path.of(resource.getPath());
        return Files.readString(path);
    }

}
