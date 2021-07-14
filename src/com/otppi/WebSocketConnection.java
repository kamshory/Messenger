package com.otppi;

import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public class WebSocketConnection {

	private WebSocket conn;
	private ClientHandshake request;
	private String path = "/";
	private String sessionID = System.nanoTime()+"";
	private String channel = "";

	public WebSocketConnection(WebSocket conn, ClientHandshake request) {
		this.conn = conn;
		this.request = request;
		this.processRequest(request);
	}

	private void processRequest(ClientHandshake request) {
		String requestPath = request.getResourceDescriptor();
		if(requestPath.contains("?"))
		{
			String[] arr = requestPath.split("\\?", 2);
			if(arr.length > 1)
			{
				Map<String, String> query = Users.parseQueryPairs(arr[1]);
				if(query.containsKey("path"))
				{
					this.path = query.getOrDefault("path", "");
				}
				if(query.containsKey("channel"))
				{
					this.channel = query.getOrDefault("channel", "");
				}
			}
		}
	}

	public void send(String message) {
		this.conn.send(message);
		
	}

	public WebSocket getConn() {
		return conn;
	}

	public void setConn(WebSocket conn) {
		this.conn = conn;
	}

	public ClientHandshake getRequest() {
		return request;
	}

	public void setRequest(ClientHandshake request) {
		this.request = request;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
