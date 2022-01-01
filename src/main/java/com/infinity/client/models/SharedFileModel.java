package com.infinity.client.models;

public class SharedFileModel {
	/**
	 * The name of the file.
	 */
	private String fileName;
	
	/**
	 * The path of the file.
	 */
	private String filePath;

	/**
	 * The checksum of the file.
	 */
	private long checksum;

	/**
	 * The size in Byte of the file.
	 */
	private long size;
	
	/**
	 * The unique ID for serializing.
	 */
	private static final long serialVersionUID = -4459827249944645125L;
	
	/**
	 * Default constructor for FastJSON.
	 */
	public SharedFileModel() { }

	/**
	 * The constructor of this class.
	 * 
	 * @param fileName  the file name of the file
	 * @param filePath  the file path of the file
	 * @param checksum  the checksum of the file
	 * @param size      the size in Byte of the file
	 */
	public SharedFileModel(String fileName, String filePath, long checksum, long size) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.checksum = checksum;
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getChecksum() {
		return checksum;
	}

	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String toString() {
		return String.format("SharedFile [FileName=%s, FilePath=%s, Checksum=%s, Size=%d Byte]", 
				new Object[] { fileName, filePath, checksum, size });
	}
}
