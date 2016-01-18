package com.sap.cloud.samples.mailjetmaildemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.sap.core.connectivity.api.DestinationException;
import com.sap.core.connectivity.api.DestinationFactory;
import com.sap.core.connectivity.api.DestinationNotFoundException;
import com.sap.core.connectivity.api.http.HttpDestination;

public class MailjetClient{
	private static MailjetClient instance = null;

//	private static final Logger LOGGER = LoggerFactory.getLogger(MailjetClient.class);

	private static final String DEFAULT_PROPS_FILE_LOCATION = "/WEB-INF/lib/mailjet.properties";
	private static final String MAILJET_API_DESTINATION = "MAILJETAPI";

	private static HttpDestination mailjetDestination;

	public static Properties mailjetProps;
    public static Session mailSession;

	public static enum actionSuffix {
		SEND ("send");

		private String suffix;

		actionSuffix(String suffix) {
			this.suffix = suffix;
		}

		public String suffix() {
			return suffix;
		}
	}

	private MailjetClient() {
		Context ctx;
		try {
			ctx = new InitialContext();
			DestinationFactory destinationFactory = (DestinationFactory)ctx.lookup(DestinationFactory.JNDI_NAME);
			mailjetDestination = (HttpDestination) destinationFactory.getDestination(MAILJET_API_DESTINATION);
		} catch (NamingException | DestinationNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static MailjetClient getInstance(ServletContext servletContext, Session session) {
		synchronized(MailjetClient.class) {
			if (instance == null)
				MailjetClient.loadPropertiesFromContext(servletContext);
				mailSession = session;
				instance = new MailjetClient();
		}
		return instance;
	}

	private static void loadPropertiesFromContext(ServletContext servletContext) {
		mailjetProps = new Properties();
		try {
			InputStream stream = servletContext.getResourceAsStream(DEFAULT_PROPS_FILE_LOCATION);
			try {
				mailjetProps.load(stream);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String makeAPIPOSTCall(String extraURL, String data)
			throws ClientProtocolException, URISyntaxException, DestinationException, IOException {
		return makePOSTCall(mailjetDestination, extraURL, data);
	}

	public String makeAPIPOSTCall(String extraURL, JSONObject data)
			throws ClientProtocolException, URISyntaxException, DestinationException, IOException {
		return makePOSTCall(mailjetDestination, extraURL, data.toString());
	}

	protected String makePOSTCall(HttpDestination destination, String extraURL, String data)
			throws URISyntaxException, DestinationException, IOException, ClientProtocolException {
		String url = destination.getURI() + extraURL;
		HttpClient client = destination.createHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);

		HttpPost request = new HttpPost(url);

		request.setHeader("Content-type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setEntity(new StringEntity(data, "UTF-8"));

		HttpResponse response = client.execute(request);//httpContextsMap.get(client));
		System.err.println(response.getStatusLine().toString());
		return EntityUtils.toString(response.getEntity());
	}

	public String makeAPIGETCall(String extraURL, List<NameValuePair> headerParams)
			throws ClientProtocolException, URISyntaxException, DestinationException, IOException {
		return makeGETCall(mailjetDestination, extraURL, headerParams);
	}

	public String makeAPIGETCall(String extraURL)
			throws ClientProtocolException, URISyntaxException, DestinationException, IOException {
		List<NameValuePair> headerParams = new ArrayList<>();
		return makeAPIGETCall(extraURL, headerParams);
	}

	protected String makeGETCall(HttpDestination destination, String extraURL, List<NameValuePair> headerParams)
			throws URISyntaxException, DestinationException, IOException, ClientProtocolException {
		String url = destination.getURI() + extraURL;
		HttpClient client = destination.createHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);

		HttpGet request = new HttpGet(url);
		for (NameValuePair headerParam : headerParams) {
			request.addHeader(headerParam.getName(), headerParam.getValue());
		}

		HttpResponse response = client.execute(request);//httpContextsMap.get(client));
		return EntityUtils.toString(response.getEntity());
	}

}
