package com.amazonaws.samples;

//import java.io.File;
//import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class ProcessDocument {
	
	public String document;
	
	public ProcessDocument() {
	}

	public String getDocument() {
		//AmazonS3Client credentials 
		//Could not load from disk => Had to hard code them
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("#########", "#########");       
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion("us-west-2")
                .build();
		
        //Assign bucket name and key of object
        String bucketName = "classification042018";
    	//model.ser for training of the whole dataset
    	//model_mdeium.ser for training of a 5000
    	//model-small.ser for training of 2000
        String key = "model_small.ser";
        //Get object from the bucket
        S3Object s3object = s3.getObject(new GetObjectRequest(bucketName, key));
        InputStream objectData = s3object.getObjectContent();
		String out = null;
		
		//Read the object
		try {	
			//Process with document in local file
	    	//model.ser for training of the whole dataset
	    	//model_mdeium.ser for training of a 5000
	    	//model-small.ser for training of 2000
        	//FileInputStream fi = new FileInputStream(new File("model_small.ser"));
			//ObjectInputStream oi = new ObjectInputStream(fi);
			//Process with document in cloud
        	ObjectInputStream oi = new ObjectInputStream(objectData);
        	Model model = (Model) oi.readObject();
        	oi.close();
        	System.out.println("Predicting");
			out = model.predictCategory(this.document);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		return out;
	}

	public void setDocument(String document) {
		this.document = document;
	}
	
	

}
