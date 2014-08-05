package uk.co.javawork.svcs.download.retrieve;

import java.io.Serializable;

public class DownloadInitResponse implements Serializable  {

	private final long size;
	private final String localFileName;
	
	public DownloadInitResponse(String localFileName, long size){
		this.localFileName = localFileName;
		this.size = size;
	}

	public long getSize() {
		return size;
	}

	public String getLocalFileName() {
		return localFileName;
	}
}
