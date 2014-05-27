package uk.co.javawork.svcs.download.web;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import uk.co.javawork.svcs.download.retrieve.DownloadInitResponse;
import akka.actor.ActorRef;
import akka.pattern.Patterns;

@Controller
public class RemoteFilesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFilesController.class);
	
	private final ActorRef manager;
	
	@Autowired
	public RemoteFilesController(ActorRef downloadManager){
		this.manager = downloadManager;
	}
	
	@Value("${storage-dir}")
	private String storagePath;
	
	@RequestMapping(value="/fetch", method={RequestMethod.GET})
	public String fetch(){
		//bounce to the fetch.html page
		return "/fetch";
	}

	@RequestMapping(value="/remote", method={RequestMethod.POST})
	public String storeRemoteFile(HttpServletRequest req, @RequestParam String url){
		
		String resp = null;
		
		final File storageDir = new File(storagePath);
		
		long size = 0L;
		String localFileName = "";
		
		try{
			
			final URL u = new URL(url);
			
			Future<Object> f = Patterns.ask(manager, u, 10000);
			
			DownloadInitResponse rspMsg = (DownloadInitResponse)Await.result(f, Duration.create(5, TimeUnit.SECONDS));
			size = rspMsg.getSize();
			localFileName = rspMsg.getLocalFileName();
			
			//set response attrs for use in view
			req.setAttribute("filesize", Long.toString(size));
			req.setAttribute("filename", localFileName);
			
			//respond to client
			resp = "/started";
			
			
		}catch(MalformedURLException e){
			
			req.setAttribute("errormsg", "Requested file is not a well formed URL.");
			
		}catch(Exception e){
			
			LOGGER.warn("No download init response for url " + url, e);
			req.setAttribute("errormsg", "Unable to initiate download: " + e.getMessage());
		}
		
		return resp;
	}
}
