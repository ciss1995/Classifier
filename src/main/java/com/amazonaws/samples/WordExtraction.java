package com.amazonaws.samples;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordExtraction implements Serializable{
    
	private static final long serialVersionUID = 1L;

	//Get the number of view, the number of Category and word appearance and the the number of times a category was found for a word
    public WordStatistics extractWordStats(List<Format> dataset) {
    	WordStatistics stats = new WordStatistics();
        Integer categoryCount;
        String category;
        Integer wordCategoryCount;
        String word;
        Map<String, Integer> featureCategoryCounts;
        
        //FOr each formated document
        for(Format format : dataset) {
        	//increase the number of view
            ++stats.n; 
            //Get the category
            category = format.getCategory();
            //increase the category counter by one
            categoryCount = stats.categoryCounts.get(category);
            if(categoryCount==null) {
                stats.categoryCounts.put(category, 1);
            }
            else {
                stats.categoryCounts.put(category, categoryCount+1);
            }
            //For each tokens inside a category
            for(Map.Entry<String, Integer> entry : format.getTokens().entrySet()) {
            	//Get the word
                word = entry.getKey();   
                //get the count of word appearance in the categories
                featureCategoryCounts = stats.wordCategoryJointCount.get(word);
                if(featureCategoryCounts==null) { 
                    //initialize it if it does not exist
                    stats.wordCategoryJointCount.put(word, new HashMap<String, Integer>());
                }         
                //increment word and category count
                wordCategoryCount =stats.wordCategoryJointCount.get(word).get(category);
                if(wordCategoryCount ==null) {
                	wordCategoryCount =0;
                }
                //increase the number of time a word appeared in the category
                stats.wordCategoryJointCount.get(word).put(category, ++wordCategoryCount);
            }
        }
        return stats;
    }

    //Selection of words using CHi Square method
    public Map<String, Double> chisquare(WordStatistics stats, double criticalLevel) {
    	//List of selected word and their Chi Square result
        Map<String, Double> selectedWord = new HashMap<>();
        
        String word;
        String category;
        Map<String, Integer> categoryList;
        
        int N1dot, N0dot, N00, N01, N10, N11;
        double chisquareScore;
        Double previousScore;
        for(Map.Entry<String, Map<String, Integer>> entry1 : stats.wordCategoryJointCount.entrySet()) {
            word = entry1.getKey();
            //List of all category that the word is in
            categoryList = entry1.getValue();
            
            //calculate the N1. (number of documents that have the word)
            N1dot = 0;
            for(Integer count : categoryList.values()) {
                N1dot+=count;
            }
            
            //Calculate the N0. (number of documents that DONT have the word)
            N0dot = stats.n - N1dot;
            
            //For each category that has the word
            for(Map.Entry<String, Integer> entry2 : categoryList.entrySet()) {
                category = entry2.getKey();
                //N11 is the number of documents that have the word and belong on the category
                N11 = entry2.getValue(); 
                //N01 is the total number of documents that do not have the particular word BUT belong to the category
                N01 = stats.categoryCounts.get(category)-N11; 
                //N00 counts the number of documents that don't have the feature and don't belong to the category
                N00 = N0dot - N01; 
                //N10 counts the number of documents that have the feature and don't belong to the category
                N10 = N1dot - N11; 
                
                //calculate the chisquare score 
                chisquareScore = stats.n*Math.pow(N11*N00-N10*N01, 2)/((N11+N01)*(N11+N10)*(N10+N00)*(N01+N00));
                
                //if the score is larger than the critical value then add it in the list
                if(chisquareScore>=criticalLevel) {
                    previousScore = selectedWord.get(word);
                    if(previousScore==null || chisquareScore>previousScore) {
                        selectedWord.put(word, chisquareScore);
                    }
                }
            }
        }
        return selectedWord;
    }
}

