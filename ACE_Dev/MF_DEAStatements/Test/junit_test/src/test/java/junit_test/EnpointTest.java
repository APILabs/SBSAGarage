package junit_test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EnpointTest {
	private static URL url;
	private static HttpURLConnection con;
	private String msg="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns2:ProcessESCRequest xmlns:ns2=\"http://standardbank.co.za/sa/services/Technical/Integration/Integration/PS_ESC_LegacyStatementDataV1\" xmlns=\"http://standardbank.co.za/sa/services/Global/IfxV2_1/HeaderV2_0\"></ns2:ProcessESCRequest></soapenv:Body></soapenv:Envelope>";
	

	@Before
	public void setUp() throws Exception {
		con=setupConnection("http://localhost:9081/processesc");
	}

	@After
	public void tearDown() throws Exception {
		con.disconnect();
	}

	@Test
	public void test() {
		try {
			con.getOutputStream().write(msg.getBytes());
			System.out.println(getResponse(con));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(getResponse(con));
	}
	
	@Test
	public void test2() {
		try {
			con.getOutputStream().write(msg.getBytes());
			System.out.println(getResponse(con));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNull(getResponse(con));
	}
	
	//setups the http connection
	private HttpURLConnection setupConnection(String endPoint){
		URL url;
		HttpURLConnection con = null;
		try {
			url = new URL(endPoint);
			con=(HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;				
	}
	
	//get response
	private String getResponse(HttpURLConnection con){
		
		Reader in;
		String result="";
		try {
			in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			
			for (int c;(c=in.read())>=0;result=result+(char)c);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
