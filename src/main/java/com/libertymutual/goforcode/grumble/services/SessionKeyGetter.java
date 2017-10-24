package com.libertymutual.goforcode.grumble.services;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class SessionKeyGetter {
	
	public String getTheSessionKeyForRequest(HttpServletRequest request) throws IOException {
		StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line = "";
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    data = data.substring(15, 79);
		return data;
	}
}
