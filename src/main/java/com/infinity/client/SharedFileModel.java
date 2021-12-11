package com.infinity.client;

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
	 * The nick name who share the file.
	 */
	private String sharer;

	/**
	 * The checksum of the file.
	 */
	private String checksum;

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
	 * @param sharer    the nick name who share the file
	 * @param checksum  the checksum of the file
	 * @param size      the size in Byte of the file
	 */
	public SharedFileModel(String fileName, String filePath, String sharer, String checksum, long size) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.sharer = sharer;
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

	public String getSharer() {
		return sharer;
	}

	public void setSharer(String sharer) {
		this.sharer = sharer;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
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
		return String.format("SharedFile [FileName=%s, FilePath=%s, Sharer=%s, Checksum=%s, Size=%d Byte]", 
				new Object[] { fileName, filePath, sharer, checksum, size });
	}
}
