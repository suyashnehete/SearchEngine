import core.SearchEngine;
import core.SearchResult;

import java.util.List;

public class Main {
    public static void main(String[] args){
        SearchEngine engine = new SearchEngine();
        engine.insert("hello");
        engine.insert("world");
        engine.insert("help");

        List<SearchResult> results = engine.fuzzySearch("help", 2);
        results.forEach(System.out::println);
    }
}
