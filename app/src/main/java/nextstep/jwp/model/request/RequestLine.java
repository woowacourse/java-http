package nextstep.jwp.model.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.ProtocolType;

public class RequestLine {

    private final String method;
    private final String path;
    private final Map<String, String> queries;
    private final ProtocolType protocol;

    public RequestLine(String line) throws IOException {
        String[] split = line.split(" ");
        this.method = split[0];
        this.path = extractPath(split[1]);
        this.queries = extractQueries(split[1]);
        this.protocol = ProtocolType.find(split[2]);
    }

    private Map<String, String> extractQueries(String path) {
        int index = path.indexOf('?');
        if (index < 0) {
            return new HashMap<>();
        }
        String queryString = path.substring(index + 1);
        return Stream.of(queryString.split("&"))
                .map(x -> x.split("="))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));
    }

    private String extractPath(String path) {
        int index = path.indexOf('?');
        if (index < 0) {
            return path;
        }
        return path.substring(0, index);
    }

    public String getMethod() {
        return method;
    }

    public FileType getFileType() {
        int index = path.indexOf('.');
        String fileType = path.substring(index + 1);
        return FileType.valueOf(fileType.toUpperCase());
    }

    public String getPath() {
        return path;
    }

    public boolean hasQueries() {
        return !queries.isEmpty();
    }

    public Map<String, String> getQueries() {
        return new HashMap<>(queries);
    }

    public ProtocolType getProtocol() {
        return protocol;
    }
}
