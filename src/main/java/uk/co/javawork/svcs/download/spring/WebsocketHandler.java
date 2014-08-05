package uk.co.javawork.svcs.download.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import uk.co.javawork.svcs.download.retrieve.WebSocketInitRequest;
import uk.co.javawork.svcs.download.retrieve.WebsocketRelay;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSingletonProxy;

public class WebsocketHandler extends TextWebSocketHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketHandler.class);

	private final ActorSystem sys;
	private final ActorRef singletonProxy;
	
	public WebsocketHandler(ActorSystem sys){
		this.sys = sys;
		Props singletonProps = ClusterSingletonProxy.defaultProps("/user/singleton/manager", "manager-role");
		singletonProxy = sys.actorOf(singletonProps, "proxy");
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session,
										CloseStatus status) throws Exception {

		super.afterConnectionClosed(session, status);
	}

	@Override
	public void handleTransportError(WebSocketSession session,
										Throwable exception) throws Exception {

		super.handleTransportError(session, exception);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		LOGGER.debug("websocket got text msg: " + message.getPayload());
		
		String fileName = message.getPayload();
		WebSocketInitRequest msg = new WebSocketInitRequest(fileName);
		ActorRef relay = sys.actorOf(Props.create(WebsocketRelay.class, session), fileName);
		try{
			singletonProxy.tell(msg, relay);
		}catch(Throwable t){
			LOGGER.error("Unable to send websocket init request to download manager", t);
			throw t;
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		LOGGER.debug("websocket connection established");
		session.sendMessage(new TextMessage("0"));
	}
}
