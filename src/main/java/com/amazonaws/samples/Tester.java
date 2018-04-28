package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class Tester {

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
       
       
        //Get the data from the bucket 
        S3Object s3object = s3.getObject(new GetObjectRequest(bucketName, key));
        //Read file 
        BufferedReader reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
        
        //Storage for features and categories
        ArrayList<String> testing = new ArrayList<String>();
        ArrayList<String> testingCat = new ArrayList<String>();
        
        //Counter for the number of data to test
        int count = 0;
        
        String line;
        try {
			while(((line = reader.readLine()) != null) && count < 100) {
				//Get the title of the document
				String SplitArr [] = line.split(",") ;
				//Check if there is data
				if (SplitArr.length == 2){
					//Get the features
					testing.add(SplitArr[1]);
					//Get the categories
					testingCat.add(SplitArr[0]);
					count++;
				}
			}
		} catch (IOException e) {
			System.out.print("Error ");
			e.printStackTrace();
		}
        
        //Test the model locally
        for (int i = 0; i < testing.size(); i++) {
        ProcessDocument pd = new ProcessDocument();
        //pd.setDocument("c99bb13b5e17 754980538d32 cafaf222091d 415e3c4bcd07 1c62a7ef6714 efb153cbfa1f 422068f04236 cbd7cf0d1dd2 30ca33997a38 133d46f7ed38 5892ad716bb7 d38820625542 094453b4e4ae 427028e08976 0efaf9ea8493 8b1b940909d6 4a4450563039 6f0a43edaab2 8b1b940909d6 80db64375ff4 b4fcf37f996c 4a4450563039 93a5aefea103 6f0a43edaab2 ee7804cde029 8ce4d4a3c25e 3b952c633ee4 7396ced735de 33745e680c9e 0c4ce226d9fe 142d16db61dd 8ce4d4a3c25e 7396ced735de 6365c4563bd1 0c4ce226d9fe 845c5d0ccbf8 cfd22ba194a9 4ba791167817 30f6ec5d2c88 d1e8788f7d3a e97fa11a8129 e4bcfb5f9502 8f75273e5510 ba47b8a02a0e 236eeaf59a18 6ce6cc5a3203 6ba71b05d9b4 a569ce5b0dc2 df066942c9fb e63fa74d3c8b 31fdb28ad1d7 6365c4563bd1 fe081ae57a8b 3bf2531eae08 a0020c78593b 3463cc7cb4db fe33912c5732 04503bc22789 f76d35ebd055 360e8b28421c 5a5c729507ea 5c02c2aaa67b 21ec4e7c3624 c64a32416910 ddf4525e90e3 d8afd84c6fa9 a09e0a748699 da61efdd2b77 6ce6cc5a3203 5ff4125e9d7b 9a6abe0393e2 6ce6cc5a3203 a86487150d57 e1938ec2418d 5a5c729507ea f10f196b0752 f7ae6f8257da f52671c3d5de 586242498a88 a84e4745160d a1bb6b4223d9 8f09a81591d3 d8535c18626a 633c472ba3a1 81a204399a04 f22255ebf012 66015316b49a f95d0bea231b 108b1614f4a6 26f768da5068 6af770640118 aa07c87e9ec6 b5af8a9b1fe3 66015316b49a 8c2bce2724af ee531db4bbcb 8f09a81591d3 4b8a49e51a5d 846bf2cc3856 a5ed762996a1 e128bc6e369d 36e7aa72ffe1 e43c4b6f2c61 b32153b8b30c d38820625542 ce68d85c1b08 d8535c18626a 81e9bccc0548 6bf9c0cb01b4 0c4ce226d9fe b32153b8b30c e7f10ad56136 6bf9c0cb01b4 0562c756a2f2 4ad52689d690 0c4ce226d9fe b32153b8b30c 0562c756a2f2 3d442094a919 26f768da5068 6af770640118 77a64531e8e5 c238182b19f5 77a64531e8e5 f0666bdbc8a5 b9699ce57810 b5558a4d9fc2 845c5d0ccbf8 f7857fc73180 2dd786866fc1 a8df78439fe6 6ca2dd348663 0570a65be98f d2e04a08263d 395a5e8185f8 afcbceb9a345 9b9fbf583f15 a1e5904d04f5 d38820625542 3ae9a040a80b fdebb678ec29 33a2d38b97ed 6d10c76d455a 7498a1e9d9d7 ac624879ac84 957b5cf4e65e ec3406979928 e4dad7cb07b6 5c2db045bc17 d38820625542 7846c4b0c98a d8535c18626a 4b8ab5da6183 28bce73d3237 a3cb110635ab 4b1b19d4c756 eecc7a0ddf4a b56aab080e2b b208ae1e8232 b7ab56536ec4 422068f04236 bf064c332aa1 f53ea1abbc15 056314258a60 cafaf222091d 7dd24f99f4ba fbc4d37bf4cc be95012ebf2b 83d86d05f70d 0747759a1871 73118d178ab8 036087ac04f9 f95d0bea231b 2bcce4e05d9d 2ef7c27a5df4 944210cd5000 6ca2dd348663 327f094890d6 2d8be57ed4b6 133d46f7ed38 31fd3123f41c 2d00e7e4d33f 4ba791167817 e7aa4ec47132 7d9e333a86da 95293dff9e86 4928c68aa3f7 5a5c729507ea 3b952c633ee4 bfb030c0e4e2 fee490da6850 6b5a677b4af2 a1bb6b4223d9 6101ed18e42f fe33912c5732 61bc26b854a5 fe09a5b9f7bc 5a5c729507ea 36490331ef85 65f888439937 7d16bc01dce6 f7ae6f8257da 6365c4563bd1 586242498a88 b104c5d62c6d 5d07baa4b07c 6dd255113131 c6305ec85f2e 85bf198ebad1 ad4440ac97a5 4ad52689d690 0c4ce226d9fe 041a934b1778 99e613bf119e 4ad52689d690 0c4ce226d9fe b32153b8b30c 2257c7f14f26 c5dcd74b40a9 28bce73d3237 8f75273e5510 ba47b8a02a0e 586242498a88 5c960d5545d5 2be8ace3819e 132a682c6a03 5a35c269cc03 7fdfab6f73ae 5ce7afc3a0e0 cb7631b88e51 e54915add605 d2fed0e65ee8 cc03a8691f3f 1918bab7ef7f 0a6eb038c015 76d13a346c76 7fdfab6f73ae cb7631b88e51 6580222a078b 28bce73d3237 c05d63120509 c2cdeb00179d 6365c4563bd1 aeadb65f0f87 77067130a670 dd9ec1375214 5d00ab650ac2 6b304aabdcee 8f75273e5510 360e8b28421c a7625fc4baba 6365c4563bd1 27143f7f5d10 427028e08976 5b9537791a91 7e996d2f7aa7 bfc18ff2b989 a79dcd219659 570ffaa68b16 24e1d1aefa6f f76533ec75cf 3397db22bc41 6f6fb5a7797f e6b4e4052b20 6ca2dd348663 f1413affa34b 7963d7b66b32 b208ae1e8232 a86f2ba617ec 5b9537791a91 d2fed0e65ee8 8a3fc46e34c1 6ecaeff9cf3b 1918bab7ef7f 21ab107e9310 b4df27a3fadb 1918bab7ef7f 494c486bfa24 e5c361720599 6f5b809e6812 26f768da5068 6af770640118 8f75273e5510 ed5d3a65ee2d 90c0111527fb 9fc097bdc653 3473374a8786 6af770640118 2aab9b5a143d b9699ce57810 28bce73d3237 321a86f29959 288ccf089872 21ab107e9310 6d10c76d455a 2ce0277ae4e0 442af4566619 fb57719be1cf 824957f25ea0 9e5e8a7bd1d6 744366456381 28bce73d3237 816aed74475e 288ccf089872 04de19ba9fd7 52db4acf276f f95d0bea231b 5c02c2aaa67b f79da29e041c 3689f1b1fc46 507abf029e97 e2b01cd25833 133d46f7ed38 754980538d32 28bce73d3237 10e45001c2f2 3689f1b1fc46 22fa1184be26 b7ab56536ec4 6ce6cc5a3203 4ac8853e4d7e 816aed74475e 6a01047db3ab 45e16cbae38f 557ec6c63cf9 38ab8a760603 0f1d041f5921 5be138559904 b8d839147e21 6f5b809e6812 a9ee836e8303 6365c4563bd1 88086c92524c f79da29e041c 85bf198ebad1 af1be941b064 85bf198ebad1 b1b3097c0307 1569d05bdcf0 6365c4563bd1 17bc3e95a38f 0f1d041f5921 c337a85b8ef9 73ee90b756c1 0a6eb038c015 5e99d31d8fa4\n");
        pd.setDocument(testing.get(i));
        System.out.println(testing.get(i));
        String d = pd.getDocument();
        System.out.println("Category should be "+testingCat.get(i)+". The model says: "+d);
        }
	}
        

}
