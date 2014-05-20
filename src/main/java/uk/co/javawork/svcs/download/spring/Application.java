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
	
	@Value("${storage-path}")
	private String storagePath;
	
	@Bean
	public ActorSystem actorSys(){
		return ActorSystem.create("sys");
	}
	
	@Bean
	public ActorRef downloadManager(ActorSystem sys){
		File storageDir = new File(storagePath);
		return sys.actorOf(Props.create(DownloadManager.class, storageDir), "download-manager");
	}
	
	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}
}
