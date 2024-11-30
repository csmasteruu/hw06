public class EvilHangmanRunner {
    public static void main(String[] args) {
        String dictionaryFile = "engDictionary.txt";
        EvilHangman game = new EvilHangman(dictionaryFile);
        game.start();
    }
}