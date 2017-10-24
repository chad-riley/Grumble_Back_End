package com.libertymutual.goforcode.grumble.services;

import javax.servlet.http.HttpServletRequest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

public class SessionKeyGetterTests {
	
	private HttpServletRequest request;

	@Before
	public void setUp() {	
		this.request = mock(HttpServletRequest.class);
	} 

	@Test
	public void test_getTheSessionKeyForRequest_returns_valid_key() throws IOException {
		SessionKeyGetter getAKey = new SessionKeyGetter();
		String testInput = "{   key:       1111111111111111111111111111111111111111111111111111111111111111}";
		String key = "1111111111111111111111111111111111111111111111111111111111111111";
		BufferedReader testReader = new BufferedReader(new StringReader(testInput));
		when(this.request.getReader()).thenReturn(testReader);
		
		String result = getAKey.getTheSessionKeyForRequest(this.request);
		
		assertThat(result).isEqualTo(key);
	}
}