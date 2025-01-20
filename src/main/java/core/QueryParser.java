package core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A QueryParser is responsible for parsing a query string into tokens, operators, phrases, and wildcards.
 */
public class QueryParser {
    private static final String AND_OPERATOR = "AND";
    private static final String OR_OPERATOR = "OR";
    private static final String NOT_OPERATOR = "NOT";
    private static final Pattern PHRASE_PATTERN = Pattern.compile("\"([^\"]*)\"");
    private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\*?\\w+\\*?");

    /**
     * Parses the given query string and returns a Query object containing tokens, operators, phrases, and wildcards.
     *
     * @param query the query string to parse
     * @return a Query object containing the parsed elements
     */
    public Query parse(String query) {
        List<String> tokens = new ArrayList<>();
        List<String> operators = new ArrayList<>();
        List<String> phrases = new ArrayList<>();
        List<String> wildcards = new ArrayList<>();

        // Step 1: Extract phrases in quotes
        Matcher phraseMatcher = PHRASE_PATTERN.matcher(query);
        while (phraseMatcher.find()) {
            String phrase = phraseMatcher.group(1);
            phrases.add(phrase);
            // Remove the phrase from the query to avoid duplication
            query = query.replace("\"" + phrase + "\"", "");
        }

        // Step 2: Tokenize remaining query
        String[] parts = query.split("\\s+");
        for (String part : parts) {
            if (part.equalsIgnoreCase(AND_OPERATOR) || part.equalsIgnoreCase(OR_OPERATOR) || part.equalsIgnoreCase(NOT_OPERATOR)) {
                operators.add(part.toUpperCase());
            } else if (part.contains("*")) {
                wildcards.add(part);
            } else if (!part.isEmpty()) {
                tokens.add(part);
            }
        }

        return new Query(tokens, operators, phrases, wildcards);
    }
}