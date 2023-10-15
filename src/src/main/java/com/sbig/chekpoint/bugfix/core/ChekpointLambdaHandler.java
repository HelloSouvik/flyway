package com.sbig.chekpoint.bugfix.core;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesRequest;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.NetworkInterface;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ChekpointLambdaHandler implements RequestHandler<Object, ALBResponse> {
	String pattern = "yyyy.MM.dd G 'at' HH:mm:ss z";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String region = "ap-south-1"; // Replace with your desired region
    String vpcId = "vpc-096665cdfdff6a82a"; // Replace with your VPC ID
    String availabilityZone = "ap-south-1a"; // Replace with your desired availability zone
    String subnet_IN1A = "subnet-0a4696dc522788d01";
    String subnet_IN1B = "subnet-0ea6f3da899203422";
    String subnet_IN1C = "subnet-0c241e9dcfd433c15";
    
	public ALBResponse handleRequest(Object inputObject, Context context) {

		System.out.println("handleRequest started...");		
		String allCommaSeperatedIPs = populateIPs();
		
		ALBResponse response = createALBResponse(allCommaSeperatedIPs);
		return response;
	}
	
	private String populateIPs() {
		System.out.println("populateIPs started...");	
		String allCommaSeperatedIPs = "";
		AmazonEC2 ec2Client = AmazonEC2Client.builder()
                .withRegion(region)  // Replace YOUR_REGION with your desired AWS region
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
		// Create filters for VPC ID and availability zone
        Filter vpcFilter = new Filter("vpc-id", Arrays.asList(vpcId));
        Filter azFilter = new Filter("availability-zone", Arrays.asList(availabilityZone));
        Filter subnetFilter = new Filter("subnet-id", Arrays.asList(subnet_IN1A, subnet_IN1B, subnet_IN1C));
        
        DescribeNetworkInterfacesRequest request = new DescribeNetworkInterfacesRequest()
                .withFilters(vpcFilter, azFilter, subnetFilter);	
        // Describe network interfaces based on filters
        DescribeNetworkInterfacesResult result = ec2Client.describeNetworkInterfaces(request);
        Date attachDateTime = null;
        for (NetworkInterface networkInterface : result.getNetworkInterfaces()) {
            // Get and print all IP addresses associated with the ENI
        	attachDateTime = networkInterface.getAttachment().getAttachTime();
//        	TODO remove this tomorrow
        	if(attachDateTime!= null)
        		System.out.printf("IP is %s and created at: %s", networkInterface.getPrivateIpAddress(), simpleDateFormat.format(attachDateTime));
        	System.out.println();
//            System.out.println("IP Address: " + networkInterface.getPrivateIpAddress());
            if(allCommaSeperatedIPs.length() == 0) {
            	allCommaSeperatedIPs = networkInterface.getPrivateIpAddress(); 
            }else {
            	allCommaSeperatedIPs = allCommaSeperatedIPs + "," +networkInterface.getPrivateIpAddress();
            }
        }
        return allCommaSeperatedIPs;
	}
	
	
	private ALBResponse createALBResponse(String allCommaSeperatedIPs) {
		ALBResponse response = new ALBResponse();
        response.setStatusCode(200);
        response.setStatusDescription("OK");
        response.addHeader("Content-Type", "text/plain");
        response.setBody(allCommaSeperatedIPs);
        return response;
	}
}
