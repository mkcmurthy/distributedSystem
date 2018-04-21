package com.project1;

public class ServerDetails {

	private String ipAddress;
	private String service;
	private int port;
	
	public ServerDetails(String ipAddress, String service, int port) {
		
		this.ipAddress = ipAddress;
		this.service = service;
		this.port = port;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getService() {
		return service;
	}

	public int getPort() {
		return port;
	}
	
	
	
	
}
