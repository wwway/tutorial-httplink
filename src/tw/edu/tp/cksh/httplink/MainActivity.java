package tw.edu.tp.cksh.httplink;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.*;
import org.json.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void queryHTTP(View nameIsNotImportant) 
    		throws MalformedURLException, UnsupportedEncodingException
    {
    	String city = ((EditText) this.findViewById(R.id.cityField))
    			                                    .getText().toString();
    	URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="
    				       + URLEncoder.encode(city, "UTF-8"));
    	
    	DownloadAsyncTask task = new DownloadAsyncTask();
    	task.referenceToMainActivity = this;
    	task.execute(url);
    }
    
    
    private class DownloadAsyncTask extends AsyncTask<URL, Void, String> {
    	
    	public MainActivity referenceToMainActivity;

		@Override
		protected String doInBackground(URL... urls) {
			if (urls.length <= 0)
				throw new java.lang.ArrayIndexOutOfBoundsException();
			
			try {
				InputStream stream = urls[0].openStream();
				InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
				
				StringBuilder builder = new StringBuilder();
				int ch;
				while ((ch = reader.read()) != -1) { // 從網路讀取一個個字元
					builder.append((char) ch);
				}
				
				return builder.toString(); // 轉成字串
			} catch (IOException e) {
				return "";
			}
		}
    	
		@Override
		protected void onPostExecute(String result) {
			TextView title = ((TextView) referenceToMainActivity.findViewById(R.id.locationText));
			TextView label = ((TextView) referenceToMainActivity.findViewById(R.id.resultText));
			if (result == "") {
				label.setText("無法連網QAQ");
			} else {
				try {
					JSONObject obj = new JSONObject(result);
					title.setText(obj.getString("name"));
					
					StringBuilder builder = new StringBuilder();
					
					JSONObject coord = obj.getJSONObject("coord");
					builder.append("(");
					builder.append(coord.getDouble("lat"));
					builder.append(", ");
					builder.append(coord.getDouble("lon"));
					builder.append(")");
					builder.append("\n");
					
					JSONObject weather = (JSONObject) obj.getJSONArray("weather").get(0);
					builder.append(weather.getString("description"));
					
					label.setText(builder.toString());
					
				} catch (JSONException e) {
					label.setText("JSON錯誤");
				}
			}
		}
    }
}
