package com.infinity.client.models;

public class ReceivedFileModel extends SharedFileModel {
	
	/**
	 * The id of file indexed by server.
	 */
	private String id;
	
	/**
	 * The nick name who share the file.
	 */
	private String sharer;
	
	/**
	 * The ip address of sharer who share the file.
	 */
	private String ip;
	
	/**
	 * The port sharer used to receive request download file.
	 */
	private int commandPort;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSharer() {
		return sharer;
	}

	public void setSharer(String sharer) {
		this.sharer = sharer;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getCommandPort() {
		return commandPort;
	}

	public void setCommandPort(int commandPort) {
		this.commandPort = commandPort;
	}
	
}
