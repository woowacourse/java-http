package nextstep.jwp.http.request;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final Logger log = LoggerFactory.getLogger(RequestBody.class);

    private Map<String, String> params = new HashMap<>();

    public RequestBody(BufferedReader reader) {
        try {
            if (reader.ready()) {
                String line = reader.readLine();
                if (Strings.isNullOrEmpty(line)) {
                    return;
                }
                addParams(line);
            }
        } catch (Exception exception) {
            log.error("Exception buffered reader read body", exception);
        }
    }

    private void addParams(String line) {
        String[] tokens = line.split("&");
        for (String token : tokens) {
            if (Strings.isNullOrEmpty(token)) {
                continue;
            }
            String[] tmp = token.split("=");
            if (tmp.length == 2) {
                this.params.put(tmp[0], tmp[1]);
            }
        }
    }

    public String getParam(String key) {
        return params.get(key);
    }
}
