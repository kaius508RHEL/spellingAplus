package src.main.java;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model for the spell checker.
 */
public class SpellCheckerModel {
    private final File inputFile;
    private final String[] content;
    private boolean[] isComment;
    private final int automation;
    private final List<String> variableList;
    private final List<String> changeList;
    private final static List<String> dictionary = new ArrayList<>();
    private final static Map<String, Double> keys = new HashMap<>();
    private static List<String> whitelist;

    /**
     * Create a new SpellCheckerModel.
     * 
     * @param file input text file to be processed by the spell checker
     * @param automation the level of the spell checking
     * @throws IllegalArgumentException if automation argument is out of range 0 to 10 inclusive
     */
    public SpellCheckerModel (File file, int automation, List<String> whitelist) throws IllegalArgumentException {
        if (automation < 0 || automation > 10){
            throw new IllegalArgumentException("Automation value out of range.");
        }
        this.inputFile = new File(file.getAbsolutePath());
        this.automation = automation;
        this.variableList = new ArrayList<>();
        this.changeList = new ArrayList<>();
        this.whitelist = whitelist;
        try {
            this.content = getContent(file);
        } catch (IOException e) {
            throw new IllegalArgumentException("File doesn't exist");
        }
        loadDictionary();
        loadKeys();
    }

    private void loadDictionary() {
        try (Scanner scan = new Scanner(new File("src/main/java/words.txt"))) {
            while (scan.hasNextLine()) {
                dictionary.add(scan.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary file missing.");
        }
    }

    private void loadKeys() {
        try(Scanner keyFile = new Scanner(new File("src/main/java/keys.txt"))) {
            while (keyFile.hasNext()) {
                String currentKeyLine = keyFile.nextLine();
                keys.put(currentKeyLine.charAt(0) + "" + currentKeyLine.charAt(1), Double.parseDouble(currentKeyLine.substring(2)));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Keys file missing.");
        }
    }

    /**
     * Reads the target file and parses its contents into a single string.
     *
     * <p>The specific file to be parsed depends on the class's configuration
     * (e.g., a file path stored as a field). Implementations typically read
     * the file line by line and concatenate the contents into a single
     * {@code String}, optionally performing preprocessing such as trimming
     * whitespace or removing formatting characters.</p>
     *
     * @return a string containing the full parsed contents of the file; never {@code null},
     *         though it may be empty if the file has no content
     * @throws IOException if an I/O error occurs while reading the file
     */

    private String[] getContent(File file) throws IOException {

        String text = Files.readString(file.toPath());

        List<String> tokens = new ArrayList<>();
        List<Boolean> commentFlags = new ArrayList<>();

        Pattern p = Pattern.compile(
                "(//)" +                            // 1: line comment start
                        "|(/\\*\\*)" +                      // 2: JavaDoc start /**
                        "|(/\\*)" +                         // 3: block comment start /*
                        "|(\\*/)" +                         // 4: block comment end */
                        "|(\\s+)" +                         // 5: whitespace
                        "|([A-Za-z_][A-Za-z0-9_]*)" +       // 6: identifiers/words
                        "|([{}()\\[\\].,;:+\\-*/=<>!&|^%?~])" + // 7: symbols
                        "|(.)",                             // 8: any other char
                Pattern.DOTALL
        );

        Matcher m = p.matcher(text);

        boolean inBlockComment = false;
        boolean inLineComment = false;

        while (m.find()) {

            String token;
            boolean isCommentToken = false;

            if (m.group(1) != null) {            // //
                token = "//";
                inLineComment = true;
                isCommentToken = true;
            }
            else if (m.group(2) != null) {       // /**
                token = "/**";
                inBlockComment = true;
                isCommentToken = true;
            }
            else if (m.group(3) != null) {       // /*
                token = "/*";
                inBlockComment = true;
                isCommentToken = true;
            }
            else if (m.group(4) != null) {       // */
                token = "*/";
                isCommentToken = true;
                inBlockComment = false;
            }
            else {
                token = m.group();

                // Any token inside comment should be marked
                if (inLineComment || inBlockComment) {
                    isCommentToken = true;
                }
            }

            // End of line ends a // comment
            if (token.contains("\n")) {
                inLineComment = false;
            }

            // ⬇️ NEW PART: detect identifiers not in comments
            if (!isCommentToken && isWord(token)) {
                variableList.add(token.toUpperCase());
            }

            tokens.add(token);
            commentFlags.add(isCommentToken);
        }

        isComment = new boolean[commentFlags.size()];
        for (int i = 0; i < commentFlags.size(); i++) {
            isComment[i] = commentFlags.get(i);
        }

        return tokens.toArray(new String[0]);
    }

    public static boolean isWord(String s) {
        return s.matches("[A-Za-z_][A-Za-z0-9_]*");
    }

    /**
    * Computes and returns a list of the five best choices based on the given input string.
    *
    * <p>The definition of "best" depends on the algorithm implemented in this method.
    * Typically, this may involve evaluating how well each possible choice matches
    * or relates to the input string {@code s} - for example, by using similarity
    * metrics, scoring heuristics, or domain-specific rules.</p>
    *
    * <p>If fewer than five valid choices exist, the returned list will contain
    * as many choices as are available. The returned list is unmodifiable.</p>
    *
    * @param s the input string used to compute and rank the possible choices;
    *          must not be {@code null}
    * @return an unmodifiable list containing up to five of the best choices,
    *         ordered from best to worst; never {@code null}
    */

    /**
     * @param word non-empty word to be corrected (assume not in dictionary)
     * @param automation automation level (0 to 10) --> 0 = full manual, 10 = full automatic
     * @return If correctness threshold of closest word match exceeds the automation threshold --> List with top 5 closest words
     * If correction can be made without exceeding threshold of automation --> List with one entry which is the corrected word to use
     * If the word exists in the dictionary --> List with only one entry which is the original word
     * @throws IOException if dictionary or keys file does not exist
     */
    public static List<String> bestChoices(String word, int automation) throws IOException {
        String wordUp = word.toUpperCase();
        Map<String, Double> scoredWords = new HashMap<>();

        for (String dictEntry : dictionary) {
            // difference calculations start here
            double keyDiff = 0;
            int keyChanges = 0;
            double diff = Math.abs(wordUp.length() - dictEntry.length());
            if (wordUp.length() > dictEntry.length()) {
                StringBuilder dictEntryMatchedLength = new StringBuilder(dictEntry);
                dictEntryMatchedLength.append(new StringBuilder(CharBuffer.allocate((int) diff).toString().replace('\0', ' ')));
                for (int i = 0; i < dictEntryMatchedLength.length(); i++) {
                    if (dictEntryMatchedLength.charAt(i) != wordUp.charAt(i)) {
                        diff++;
                        String keyEntry = wordUp.charAt(i) + "" + dictEntryMatchedLength.charAt(i);
                        if (keys.containsKey(keyEntry)) {
                            keyDiff += keys.get(keyEntry);
                        } else {
                            keyDiff += 100;
                        }
                        keyChanges++;

                    }
                }
            } else {
                StringBuilder wordUpMatchedLength = new StringBuilder(wordUp);
                wordUpMatchedLength.append(new StringBuilder(CharBuffer.allocate((int) diff).toString().replace('\0', ' ')));
                for (int i = 0; i < wordUpMatchedLength.length(); i++) {
                    if (wordUpMatchedLength.charAt(i) != dictEntry.charAt(i)) {
                        diff++;
                        String keyEntry = wordUpMatchedLength.charAt(i) + "" + dictEntry.charAt(i);
                        if (keys.containsKey(keyEntry)) {
                            keyDiff += keys.get(keyEntry);
                        } else {
                            keyDiff += 100;
                        }
                        keyChanges++;

                    }
                }
            }
            double score;
            // old algorithms
            //score = (10.0*(Math.abs(wordUp.length()-stringDiff(wordUp,dictEntry))/(1.0*wordUp.length())));
            //score = ((double) Math.abs(diff) /wordUp.length() + keyDiff/(1000.0*keyChanges));

            score = ((double) Math.abs(diff) / wordUp.length() + (keyDiff * keyChanges) / 1000);
            scoredWords.put(dictEntry, score);

            // verbose scoring output data
            //if(4*score<=automation) {
            //    System.out.println("C: " + dictEntry + " Score: " + score + " String diff: " + diff + " Keys diff: " + keyDiff + " Key changes: " + keyChanges);
            //}
        }

        List<String> topFiveResults = scoredWords.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()) // sort entries by score
                .limit(5)                             // take lowest 5
                .map(Map.Entry::getKey)               // extract the key
                .toList();

        double topScore = scoredWords.get(topFiveResults.get(0));
        if ((4 * topScore <= automation || automation==10) && automation != 0) {
            return new ArrayList<>(Collections.singleton(topFiveResults.get(0)));
        } else {
            return topFiveResults;
        }
    }

    private boolean inDictionary(String s){
        return dictionary.contains(s.toUpperCase());
    }

    private boolean inWhitelist(String s){
        return whitelist.contains(s.toUpperCase());
    }

    private boolean inVariableList(String s){
        return variableList.contains(s.toUpperCase());
    }
    /**
     * Takes in a file, reads text, identifies documentation, overwrites to accommodate 
     * corrections, and returns the edited file with a list of changes attached to it
     * @return the file with automated corrections and marked suggestions appended
     * @throws IOException if file does not exist
     */
    public File spellCheck () throws IOException {
        for (int i = 0; i < content.length; i++) {
            if (isComment[i] && isWord(content[i])) {
                if (!inDictionary(content[i]) && !inWhitelist(content[i]) && !inVariableList(content[i])) {
                    List<String> options = bestChoices(content[i],automation);
                    if (options.size() == 1) {
                        String corrected = options.get(0);
                        changeList.add("Word changed: [" + content[i] + "] -> [" + matchCase(content[i], corrected) + "]");
                        content[i] = matchCase(content[i], corrected);
                    }
                    else {
                        String userChoice = getUserChoice(content[i], options);
                        if (userChoice != null && !userChoice.isEmpty()) {
                            changeList.add("Word changed: [" + content[i] + "] -> [" + matchCase(content[i], userChoice) + "]");
                            content[i] = matchCase(content[i], userChoice);
                        }
                    }
                }
            }
        }
        SpellCheckerView.displayOutput(changeList);
        return writeCorrectedContentToFile();
    }

    private String matchCase(String original, String corrected) {
        if (original.isEmpty() || corrected.isEmpty()) return corrected;

        // 1. Entire word lowercase → output lowercase
        if (Character.isLowerCase(original.charAt(0)) &&
                original.substring(1).equals(original.substring(1).toLowerCase())) {
            return corrected.toLowerCase();
        }

        // 2. Entire word uppercase → output uppercase
        if (original.equals(original.toUpperCase())) {
            return corrected.toUpperCase();
        }

        // 3. Mixed case, capitalized, or anything else → Capitalize
        return corrected.substring(0,1).toUpperCase() + corrected.substring(1).toLowerCase();
    }

    private String getUserChoice(String original, List<String> options) {
        System.out.println("Unknown word: " + original);
        System.out.println("Choose an option below:");
        for (int i = 0; i < options.size(); i++) {
            System.out.print("(" + (i + 1) + "): " + options.get(i));
            if(i < (options.size() - 1)) {
                System.out.print(" | ");
            } else {
                System.out.println(" ");
            }
        }
        System.out.println("(A) Keep current | (B) Whitelist current | (C) Set custom replacement");

        Scanner scan = new Scanner(System.in);
        String choice = scan.nextLine();

        if (choice.equals("A")) {
            return original;
        }
        if (choice.equals("B")) {
            whitelist.add(original);
            return original;
        }
        if (choice.equals("C")) {
            System.out.print("Enter replacement: ");
            return scan.nextLine();
        }
        if (Integer.parseInt(choice) >= 1 && Integer.parseInt(choice) <= options.size()) {
            return options.get(Integer.parseInt(choice) - 1);
        }
        return original;
    }

    private File writeCorrectedContentToFile() throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile))) {
            for (String w : content) {
                writer.write(w);
            }
        }
        return inputFile;
    }
}