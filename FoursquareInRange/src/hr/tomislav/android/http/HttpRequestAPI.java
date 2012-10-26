package hr.tomislav.android.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class HttpRequestAPI {

	/**
	 * Method that sends request to uri, and creates JSONObject from response string.
	 * @param uri
	 * @return JSONObject with response from api. Response from api must be JSONObject.
	 * @throws Exception
	 */
	public static JSONObject executeHttpGet(String uri) throws Exception {
		
		HttpGet req = new HttpGet(uri);
		HttpClient client = new DefaultHttpClient();
		HttpResponse resLogin = client.execute(req);
		BufferedReader r = new BufferedReader(new InputStreamReader(resLogin
				.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = r.readLine()) != null) {
			sb.append(s);
		}
		return new JSONObject(sb.toString());
	}
}
