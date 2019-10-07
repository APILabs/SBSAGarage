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
	private String msg="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://IDX.IBIDXService/V1\">\n" + 
			"   <soapenv:Header>\n" + 
			"      <v1:IDXHeader>\n" + 
			"         <!--Optional:-->\n" + 
			"         <v1:AccountStatus>1</v1:AccountStatus>\n" + 
			"         <v1:AccountType>1</v1:AccountType>\n" + 
			"         <v1:IBReference>1234</v1:IBReference>\n" + 
			"         <!--Optional:-->\n" + 
			"         <v1:IDXReference>4321</v1:IDXReference>\n" + 
			"         <v1:InitiatingBank>1</v1:InitiatingBank>\n" + 
			"         <v1:MessageType>1</v1:MessageType>\n" + 
			"         <v1:SourceBank>2</v1:SourceBank>\n" + 
			"         <!--Optional:-->\n" + 
			"         <v1:SBReference>1</v1:SBReference>\n" + 
			"         <v1:BusinessUnit>1</v1:BusinessUnit>\n" + 
			"         <v1:InitiatingIP>1</v1:InitiatingIP>\n" + 
			"      </v1:IDXHeader>\n" + 
			"   </soapenv:Header>\n" + 
			"   <soapenv:Body>\n" + 
			"      <v1:Submit>\n" + 
			"         <!--Optional:-->\n" + 
			"         <v1:request>\n" + 
			"            <!--Optional:-->\n" + 
			"            <v1:BankAccount>0000010001251277</v1:BankAccount>\n" + 
			"            <!--Optional:-->\n" + 
			"            <v1:BranchCode>632005</v1:BranchCode>\n" + 
			"            <!--Optional:-->\n" + 
			"            <v1:MonthsBankStatements>6</v1:MonthsBankStatements>\n" + 
			"            <v1:StatementRange>2019-01-29:2019-08-29</v1:StatementRange>\n" + 
			"            <!--Optional:-->\n" + 
			"            <v1:PhysicalEntity>\n" + 
			"               <!--1 to 10 repetitions:-->\n" + 
			"               <v1:Entity>\n" + 
			"                  <!--Optional:-->\n" + 
			"                  <v1:IdentificationNo>NHJ890012</v1:IdentificationNo>\n" + 
			"                  <!--Optional:-->\n" + 
			"                 <v1:IdentificationType>1</v1:IdentificationType>\n" + 
			"                  <!--Optional:-->\n" + 
			"                  <v1:Initials>R</v1:Initials>\n" + 
			"                  <!--Optional:-->\n" + 
			"                  <v1:Name>RAMARU</v1:Name>\n" + 
			"               </v1:Entity>\n" + 
			"           </v1:PhysicalEntity> \n" + 
			"         </v1:request>\n" + 
			"      </v1:Submit>\n" + 
			"   </soapenv:Body>\n" + 
			"</soapenv:Envelope>";
	

	@Before
	public void setUp() throws Exception {
		con=setupConnection("http://172.30.100.67:7800/sa/services/technical/integration/integration/ps_dea_statementadapteroutv1");
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
	
//	@Test
//	public void test2() {
//		try {
//			con.getOutputStream().write(msg.getBytes());
//			System.out.println(getResponse(con));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		assertNull(getResponse(con));
//	}
	
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
