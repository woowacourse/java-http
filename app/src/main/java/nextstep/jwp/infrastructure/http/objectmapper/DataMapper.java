package nextstep.jwp.infrastructure.http.objectmapper;

import java.util.Map;

public interface DataMapper {

    Map<String, String> parse(final String data);
}
