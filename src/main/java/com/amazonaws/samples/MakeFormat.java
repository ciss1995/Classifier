package com.amazonaws.samples;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MakeFormat implements Serializable{

	private static final long serialVersionUID = 1L;

    //Takes an array and return the number of time each token appears in the text
    public static Map<String, Integer> countWord(String[] keywordArray) {
        Map<String, Integer> counts = new HashMap<>();
        
        Integer counter;
        for(int i=0;i<keywordArray.length;++i) {
            counter = counts.get(keywordArray[i]);
            if(counter==null) {
                counter=0;
            }
            counts.put(keywordArray[i], ++counter); //increase counter for the keyword
        }
        
        return counts;
    }
    
    //Get the words in an array
    public static String[] getWords(String text) {
        return text.split(" ");
    }
    
    //Get the proper format for the document
    public static Format getFormat(String text) {
        String[] keywordArray = getWords(text); 
        Format format = new Format();
        format.setTokens(countWord(keywordArray));
        return format;
    }
}
