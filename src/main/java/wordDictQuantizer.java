package src.main.java;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.*;

public class wordDictQuantizer {

    // some quick tests
    public static void main(String[] args) throws IOException {

        // load key distance file into a Map where the two keys are the map key and the distance as the entry
        Scanner keyFile = new Scanner(new File("src/main/java/keys.txt"));
        Map<String, Double> keys = new HashMap<>();
        while (keyFile.hasNext()) {
            String currentKeyLine = keyFile.nextLine();
            keys.put(currentKeyLine.charAt(0) + "" + currentKeyLine.charAt(1), Double.parseDouble(currentKeyLine.substring(2)));
        }
        keyFile.close();

        // verbose output for showing key map contents
        //for (Map.Entry<String, Double> entry : keys.entrySet()) {
        //    System.out.println(entry.getKey() + ": " + entry.getValue());
        //}

        List<String> dictionary = new ArrayList<>();
        Scanner dictFile = new Scanner(new File("src/main/java/words.txt"));
        while (dictFile.hasNext()) {
            dictionary.add(dictFile.next());
        }
        dictFile.close();

        long start = System.currentTimeMillis();
        List<String> Test1 = wordDictQuantizer(new String("helpo"), 5, dictionary, keys);
        List<String> Test2 = wordDictQuantizer(new String("FUNCTIONP"), 0, dictionary, keys);
        List<String> Test3 = wordDictQuantizer(new String("wkll"), 2, dictionary, keys);
        List<String> Test4 = wordDictQuantizer(new String("puj"), 5, dictionary, keys);
        List<String> Test5 = wordDictQuantizer(new String("Recommnedations"), 10, dictionary, keys);
        long end = System.currentTimeMillis();
        for (String words : Test1) {
            System.out.println("Test1: " + words);
        }
        for (String words : Test2) {
            System.out.println("Test2: " + words);
        }
        for (String words : Test3) {
            System.out.println("Test3: " + words);
        }
        for (String words : Test4) {
            System.out.println("Test4: " + words);
        }
        for (String words : Test5) {
            System.out.println("Test5: " + words);
        }

        double runTime = (end - start) / 1000.0;
        System.out.println("Run time in seconds: " + runTime);
    }

    /**
     *
     * @param word non-empty word to be corrected (assume not in dictionary)
     * @param automation automation level (0 to 10) --> 0 = full manual, 10 = full automatic
     * @param dict the dictionary list to do used
     * @param keys key distance map (with two key characters as key and distance as value)
     * @return If correctness threshold of closest word match exceeds the automation threshold --> List with top 5 closest words
     * If correction can be made without exceeding threshold of automation --> List with one entry which is the corrected word to use
     * If the word exists in the dictionary --> List with only one entry which is the original word
     * @throws IOException if dictionary or keys file does not exist
     */
    public static List<String> wordDictQuantizer(String word, int automation, List<String> dict, Map<String, Double> keys) throws IOException {

        String wordUp = word.toUpperCase();
        Map<String, Double> scoredWords = new HashMap<>();

        for (String dictEntry : dict) {
            // difference calculations start here
            double keyDiff = 0;
            int keyChanges = 0;
            double diff = Math.abs(wordUp.length() - dictEntry.length());
            if (wordUp.length() > dictEntry.length()) {
                StringBuilder dictEntryMatchedLength = new StringBuilder(dictEntry);
                dictEntryMatchedLength.append(new StringBuilder(CharBuffer.allocate(Math.abs(wordUp.length() - dictEntry.length())).toString().replace('\0', ' ')));
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
                wordUpMatchedLength.append(new StringBuilder(CharBuffer.allocate(Math.abs(wordUp.length() - dictEntry.length())).toString().replace('\0', ' ')));
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
}


