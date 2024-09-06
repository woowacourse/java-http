package http.requestheader;

import java.util.Arrays;

public class AcceptField {

    private static final String PARAMETER_DELIMITER = "; *";
    private static final String TYPE_DELIMITER = "/";
    private static final String PARAMETER_VALUE_DELIMITER = "=";
    private static final String PRIORITY_PARAMETER_NAME = "q";
    private static final String WILD_TYPE = "*";
    private String type;
    private String subType;
    private double priority;

    public AcceptField(String rawFieldValue) {
        String[] fields = rawFieldValue.split(PARAMETER_DELIMITER);
        validateFieldsLength(fields);
        String[] types = fields[0].split(TYPE_DELIMITER);
        validateTypesLength(types);
        this.type = types[0];
        this.subType = types[1];
        this.priority = parsePriority(fields);
    }

    private void validateFieldsLength(String[] fields) {
        if (fields.length < 1) {
            throw new RuntimeException("invalid Accept header length");
        }
    }

    private void validateTypesLength(String[] types) {
        if (types.length < 2) {
            throw new RuntimeException("invalid Accept type");
        }
    }

    private double parsePriority(String[] fields) {
        if (fields.length == 1) {
            return 1;
        }
        return Arrays.stream(fields)
                .skip(0)
                .map(field -> field.split(PARAMETER_VALUE_DELIMITER))
                .filter(tokens -> tokens.length == 2 && tokens[0].equals(PRIORITY_PARAMETER_NAME))
                .findFirst()
                .map(tokens -> Double.parseDouble(tokens[1]))
                .orElse(1.0);
    }

    public int compare(AcceptField other) {
        if (this.priority < other.priority) {
            return -1;
        }
        if (this.priority > other.priority) {
            return 1;
        }
        return compareType(other);
    }

    private int compareType(AcceptField other) {
        int result = compareTypeValue(this.type, other.type);
        if (result == 0) {
            return compareSubType(other);
        }
        return result;
    }

    private int compareSubType(AcceptField other) {
        return compareTypeValue(this.subType, other.subType);
    }

    private int compareTypeValue(String typeValue, String otherTypeValue) {
        if (typeValue.equals(WILD_TYPE) && !otherTypeValue.equals(WILD_TYPE)) {
            return -1;
        }
        if (!typeValue.equals(WILD_TYPE) && otherTypeValue.equals(WILD_TYPE)) {
            return 1;
        }
        return typeValue.compareTo(otherTypeValue);
    }

    public String buildMediaType(String path) {
        if (!type.equals(WILD_TYPE) && !subType.equals(WILD_TYPE)) {
            return combineType(this.subType);
        }
        if (type.equals(WILD_TYPE)) {
            return "text/html";
        }
        return buildMediaTypeFromUri(path);
    }

    private String combineType(String subType) {
        return type + "/" + subType;
    }

    private String buildMediaTypeFromUri(String path) {
        String extension = parseFileExtension(path);
        if (extension.isEmpty()) {
            return "text/plain";
        }
        return combineType(subType);
    }

    private String parseFileExtension(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        String fileName = path.substring(path.lastIndexOf('/') + 1);

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
