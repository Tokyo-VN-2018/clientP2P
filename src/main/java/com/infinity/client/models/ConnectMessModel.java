package com.infinity.client.models;

import java.util.ArrayList;
import java.util.List;

public class ConnectMessModel {
	
	private String status;
	
	private String username;
	
	private List<SharedFileModel> payload = new ArrayList<SharedFileModel>();
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<SharedFileModel> getPayload() {
		return payload;
	}
	public void setPayload(List<SharedFileModel> payload) {
		this.payload = payload;
	}
	public void addSharedFile(SharedFileModel sharedFile) {
		payload.add(sharedFile);
	}
	
	
}
