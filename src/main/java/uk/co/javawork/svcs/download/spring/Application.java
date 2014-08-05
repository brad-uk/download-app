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
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSingletonManager;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@EnableAutoConfiguration
@ComponentScan({"uk.co.javawork.svcs.download.spring", "uk.co.javawork.svcs.download.web"})
public class Application {
	
	@Value("${tmp-dir}")
	private String tmpDirPath;	
	
	@Value("${storage-dir}")
	private String storageDirPath;
	
	@Bean
	public ActorSystem actorSys(){
		
		
		Config clusterConfig = ConfigFactory.load();
		
		ActorSystem sys =  ActorSystem.create("ClusterSystem", clusterConfig);
		
		File tmpDir = new File(tmpDirPath);
		File storageDir = new File(storageDirPath);
		
		Props managerProps = Props.create(DownloadManager.class, tmpDir, storageDir);
		Props clusterSingletonProps = ClusterSingletonManager.defaultProps(managerProps, 
																	"manager", 
																	PoisonPill.getInstance(), 
																	"manager-role");
		ActorRef manager = sys.actorOf(clusterSingletonProps, "singleton");
		
		return sys;
	}
	
	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}
}
