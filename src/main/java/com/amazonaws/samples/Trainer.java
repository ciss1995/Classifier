package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class Trainer {

	public static void main(String[] args) {
		//Set aws 
		AWSCredentials credentials = null;
		//Get the credentials
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (/Users/cheickcisse/.aws/credentials), and is in valid format.",
                    e);
        }
		//Create an instance of S3 (Cloud storage)
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("us-west-2")
                .build();

        //Assign bucket name 
        String bucketName = "classification042018";
        //Key for the dataset to train 
        String key = "shuffled-full-set-hashed.csv";
        //Get the object from the bucket 
        S3Object s3object = s3.getObject(new GetObjectRequest(bucketName, key));
        //Read file 
        BufferedReader reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
        
        //Create a Map to get data from file
        Map<String, ArrayList<String>> dataset = new HashMap<String, ArrayList<String>>();
        int count = 0;
        String line;
        try {
        	//For website, only train 6000 -> Because of Timeout 
        	//Otherwise, train without limit -> knowledge2
        	//count < 2000 for model-small
        	//count < 5000 for model_medium
        	//Not count => model
			while( ((line = reader.readLine()) != null)) {	
				//Get the title of the document
				String SplitArr [] = line.split(",") ;
				//Check if there is data
				if (SplitArr.length == 2){
					//Print Data
					System.out.print(count);
					System.out.print("  " +SplitArr[0] );
					System.out.println( "->"+ SplitArr[1]);
					//Check if the category was already added
					if (dataset.containsKey(SplitArr[0])){
						//Add data to category
						dataset.get(SplitArr[0]).add(SplitArr[1]);
					}else {
						//Create a new ArrayList for the tokens of a new category
						ArrayList<String> str = new ArrayList<String>();
						//Add tokens
						str.add(SplitArr[1]);
						//Create map with category as key
						dataset.put(SplitArr[0], str);
					}
					count++;
				}
			}
		} catch (IOException e) {
			System.out.print("Error ");
			e.printStackTrace();
		}
        
        //loading examples in memory
        Map<String, String[]> trainingData = new HashMap<>();
        for(Map.Entry<String, ArrayList<String>> entry : dataset.entrySet()) {
            trainingData.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
        }
        
        //create an instance of the classifier 
        Model model = new Model();
        //Set the Chi Square
        /*Selection method 
         * Test if the occurrence of a specific term and the occurrence of a class are independent
         * We then only select the features with the highest result
         * => Remove unnecessary features
         */
        model.setChisquareCriticalValue(6.83); //0.01 pvalue
        //Train the data
        model.train(trainingData, null);
        //Create an instance of knowledge with the trained data
        Knowledge knowledge = model.getKnowledge();
        // Create a new instance of the classifier with the knowledge
        model = new Model(knowledge);
        //Save the model locally and in the aws cloud
        try {     
        	//model.ser for training of the whole dataset
        	//model_mdeium.ser for training of a 5000
        	//model-small.ser for training of 2000
        	File f = new File("model.ser");
            FileOutputStream fos = new FileOutputStream(f);
        	ObjectOutputStream oos = new ObjectOutputStream(fos);
        	oos.writeObject(model);
        	oos.close();  	
        	//Add model to the databse
        	//model.ser for training of the whole dataset
        	//model_mdeium.ser for training of a 5000
        	//model-small.ser for training of 2000
        	s3.putObject(new PutObjectRequest(bucketName, "model.ser", f));
        }  catch (IOException e) {
			e.printStackTrace();
		}
	}

}
