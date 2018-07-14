package com.adminportal.s3.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.adminportal.s3.service.S3Services;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3ServicesImpl implements S3Services {

	private Logger logger = LoggerFactory.getLogger(S3ServicesImpl.class);

	@Autowired(required = true)
	private AmazonS3 s3client;

	@Value("${jsa.s3.bucket}")
	private String bucketName;

	@Override
	public void uploadObjectWithPublicAccess(String keyName, String uploadFilePath) {

		try {
			File file = new File(uploadFilePath);
			s3client.putObject(new PutObjectRequest(bucketName, keyName, file).withCannedAcl(CannedAccessControlList.PublicRead));

			logger.info("File upload done for file: " + keyName + " from location: " + uploadFilePath);

		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException: ");
			logger.info("Error Message: " + ace.getMessage());
		}

	}

	@Override
	public void uploadProfileImageToS3(MultipartFile mFile, String fileName) {

		logger.info("**** Uploading profile Image to AWS ****** profileIamge Name: "+ fileName);
		try {
			File file = convertMultiPartToFile(mFile);
			uploadFileTos3bucket(fileName, file);
			file.delete();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	// S3 bucket uploading method requires File as a parameter,
	// but we have MultipartFile, so we need to add method which can make this
	// convertion.
	private File convertMultiPartToFile(MultipartFile file) throws IOException {

		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		
		s3client.putObject(new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
		
	}

	@Override
	public URL getObjectAccessibleUrl(String imageName) {
		return s3client.getUrl(bucketName, imageName);
	}
	
	
	/**
	 * Creating folder in S3 bucket with given FolderName
	 * 
	 * @param bucketName
	 * @param folderName
	 * @param s3client
	 */
	public void createFolderInS3Bucket(String bucketName, String folderName) {
		
		String SUFFIX = "/";
		
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(0);
		
		// Create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName+SUFFIX, emptyContent, objectMetadata);
		
		// send request to S3 to create folder
		s3client.putObject(putObjectRequest);
		
	}
	
	
	/**
	 * Uploading object to particular folder in S3
	 * 
	 * @param fileName
	 * @param file
	 * @param folderName
	 */
	@Override
	public void uploadProfileImageToFolderInS3(MultipartFile mFile, String fileName, String folderName) {
		String SUFFIX="/";
		
		String newFileName = folderName + SUFFIX + fileName;
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^ newFileName "+ newFileName);
		File file = null;
		try {
			file = convertMultiPartToFile(mFile);
		} catch (IOException e) {
			System.out.println("***** Error while converting multipart file to file");
			e.printStackTrace();
		}
		
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, newFileName, file);
		s3client.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead));

	}

}