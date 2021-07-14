package com.otppi;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WebSocketServerImpl extends WebSocketServer{

	private static Map<Integer, WebSocketConnection> clients = new HashMap<>();
	public WebSocketServerImpl(int port)
	{
		super(new InetSocketAddress(port));
	}
	public WebSocketServerImpl(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onClose(WebSocket conn, int code, String message, boolean arg3) {
		this.remove(conn);		
	}

	@Override
	public void onError(WebSocket conn, Exception e) {
		this.remove(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		/**
		 * Do nothing
		 */
	
		broadcastMessage(message, conn);
	}
	public String getChannel(WebSocket conn)
	{
		WebSocketConnection sender = this.getConnection(conn);	
		if(sender != null)
		{
			return sender.getChannel();
		}
		else
		{
			return "";
		}
	}
	public WebSocketConnection getConnection(WebSocket conn)
	{
		return clients.get(conn.hashCode());
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake request) {
		
		String authorization = request.getFieldValue("Authorization");

		if(authorization.startsWith("Basic "))
		{
			byte[] decodedBytes = Base64.getDecoder().decode(authorization.substring("Basic ".length()));
			if(decodedBytes != null)
			{
				String str = new String(decodedBytes, StandardCharsets.UTF_8);
				String[] arr = str.split("\\:");
				if(arr.length > 1)
				{
					String username = arr[0];
					String password = arr[1];
					try 
					{
						if(Users.checkUserAuth(username, password))
						{
							WebSocketServerImpl.addClient(conn, request);
						}
						else
						{
							conn.close();
						}
					} 
					catch (InvalidClientException e) 
					{
						e.printStackTrace();
						conn.close();
					}
				}
			}
		}
	}
	
	private static void addClient(WebSocket conn, ClientHandshake request) {
		Integer hashCode = conn.hashCode();
		WebSocketConnection arg1 = new WebSocketConnection(conn, request);
		clients.put(hashCode, arg1);
	}
	@Override
	public void onStart() {
		/**
		 * Do nothing
		 */
	}
	
	private void remove(WebSocket conn) {
		clients.remove(conn.hashCode());	
	}
	
	public static void broadcastMessage(String message)
	{
		for(Entry<Integer, WebSocketConnection> client : clients.entrySet())
		{
			client.getValue().send(message);
		}
	}
	
	public static void broadcastMessage(String message, String path)
	{
		for(Entry<Integer, WebSocketConnection> client : clients.entrySet())
		{
			if(client.getValue().getPath().contains(path))
			{
				client.getValue().send(message);
			}
		}
	}
	public static void broadcastMessage(String message, WebSocket sender)
	{
		for(Entry<Integer, WebSocketConnection> client : clients.entrySet())
		{
			if(client.getValue().hashCode() != sender.hashCode())
			{
				client.getValue().send(message);
			}
		}
	}

	
	
	
	
	

}
