package com.otppi;

import java.util.Base64;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;

public class UserValidator {

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
}
