package com.libertymutual.goforcode.grumble.models;

import java.util.ArrayList;
import java.util.List;

public class ImageApiCredentials {
	
	private List<String> cxKey;
	private List<String> apiKey;
	
	public ImageApiCredentials () {
		this.cxKey = new ArrayList<String>();
		this.cxKey.add("002392119250457641008:zovcx9rlbaw");//traces
		this.cxKey.add("008419413090451472490:9ehq9f7q5q8");//chads
		this.cxKey.add("012158523288957788709:gfordprffsk");//matts
		this.cxKey.add("008537554225092905589:gyawgeqjvfa");//ajs
		this.cxKey.add("001341372088491780835:xklaigccswu");//jds
		this.apiKey = new ArrayList<String>();
		this.apiKey.add("AIzaSyCPEZNXOBI9ZfcEzcEZfDjexTysIHeaScU");//traces
		this.apiKey.add("AIzaSyBR1FQBAgE0KiZLS2eYLuer3r992uxJUSo");//chads
		this.apiKey.add("AIzaSyByWFw32gK2h8UglFKX3ctHy0eLqsI4UBU");//matts
		this.apiKey.add("AIzaSyBB85hIi6ZmVPAqvDYqar88SzN2uOEhoIc");//ajs
		this.apiKey.add("AIzaSyAnK5Iyj_NWxNPby7bQNiWIRX1JNdl5HWc");//jds		
	}
	
	public String getCxKey(int index) {
		return this.cxKey.get(index);
	}
	
	public String getApiKey(int index) {
		return this.apiKey.get(index);
	}

}
