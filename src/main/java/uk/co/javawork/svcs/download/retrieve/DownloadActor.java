package uk.co.javawork.svcs.download.retrieve;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class DownloadActor extends UntypedActor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadActor.class);
	
	private static final int BUFFER_SIZE = 1024 * 8;

	private final File tmpDir;
	private final File storageDir;
	
	private File tmpFile;
	private File completeFile;
	
	private URLConnection conn;
	private long size;
	
	public DownloadActor(File tmpDir, File storageDir){
		this.tmpDir = tmpDir;
		this.storageDir = storageDir;
	}

	@Override
	public void onReceive(Object msg) throws Exception {

		LOGGER.debug("download actor received: " + msg);
		
		if(msg instanceof URL){

			URL u = (URL)msg;
			
			LOGGER.info("Download remote file: " + u.toString());

			initDownload(u);
			DownloadInitResponse resp = new DownloadInitResponse(tmpFile.getName(), size);
			getSender().tell(resp, self());
		}
		
		if(msg instanceof WebSocketInitRequest){
			
			WebSocketInitRequest req = (WebSocketInitRequest)msg;
			String fileName = req.getFilename();
			
			LOGGER.debug("doDownload...");
			
			ActorRef wsSender = getSender();
			doDownload(size, wsSender);
			
			LOGGER.info("download complete, stopping...");
			
			getContext().stop(getSelf());
		}
	}
	
	private void initDownload(URL u) throws IOException {
		
		LOGGER.debug("init download: " + u.getPath());
		
		String sf = u.getFile();
		String name = sf.substring(sf.lastIndexOf("/") + 1);
		name= name.replaceAll("%20", "-");
		
		tmpFile = new File(tmpDir, name);
		completeFile = new File(storageDir, name);
		
		if(tmpFile.exists() && !tmpFile.canWrite()){
			throw new IOException("Unable to overwrite existing tmp file " + tmpFile.getPath());
		}
		
		conn = u.openConnection();
		size = conn.getContentLengthLong();
	}
	
	private void doDownload(long targetSize, ActorRef wsSender) throws IOException{
		
		long total = 0;
		int readSize = 0;
		int currPercent = 0;
		
		try(InputStream in = conn.getInputStream()){
			
			try(OutputStream fOut = new FileOutputStream(tmpFile)){
				
				LOGGER.debug("streaming download data");
			
				byte[] buff = new byte[BUFFER_SIZE];
				
				while((readSize = in.read(buff)) > -1){
					
					fOut.write(buff, 0, readSize);
					total += readSize;
					
					if(wsSender != null){
						
						float percent = (total * 100) / targetSize;
						int m = (int)percent;
						
						if(m > currPercent){
							currPercent = m;
							//TextMessage msg = new TextMessage(Integer.toString(m));
							Integer msg = Integer.valueOf(m);
							wsSender.tell(msg, getSelf());
						}
					}
				}
			}
		}
		
		Path source = tmpFile.toPath();
		Path target = completeFile.toPath();
		Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
		
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("Download complete, size copied: " + total);
		}
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
