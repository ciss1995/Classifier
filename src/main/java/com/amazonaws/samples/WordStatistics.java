package com.amazonaws.samples;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WordStatistics implements Serializable{

	private static final long serialVersionUID = 1L;

	//Total number of views
    public int n;
    
    //Category and word appearance
    public Map<String, Map<String, Integer>> wordCategoryJointCount;
    
    //Count the number of times a category was found
    public Map<String, Integer> categoryCounts;

    
    public WordStatistics() {
        n = 0;
        this.wordCategoryJointCount = new HashMap<>();
        categoryCounts = new HashMap<>();
    }
}
