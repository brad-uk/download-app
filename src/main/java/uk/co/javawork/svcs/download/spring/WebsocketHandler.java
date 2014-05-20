package uk.co.javawork.svcs.download.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import uk.co.javawork.svcs.download.retrieve.WebSocketInitRequest;
import akka.actor.ActorRef;

public class WebsocketHandler extends TextWebSocketHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketHandler.class);

	private final ActorRef manager;
	
	public WebsocketHandler(ActorRef downloadManager){
		this.manager = downloadManager;
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		// TODO Auto-generated method stub
		super.afterConnectionClosed(session, status);
	}

	@Override
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		// TODO Auto-generated method stub
		super.handleTransportError(session, exception);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		LOGGER.debug("websocket got text msg: " + message.getPayload());
		String fileName = message.getPayload();
		WebSocketInitRequest msg = new WebSocketInitRequest(fileName, session);
		manager.tell(msg, null);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		LOGGER.debug("wbesock connection established");
		session.sendMessage(new TextMessage("0"));
	}
}
