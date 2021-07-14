package com.otppi;

import java.io.FileNotFoundException;

public class App {


	public static void main(String[] args)
	{
		try {
			Users.load(".htpasswd");
			WebSocketServerImpl server = new WebSocketServerImpl(8890);
			server.start();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
}
