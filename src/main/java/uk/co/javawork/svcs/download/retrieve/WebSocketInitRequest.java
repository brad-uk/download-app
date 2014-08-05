package uk.co.javawork.svcs.download.retrieve;

import java.io.Serializable;

public class WebSocketInitRequest implements Serializable {

	private static final long serialVersionUID = 7940030147361737560L;
	
	private final String filename;
	
	public WebSocketInitRequest(String fileName){
		this.filename = fileName;
	}

	public String getFilename() {
		return filename;
	}
	
	@Override
	public String toString() {
		return "WebSocketInitRequest [filename=" + filename + "]";
	}
}
