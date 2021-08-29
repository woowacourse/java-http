package nextstep.jwp.http.request;

import java.util.Map;

public interface RequestBody {

    Map<String, String> getAllContents();

    String getValue(String key);

    boolean isEmpty();

}
