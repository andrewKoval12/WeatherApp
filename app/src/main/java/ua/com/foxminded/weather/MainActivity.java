package ua.com.foxminded.weather;


import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private TextView textViewInfo;
    private String weather;

    private final String WEATHER_KEY = "WHETHER_KEY";
    private final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=ua&APPID=57c968b9190fd6ab75100c695e593ed2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCity = findViewById(R.id.editTextCityName);
        textViewInfo = findViewById(R.id.textViewWeatherDescription);
        if(savedInstanceState != null){
            weather = savedInstanceState.getString(WEATHER_KEY);
            textViewInfo.setText(weather);
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WEATHER_KEY, weather);
    }

    public void onClickShowWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if(!city.isEmpty()) {
            ShowWeather showWeather = new ShowWeather();
            String url = String.format(WEATHER_URL, city);
            showWeather.execute(url);
        }
    }

    private class ShowWeather extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null){
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            if(s != null) {
                try {
                    jsonObject = new JSONObject(s);
                    String city = jsonObject.getString("name");
                    String temp = jsonObject.getJSONObject("main").getString("temp");
                    String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    weather = String.format("Місто: %s\nТемпература: %s\nНа вулиці: %s", city, temp, description);
                    textViewInfo.setText(weather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                    Toast.makeText(getApplicationContext(), "Місто не знайдене", Toast.LENGTH_SHORT).show();
            }
        }
    }


}