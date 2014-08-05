package uk.co.javawork.svcs.download.retrieve;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import akka.actor.UntypedActor;

public class WebsocketRelay extends UntypedActor {

	private static Logger LOGGER = LoggerFactory.getLogger(WebsocketRelay.class);
	
	private final WebSocketSession sess;
	
	public WebsocketRelay(WebSocketSession sess){
		this.sess = sess;
	}
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
		LOGGER.info("relay starting");
	}

	@Override
	public void onReceive(Object obj) throws Exception {

		LOGGER.info("relay received: " + obj);
		
		if(obj instanceof Integer){
			Integer i = (Integer)obj;
			TextMessage msg = new TextMessage(Integer.toString(i.intValue()));
			sess.sendMessage(msg);
		}
	}
}
