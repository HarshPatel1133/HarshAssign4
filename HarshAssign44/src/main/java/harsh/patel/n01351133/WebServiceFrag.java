package harsh.patel.n01351133;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class WebServiceFrag extends Fragment {

    TextView serviceResult;
    EditText serviceZipET;
    Button serviceBtn;
    String zipCode;
    String myApiKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_web_service, container, false);
        serviceZipET = root.findViewById(R.id.HarshZipET);
        serviceResult = root.findViewById(R.id.HarshServiceResultTV);
        serviceBtn = root.findViewById(R.id.HarshServiceBtn);
        myApiKey = getString(R.string.owm_api_key);

        serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateZipCode()) {
                    zipCode = serviceZipET.getText().toString();
                    callWebService(zipCode);
                }
            }
        });
        return root;
    }

    public void callWebService(String zipCode) {
        String address = "https://api.openweathermap.org/data/2.5/weather?zip=";
        address += zipCode;
        address += ",us&appid=";
        address += myApiKey;
        new ReadJSONFeedTask().execute(address);
    }

    public String readJSONFeed(String address) {
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ;
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream content = new BufferedInputStream(
                    urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return stringBuilder.toString();
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                return readJSONFeed(urls[0]);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String result) {
            try {
                //JSONArray jsonArray = new JSONArray(result);
                //Uncomment the two rows below to parse weather data from OpenWeatherMap
                if (result.isEmpty()) {
                    serviceZipET.requestFocus();
                    serviceZipET.setError(getString(R.string.zip_does_not_exist));
                } else {
                    JSONObject weatherJson = new JSONObject(result);
                    String validate = weatherJson.getString("cod");
//                for (int i = 0; i < dataArray1.length(); i++) {
//                    JSONObject jsonObject = dataArray1.getJSONObject(i);
//                    strResults += "id: " + jsonObject.getString("id");
//                    strResults += "\nmain: " + jsonObject.getString("main");
//                    strResults += "\ndescription: " + jsonObject.getString("description");
//                }

                    String strResults = getString(R.string.location_result) + zipCode;
                    strResults += "\n" + getString(R.string.city) + weatherJson.getString("name");
                    JSONObject coordObj = weatherJson.getJSONObject("coord");
                    strResults += "\n" + getString(R.string.longitude) + coordObj.getString("lon");
                    strResults += "\n" + getString(R.string.latitude) + coordObj.getString("lat");

                    JSONObject dataObject = weatherJson.getJSONObject("main");
                    strResults += "\n" + getString(R.string.humidity) + dataObject.getString("humidity");

                    serviceResult.setText(strResults);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validateZipCode() {
        boolean isValid = true;
        //String zipRegex = "^\\d{5}(?:[-\\s]\\d{4})?$";
        String zipRegex = "^[0-9]{5}(?:-[0-9]{4})?$";
        zipCode = serviceZipET.getText().toString().trim();

        if (!zipCode.matches(zipRegex)) {
            serviceZipET.setError(getString(R.string.zip_validation_msg));
            isValid = false;
        }
        return isValid;
    }}
