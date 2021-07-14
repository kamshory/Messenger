package com.otppi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;

public class Users {
	private static Map<String, String> userList = new HashMap<>();
	private static long lastLoad = 0;
	private static long loadInterval = 30000;
	private static String file = "";
	
	private Users()
	{
		
	}
	public static void load(String file) throws FileNotFoundException
	{
		Users.file = file;
		loadUser();
	}
	public static boolean isValidUser(String username, String password) throws InvalidClientException
	{
		long currentTime = System.currentTimeMillis();
		if(!userList.containsKey(username) && lastLoad < currentTime - loadInterval)
		{
			try 
			{
				loadUser();
			} 
			catch (FileNotFoundException e) {
				throw new InvalidClientException(e.getMessage());
			}
		}
		if(userList.containsKey(username))
		{
			String hash = userList.get(username);
			return isPasswordValid(password, hash);
		}
		return false;
	}
	
	public static boolean isPasswordValid(String password, String hash) 
	{
	    if (hash.startsWith("$apr1$")) {
	      return hash.equals(Md5Crypt.apr1Crypt(password, hash));
	    } else if (hash.startsWith("$1$")) {
	      return hash.equals(Md5Crypt.md5Crypt(password.getBytes(Charsets.UTF_8), hash));
	    } else if (hash.startsWith("{SHA}")) {
	      return hash.substring(5).equals(Base64.getEncoder().encodeToString(DigestUtils.sha1(password)));
	    } else if (hash.startsWith("$2y$")) {
	      // bcrypt not supported currently
	      return false;
	    } else {
	      return hash.equals(password);
	    }
	  }
	
	private static void loadUser() throws FileNotFoundException {
			byte[] data = read(Users.file);
			if(data != null)
			{
				String str = new String(data, StandardCharsets.UTF_8);
				str = fixingRawData(str);
				String[] arr = str.split("\r\n");
				Users.userList = new HashMap<>();
				for(int i = 0; i<arr.length; i++)
				{
					String line = arr[i].trim();
					if(line.contains(":"))
					{
						String[] arr2 = line.split("\\:", 2);
						Users.userList.put(arr2[0], arr2[1]);
					}
				}
			}
		
	}

	public static String fixingRawData(String result)
	{
		result = result.replace("\n", "\r\n");
		result = result.replace("\r\r\n", "\r\n");
		result = result.replace("\r", "\r\n");
		result = result.replace("\r\n\n", "\r\n");
		return result;
	}
	public static byte[] read(String fileName) throws FileNotFoundException
	{
		byte[] allBytes = null;
		try 
		(
				InputStream inputStream = new FileInputStream(fileName);
		) 
		{
			File resource = new File(fileName);		
			long fileSize = resource.length();
			allBytes = new byte[(int) fileSize];
			int length = inputStream.read(allBytes);
			if(length == 0)
			{
				allBytes = null;
			}
		 } 
		 catch (IOException ex) 
		 {
			 throw new FileNotFoundException(ex.getMessage());
		 }
		 return allBytes;
	}
	public static boolean checkUserAuth(String username, String password) throws InvalidClientException {
		return Users.isValidUser(username, password);
	}
	public static Map<String, String> parseQueryPairs(String data)
	{
		Map<String, String> queryPairs = new LinkedHashMap<>();
		String[] pairs = data.split("&");
		int index = 0;
	    for (String pair : pairs) 
	    {
	    	if(pair.contains("="))
	    	{
		        int idx = pair.indexOf("=");
		        try 
		        {
		        	String key = fixURLEncodeKey(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), index);
		        	String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
					queryPairs.put(key, value);
				} 
		        catch (UnsupportedEncodingException e) 
		        {
					e.printStackTrace();
				}
		        index++;
	    	}
	    }
		return queryPairs;
	}
	

	private static String fixURLEncodeKey(String key, int index) 
	{
		return key.replace("[]", "["+index+"]");
	}

	public static List<String> asList(String input) 
	{
		List<String> list = new ArrayList<>();
		list.add(input);
		return list;
	}
}
