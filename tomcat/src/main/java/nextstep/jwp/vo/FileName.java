package nextstep.jwp.vo;

public class FileName {

    private static final String EXTENSION_DELIMITER = ".";
    private final String baseName;
    private final String extension;

    public FileName(String baseName, String extension) {
        this.baseName = baseName;
        this.extension = extension;
    }

    public String concat() {
        return baseName + EXTENSION_DELIMITER + extension;
    }

    public boolean isSame(String baseName) {
        return this.baseName.equals(baseName);
    }

    public String getBaseName() {
        return baseName;
    }

    public String getExtension() {
        return extension;
    }
}
