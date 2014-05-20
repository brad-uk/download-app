package uk.co.javawork.svcs.download.retrieve;

public class DownloadInitResponse {

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
