package com.example.myweatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public class WebDownloader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            Log.i("URL", urls[0]);

            URL url;
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                Log.i("WebDownloader", "Downloading page");
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1){
                    result += (char)data;
                    data = reader.read();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                result = "Error getting web page";
            } catch (IOException e) {
                e.printStackTrace();
                result = "Error getting web page";
            }
            Log.i("WebDownloader", "Page downloaded");
            return  result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);
                String desc = "";
                String main = "";
                for(int i = 0; i < arr.length(); i++){
                    main += arr.getJSONObject(i).getString("main");
                    desc += arr.getJSONObject(i).getString("description");
                }
                outputTextView.setText(main+ "\n" +desc);
                Log.i("Weather", main+desc);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    TextView outputTextView;
    EditText cityNameEditText;
    Button getWeatherButton;
    String baseUrl;
    String apiKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outputTextView = findViewById(R.id.outputTextView);
        cityNameEditText = findViewById(R.id.cityNameEditText);
        baseUrl = "http://api.openweathermap.org/data/2.5/weather?q=";
        apiKey = "Your API KEY Here";
    }

    public void clickFunction(View view){
        Log.i("info", "Button Clicked " + view.getId());
        String cityName = cityNameEditText.getText().toString();
        String requestUrl = baseUrl + cityName + "&appid=" + apiKey;

        WebDownloader webDownloader = new WebDownloader();
        try {
            webDownloader.execute(requestUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
