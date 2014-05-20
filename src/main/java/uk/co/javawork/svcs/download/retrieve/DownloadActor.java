package uk.co.javawork.svcs.download.retrieve;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import akka.actor.UntypedActor;

public class DownloadActor extends UntypedActor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadActor.class);
	
	private static final int BUFFER_SIZE = 1024 * 8;

	private final File storageDir;
	
	private File localFile;
	private URLConnection conn;
	private long size;
	
	public DownloadActor(File storageDir){
		this.storageDir = storageDir;
	}

	@Override
	public void onReceive(Object msg) throws Exception {

		if(msg instanceof URL){

			URL u = (URL)msg;
			
			LOGGER.info("Download remote file: " + u.toString());

			initDownload(u);
			DownloadInitResponse resp = new DownloadInitResponse(localFile.getName(), size);
			getSender().tell(resp, self());
		}
		
		if(msg instanceof WebSocketInitRequest){
			
			WebSocketInitRequest req = (WebSocketInitRequest)msg;
			WebSocketSession session = req.getSession();
			
			doDownload(size, session);
			
			LOGGER.info("download complete, stopping...");
			
			getContext().stop(getSelf());
		}
	}
	
	private void initDownload(URL u) throws IOException {
		
		String sf = u.getFile();
		String name = sf.substring(sf.lastIndexOf("/") + 1);
		name= name.replaceAll("%20", "-");
		
		localFile = new File(storageDir, name);
		
		if(localFile.exists() && !localFile.canWrite()){
			throw new IOException("Unable to overwrite existing local file " + localFile.getPath());
		}
		
		conn = u.openConnection();
		size = conn.getContentLengthLong();
	}
	
	private void doDownload(long targetSize, WebSocketSession session) throws IOException{
		
		try(InputStream in = conn.getInputStream()){
			
			try(OutputStream fOut = new FileOutputStream(localFile)){
			
				byte[] buff = new byte[BUFFER_SIZE];
				long total = 0;
				int readSize = 0;
				int currPercent = 0;
				
				while((readSize = in.read(buff)) > -1){
					
					fOut.write(buff, 0, readSize);
					total += readSize;
					
					if(session != null && session.isOpen()){
						
						float percent = (total * 100) / targetSize;
						int m = (int)percent;
						
						if(m > currPercent){
							currPercent = m;
							TextMessage msg = new TextMessage(Integer.toString(m));
							session.sendMessage(msg);
						}
					}
				}
				
				if(LOGGER.isInfoEnabled()){
					LOGGER.info("Download complete, size copied: " + total);
				}
			}
		}
	}
	
	private void httpDownload(URI target) throws IOException {
		
		Request.Get(target).connectTimeout(5000).socketTimeout(5000).execute().saveContent(localFile);
	}
	
	@Override
	public void postStop() throws Exception {
		LOGGER.info("download actor stopping");
	}
	
	@Override
	public void preStart() throws Exception {
		LOGGER.info("download actor starting");
	}
}
