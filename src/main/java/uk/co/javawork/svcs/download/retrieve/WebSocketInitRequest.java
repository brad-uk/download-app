package uk.co.javawork.svcs.download.retrieve;

import org.springframework.web.socket.WebSocketSession;

public class WebSocketInitRequest {

	private final String filename;
	private final WebSocketSession session;
	
	public WebSocketInitRequest(String fileName, WebSocketSession session){
		this.filename = fileName;
		this.session = session;
	}

	public String getFilename() {
		return filename;
	}

	public WebSocketSession getSession() {
		return session;
	}
}
