package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.InvalidHostHeaderException;

import java.util.List;

public class HostHeader {

    private static final String DELIMITER = ":";
    private static final int PARSED_SIZE = 2;
    private static final int DOMAIN_NAME_INDEX = 0;
    private static final int PORT_NUMBER_INDEX = 1;

    private final String domainName;
    private final int portNumber;

    private HostHeader(final String domainName, final int portNumber) {
        this.domainName = domainName;
        this.portNumber = portNumber;
    }

    public static HostHeader from(final String hostHeader) {
        final List<String> parsedHostHeader = parseByDelimiter(hostHeader);
        final String domainName = parsedHostHeader.get(DOMAIN_NAME_INDEX);
        final int portNumber = toNumber(parsedHostHeader.get(PORT_NUMBER_INDEX));
        return new HostHeader(domainName, portNumber);
    }

    private static List<String> parseByDelimiter(final String hostHeader) {
        final List<String> parsedValue = List.of(hostHeader.split(DELIMITER));
        if (parsedValue.size() != PARSED_SIZE) {
            throw new InvalidHostHeaderException();
        }
        return parsedValue;
    }

    private static int toNumber(final String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new InvalidHostHeaderException();
        }
    }

    public String getDomainName() {
        return domainName;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
