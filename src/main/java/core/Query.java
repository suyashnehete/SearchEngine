package core;

import java.util.List;

/**
 * A record that represents a query with tokens, operators, phrases, and wildcards.
 *
 * @param tokens a list of tokens in the query
 * @param operators a list of operators in the query
 * @param phrases a list of phrases in the query
 * @param wildcards a list of wildcards in the query
 */
public record Query(List<String> tokens, List<String> operators, List<String> phrases, List<String> wildcards) {

    /**
     * Returns a string representation of the Query.
     *
     * @return a string representation of the Query
     */
    @Override
    public String toString() {
        return "Query{" +
                "tokens=" + tokens +
                ", operators=" + operators +
                ", phrases=" + phrases +
                ", wildcards=" + wildcards +
                '}';
    }

}