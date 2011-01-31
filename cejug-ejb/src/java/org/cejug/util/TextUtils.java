package org.cejug.util;

/**
 * This class groups a set of methods to deal with special text requirements
 * that are not already covered by the Java API.
 * @author Hildeberto Mendonca
 */
public class TextUtils {

    /** Receives a sentence and converts the first letter of each word to a
     * capital letter and the rest of each word to lowercase. */
    public static String capitalizeFirstCharWords(String sentence) {
        final StringBuilder result = new StringBuilder(sentence.length());
        String[] words = sentence.split("\\s");
        for(int i = 0,length = words.length; i < length; ++i) {
            if(i > 0)
                result.append(" ");
            result.append(Character.toUpperCase(words[i].charAt(0)))
                  .append(words[i].substring(1).toLowerCase());
        }
        return result.toString();
    }
}