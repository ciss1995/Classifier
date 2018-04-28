package com.amazonaws.samples;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The NaiveBayesKnowledgeBase Object stores all the fields that the classifier
 * learns during training.
 * 
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 * @see <a href="http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/">http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/</a>
 */
public class Knowledge implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * number of training observations was n
     */
    private int numView=0;
    
    /**
     * number of categories was c
     */
    private int numCat=0;
    
    /**
     * number of features was d
     */
    private int numWords=0;
    
    /**
     * log priors for log( P(c) ) logPriors
     */
    public Map<String, Double> probabilityCat = new HashMap<>();
    
    /**
     * log likelihood for log( P(x|c) ) logLikelihoods
     */
    public Map<String, Map<String, Double>> probabilityLikelihoods = new HashMap<>();

	public int getNumView() {
		return numView;
	}

	public void setNumView(int numView) {
		this.numView = numView;
	}

	public int getNumCat() {
		return numCat;
	}

	public void setNumCat(int numCat) {
		this.numCat = numCat;
	}

	public int getNumWords() {
		return numWords;
	}

	public void setNumWords(int numWords) {
		this.numWords = numWords;
	}

	/*public Map<String, Double> getProbabilityCat() {
		return probabilityCat;
	}

	public void setProbabilityCat(Map<String, Double> logPriors) {
		this.probabilityCat = logPriors;
	}

	public Map<String, Map<String, Double>> getLogLikelihoods() {
		return probabilityLikelihoods;
	}

	public void setLogLikelihoods(Map<String, Map<String, Double>> logLikelihoods) {
		this.probabilityLikelihoods = logLikelihoods;
	}*/
    
    
}
