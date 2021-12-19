package com.infinity.client.models;

public class GetSharerInfoMessModel {

	private String status;
	
	private SharedFileModel payload;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public SharedFileModel getPayload() {
		return payload;
	}

	public void setPayload(SharedFileModel payload) {
		this.payload = payload;
	}
	
	
}
