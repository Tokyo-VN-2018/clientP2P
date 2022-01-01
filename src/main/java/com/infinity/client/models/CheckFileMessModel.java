package com.infinity.client.models;

public class CheckFileMessModel {

	private String status;
	
	private int listeningPort;
	
	private SharedFileModel payload;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getListeningPort() {
		return listeningPort;
	}

	public void setListeningPort(int listeningPort) {
		this.listeningPort = listeningPort;
	}

	public SharedFileModel getPayload() {
		return payload;
	}

	public void setPayload(SharedFileModel payload) {
		this.payload = payload;
	}
	
	
}
