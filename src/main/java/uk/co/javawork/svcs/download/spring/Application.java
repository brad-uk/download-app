package uk.co.javawork.svcs.download.spring;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import uk.co.javawork.svcs.download.retrieve.DownloadManager;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

@EnableAutoConfiguration
@ComponentScan({"uk.co.javawork.svcs.download.spring", "uk.co.javawork.svcs.download.web"})
public class Application {
	
	@Value("${tmp-dir}")
	private String tmpDirPath;	
	
	@Value("${storage-dir}")
	private String storageDirPath;
	
	@Bean
	public ActorSystem actorSys(){
		return ActorSystem.create("sys");
	}
	
	@Bean
	public ActorRef downloadManager(ActorSystem sys){
		
		File tmpDir = new File(tmpDirPath);
		File storageDir = new File(storageDirPath);
		
		return sys.actorOf(Props.create(DownloadManager.class, tmpDir, storageDir), "download-manager");
	}
	
	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}
}
