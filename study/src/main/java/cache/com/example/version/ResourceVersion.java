package cache.com.example.version;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class ResourceVersion {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyyMMddHHmmSS";

    private String version;

    private static String now() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return LocalDateTime.now().format(formatter);
    }

    @PostConstruct
    public void init() {
        this.version = now();
    }

    public String getVersion() {
        return version;
    }
}
