import core.SearchEngine;

public class Main {
    public static void main(String[] args){
        SearchEngine searchEngine = new SearchEngine();
        searchEngine.insert("cat");
        searchEngine.insert("car");
        searchEngine.insert("card");
        searchEngine.insert("cart");
    }
}
