import java.io.*;
import java.util.*;

public class WordSearch {


    private class WordNode {
        String mThisWord;
        int mLen;
        Map<Character, List<Integer>> mLetterIndices;

        @Override public boolean equals(Object o) {
            if (!(o instanceof WordNode)) return false;
            return mThisWord.equals(o);
        }

        WordNode(String s) {
            mThisWord = s.toLowerCase();
            mLen = s.length();
            mLetterIndices = new HashMap<>();
            for (int i = 0 ; i < mLen ; i++) {
                mLetterIndices.putIfAbsent(mThisWord.charAt(i), new ArrayList<>());
                mLetterIndices.get(mThisWord.charAt(i)).add(i);
            }
        }

        boolean checkContents(char[] charsToCheck, int charsIndex, int currentIndex) {

            //base case, we check all the way through and the index is greater
            if (charsToCheck.length <= charsIndex) {
                return true;
            }

            char currentChar = charsToCheck[charsIndex];
            int bestIndex = Integer.MAX_VALUE;
            List<Integer> list = mLetterIndices.get(currentChar);
            if (null == list) return false; //does not contain the character
            for (int indexIntoWord : list) {
                if (indexIntoWord >= currentIndex //farther in word
                        && indexIntoWord < bestIndex) { //but minimum distance away in case of repeats
                    bestIndex = indexIntoWord;
                }
            }
            if (Integer.MAX_VALUE == bestIndex) {
                return false;
            }

            return checkContents(charsToCheck, charsIndex+1, bestIndex);
        }

        boolean checkContentsNaive(char[] charsToCheck) {
            if (null == charsToCheck) {
                return false;
            }
            int i = 0;
            char[] word = mThisWord.toCharArray();
            for (int j = 0 ; j < word.length ; j++) {
                if (word[j] == charsToCheck[i]) {
                    i++;
                }

                if (i >= charsToCheck.length) {
                    return true;
                }
            }
            return false;
        }
    }

    private Map<Character, Set<WordNode>> mWordSets = new HashMap<>();


    private void createSets() {
        mWordSets.clear();
        for (int i = 'a' ; i <= 'z' ; i++) {
            mWordSets.put((char) i, new HashSet<>());
        }
    }

    public void readStrings(Collection<String> words) {
        createSets();

        for (String s : words) {
            if (!s.matches("\\p{Alpha}+?")) {
//                System.out.println("failed word: " + s);
                continue;
            }

            WordNode n = new WordNode(s);
            //new node here
            for (char c : s.toLowerCase().toCharArray()){
                Set<WordNode> gotten = mWordSets.get(c);

                if (null == gotten) {
                    System.out.println("error! c was "+ c);
                    System.exit(-2);
                }

                gotten.add(n);
            }
        }
    }


    public Set<String> findStrings(char[] inOrder) {
        if (null == inOrder || inOrder.length < 1) {
            return null;
        }

        Set<WordNode> matchSet = new HashSet<>(mWordSets.get(inOrder[0]));
        Set<String> retSet = new HashSet<>();
        for (int i = 1 ; i < inOrder.length ; i++) {
            matchSet.retainAll(mWordSets.get(inOrder[i]));
        }

        for (WordNode word : matchSet) {
            if (word.checkContents(inOrder, 0, 0)) {
                retSet.add(word.mThisWord);
            }
        }
        return retSet;
    }
    public Set<String> findStringsNaive(char[] inOrder) {
        if (null == inOrder || inOrder.length < 1) {
            return null;
        }
        Set<WordNode> matchSet = new HashSet<>(mWordSets.get(inOrder[0]));
        Set<String> retSet = new HashSet<>();
        for (int i = 1 ; i < inOrder.length ; i++) {
            matchSet.retainAll(mWordSets.get(inOrder[i]));
        }
        for (WordNode word : matchSet) {
            if (word.checkContentsNaive(inOrder)) {
                retSet.add(word.mThisWord);
            }
        }

        return retSet;
    }


    public static void main(String[] args) {
        System.out.println("Start of reading");
        long start = System.currentTimeMillis();

        ArrayList<String> wordList = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("words.txt"));
            String line;
            while ((line = reader.readLine())!= null) {
                wordList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        long end = System.currentTimeMillis();
        System.out.println("size of dict: " + wordList.size());
        System.out.println("end of reading, time was: " + (end - start));
        start = end;

        WordSearch w = new WordSearch();
        w.readStrings(wordList);

        end = System.currentTimeMillis();
        System.out.println("end of building, time was: " + (end - start));
        start = end;

        char[] inOrder = {'z', 'r', 'a'};
        Set<String> results = w.findStrings(inOrder);
        end = System.currentTimeMillis();
        System.out.println("end of finding, time was: " + (end - start));
        start = end;

        Set<String> resultsNaive = w.findStringsNaive(inOrder);
        end = System.currentTimeMillis();
        System.out.println("end of findingnaive, time was: " + (end - start));

        if (results.containsAll(resultsNaive) && resultsNaive.containsAll(results)) {
            System.out.println("it works naive");
        }

        for(String s: results) {
            System.out.println(s);
        }

    }

}
