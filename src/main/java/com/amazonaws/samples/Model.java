package com.amazonaws.samples;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Model implements Serializable{
	private static final long serialVersionUID = 1L;

	//equivalent to pvalue 0.001. It is used by feature selection algorithm
    private double chisquareCriticalValue = 10.83; 
    
    private Knowledge knowledge;
    
    //Constructor for training
    public Model() {
    }
    
    //constructor with knowledge
    public Model(Knowledge knowledge) {
        this.knowledge = knowledge;
    }
    
    //Return the knowledge
    public Knowledge getKnowledge() {
        return knowledge;
    }
    
    //Get the Chi Square value
    //=> level of statistical significance
    public double getChisquareCriticalValue() {
        return chisquareCriticalValue;
    }
    
    //Set the Chi Square value 
    //=>Level of statistical significance 
    public void setChisquareCriticalValue(double chisquareCriticalValue) {
        this.chisquareCriticalValue = chisquareCriticalValue;
    }
    
    //Get list of formated data
    private List<Format> preprocessDataset(Map<String, String[]> trainingDataset) {
        List<Format> formatedData = new ArrayList<>();     
        String[] words;  
        Format format;
        String category;    
        Iterator<Map.Entry<String, String[]>> it = trainingDataset.entrySet().iterator();       
        //Iterate through the categories and training data
        while(it.hasNext()) {
        	//For each entry
            Map.Entry<String, String[]> entry = it.next();
            //get the category
            category = entry.getKey();
            //get list of all the document with the same category
            words = entry.getValue();
            for(int i=0;i<words.length;++i) {
                //for each example in the category tokenize its text and convert it into a Document object.
            	//For each document of the same category, format the data
            	//In format, assign the category and a HashMap with every words and their count
            	//Format the document
                format = MakeFormat.getFormat(words[i]);
                //Set the category
                format.setCategory(category);
                //Add it to a list of formated documents
                formatedData .add(format);
            }
        }
        return formatedData;
    } 

    //Select's the best words and remove the noisy
    private WordStatistics selectWord(List<Format> dataset) {        
        WordExtraction featureExtractor = new WordExtraction();
        //Created an instance of the word statistics with statistics about words in the data 
        WordStatistics wordstats = featureExtractor.extractWordStats(dataset);      
        //we pass this information to the feature selection algorithm and we get a list with the selected features
        Map<String, Double> selectedWord = featureExtractor.chisquare(wordstats, chisquareCriticalValue);    
        //clip from the statistics to make selection for relevant word using Chi Square
        //Return the word and it's chiSquare result for the word for each category
        Iterator<Map.Entry<String, Map<String, Integer>>> it = wordstats.wordCategoryJointCount.entrySet().iterator();
        while(it.hasNext()) {
            String word = it.next().getKey();
            //Check if the word was selected
            if(selectedWord.containsKey(word)==false) {
                //If the word was not selected, remove it from the list => irrelevant
                it.remove();
            }
        } 
        return wordstats;
    }
    
    //Train the data buy formating it, eliminating the noisy words, calculate the probability for categories 
    //and calculate the likelihood for a word to be in a category
    public void train(Map<String, String[]> trainingDataset, Map<String, Double> categoryPriors) throws IllegalArgumentException {
        //Get the formated data
        List<Format> formatedData = preprocessDataset(trainingDataset);
        //Produce the words statistics and select the best words
        //Filters the noisy words
        WordStatistics wordStats =  selectWord(formatedData); 
        //instance of knowledge of the classifier
        knowledge = new Knowledge();
        //number of view
        knowledge.setNumView(wordStats.n); 
        //number of words
        knowledge.setNumWords(wordStats.wordCategoryJointCount.size()); 
        
        //check if there a prior probabilities was given
        //Will be null if training
        if(categoryPriors==null) { 
            //Get the probabilities from the training data
        	//Set total number of categories
            knowledge.setNumCat(wordStats.categoryCounts.size()); 
            //Create a new HashMap for the new probabilities
            knowledge.probabilityCat = new HashMap<String, Double>();
            
            String category;
            int count;
            
            for(Map.Entry<String, Integer> entry : wordStats.categoryCounts.entrySet()) {
            	//Category
                category = entry.getKey();
                //Number of time the category was found 
                count = entry.getValue();
                //Calculate the probability for the category
                //number of time category was found / total number of views
                knowledge.probabilityCat.put(category, Math.log((double)count/knowledge.getNumView()));
            }
        }
        //Previously found probabilities
        else {
            //set number of categories
            knowledge.setNumCat(categoryPriors.size());    
            
            String category;
            Double priorProbability;
            //For every category and it's probability
            for(Map.Entry<String, Double> entry : categoryPriors.entrySet()) {
                category = entry.getKey();
                priorProbability = entry.getValue();
                //Add the log of the probability to the knowledge
                knowledge.probabilityCat.put(category, Math.log(priorProbability));
            }
        }  
        //Estimate the total word occurrences in each category
        Map<String, Double> wordOccurrencesInCategory = new HashMap<>();
        Integer occurrences;
        Double TotalWordOccurences;
        //For each category in knowledge
        for(String category : knowledge.probabilityCat.keySet()) {
        	TotalWordOccurences = 0.0;
        	//for each category count for a words at wordStats.wordCategoryJointCoun.getKey()
            for(Map<String, Integer> categoryListOccurrences : wordStats.wordCategoryJointCount.values()) {
            	//Get the number of time a category in the knowledge appeared
                occurrences=categoryListOccurrences.get(category);
                if(occurrences!=null) {
                	//Increment the total number of word occurrences for a category
                	TotalWordOccurences = TotalWordOccurences + occurrences;
                }
            }
            //Add to the Map, the category and it's total word occurrences
            wordOccurrencesInCategory.put(category, TotalWordOccurences);
        }     
        //Estimate log likelihoods of a word being in a category
        String word;
        Integer count;
        Map<String, Integer> wordCategoryCounts;
        double logLikelihood;
        //For each category in the knowledge
        for(String category : knowledge.probabilityCat.keySet()) {
        	//For each category and word join 
            for(Map.Entry<String, Map<String, Integer>> entry : wordStats.wordCategoryJointCount.entrySet()) {
            	//Get the word
                word = entry.getKey();
                //Get the list of category and the number of time the word was in the category 
                wordCategoryCounts = entry.getValue();
                //Number of time the category in the knowledge matched a given word
                count = wordCategoryCounts.get(category);
                if(count==null) {
                    count = 0;
                }
                //Calculate the probability of the word being in that category
                //(Number of match+1)/(number of word occurrence for the given category + the total number of words)
                logLikelihood = Math.log((count+1.0)/(wordOccurrencesInCategory.get(category)+knowledge.getNumWords()));
                //If no previous likely hood create
                if(knowledge.probabilityLikelihoods.containsKey(word)==false) {
                    knowledge.probabilityLikelihoods.put(word, new HashMap<String, Double>());
                }
                //Assign likely hood for a given word
                knowledge.probabilityLikelihoods.get(word).put(category, logLikelihood);
            }
        }
        wordOccurrencesInCategory=null;
    }
    
    //Predict if document is in a given category
    public String predictCategory(String text) throws IllegalArgumentException {
        if(knowledge == null) {
        	System.out.println("Need knowledge");
            throw new IllegalArgumentException("Empty Knowledge"); 
        }
        
        //Get the data in the proper format
        Format format = MakeFormat.getFormat(text);
        Double probability;
        String word;
        Integer occurrences;
        String category;
        String predictedCategory = null;
        Double maxScore=Double.NEGATIVE_INFINITY;
        
        //for each category in the knowledge
        for(Map.Entry<String, Double> entry : knowledge.probabilityCat.entrySet()) {
        	//Get the category
            category = entry.getKey();
            //Get the probability of the category
            probability = entry.getValue(); 
            
            //For each word in the text
            for(Map.Entry<String, Integer> entry1 : format.getTokens().entrySet()) {
            	//Get the word
                word = entry1.getKey();
                //Check if the word is in the knowledge
                if(!knowledge.probabilityLikelihoods.containsKey(word)) {
                	//Start back the look to get next work                	
                    continue; 
                }
                //Get number of time the word appears in the text
                occurrences = entry1.getValue(); 
                //Add to the probability, the number of apperence in the text * the likelyhood of the word being in the given category
                probability += occurrences*knowledge.probabilityLikelihoods.get(word).get(category); 
            }
            //Only keep the highest probability for the text 
            if(probability>maxScore) {
                maxScore=probability;
                //Assign the highest probability score
                predictedCategory=category;
            }
        }    
        //Return the predicted category
        return predictedCategory; 
    }
}
