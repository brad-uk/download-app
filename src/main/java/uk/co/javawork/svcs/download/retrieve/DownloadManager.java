package uk.co.javawork.svcs.download.retrieve;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static akka.actor.SupervisorStrategy.resume;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.springframework.web.socket.WebSocketSession;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.japi.Function;

public class DownloadManager extends UntypedActor {

	private final File storageDir;
	
	public DownloadManager(File storageDir){
		this.storageDir = storageDir;
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		
		if(msg instanceof URL){
			
			URL u = (URL)msg;
			Props p = Props.create(DownloadActor.class, storageDir);
			final String actorName = u.getPath().substring(u.getPath().lastIndexOf("/") + 1);
			ActorRef ref = getContext().actorOf(p, actorName);
			ref.forward(msg, getContext());
		}
		
		if(msg instanceof WebSocketInitRequest){
			WebSocketInitRequest req = (WebSocketInitRequest)msg;
			String fileName = req.getFilename();
			ActorSelection ref = getContext().actorSelection(fileName);
			ref.forward(msg, getContext());
		}
	}
	
	private static SupervisorStrategy strategy = new OneForOneStrategy(10,
			Duration.create("1 minute"), new Function<Throwable, Directive>() {
				@Override
				public Directive apply(Throwable t) {
					if (t instanceof IOException) {
						return stop();
					} else if (t instanceof NullPointerException) {
						return stop();
					} else {
						return escalate();
					}
				}
			});
     
    @Override
    public SupervisorStrategy supervisorStrategy() {
    	return strategy;
    }
}
