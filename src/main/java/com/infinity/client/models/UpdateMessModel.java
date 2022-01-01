package com.infinity.client.models;

import java.util.ArrayList;
import java.util.List;

public class UpdateMessModel {

	private String status;
	
	private List<SharedFileModel> payload = new ArrayList<SharedFileModel>();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<SharedFileModel> getPayload() {
		return payload;
	}

	public void setPayload(List<SharedFileModel> payload) {
		this.payload = payload;
	}
	
	
}
