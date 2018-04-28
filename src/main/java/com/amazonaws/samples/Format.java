package com.amazonaws.samples;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Format implements Serializable{
    
	private static final long serialVersionUID = 1L;

    //Categories
    private String category;
    
	//Tokens and number of time appeared in the document
    private Map<String, Integer> tokens;
    
    public void Fomart() {
        this.tokens = new HashMap<>();
    }
    //Get tokens and count
    public Map<String, Integer> getTokens(){
    	return this.tokens;
    }
    //Set the tokens and count
    public void setTokens(Map<String, Integer> tokens){
    	this.tokens = tokens;
    }
    //Set the category
    public void setCategory(String cat){
    	this.category = cat;
    }
    public String getCategory(){
    	return this.category;
    }
}
