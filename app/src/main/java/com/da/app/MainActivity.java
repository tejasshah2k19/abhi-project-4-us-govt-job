package com.da.app;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import kotlinx.coroutines.Job;

public class MainActivity extends AppCompatActivity {

    EditText edtJobTitle;
    EditText edtLocation;
    EditText edtPayRange;
    Button searchButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtJobTitle  = findViewById(R.id.edtMainJobTitle);
        edtLocation = findViewById(R.id.edtMainLocation);
        edtPayRange = findViewById(R.id.edtMainPayRange);
        searchButton = findViewById(R.id.btnMainSubmit);




        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneModel = Build.MODEL;
                String manufacturer = Build.MANUFACTURER;

                String jobtitle = edtJobTitle.getText().toString();
                String location = edtLocation.getText().toString();
                String payRange = edtPayRange.getText().toString();

                ExecutorService ex = Executors.newSingleThreadExecutor();
                Future<ArrayList<JobModel>> ft =  ex.submit(new Callable<ArrayList<JobModel>>() {
                    @Override
                    public ArrayList<JobModel> call() throws Exception {
                        return getJobData(jobtitle,location,payRange,phoneModel,manufacturer);
                    }
                });

                try {
                    ArrayList<JobModel> jobs =  ft.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }

    private ArrayList<JobModel> getJobData(String jobTitle,String location,String payRange,String phoneModel,String manufacturer){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("jobTitle",jobTitle);
            jsonObject.put("location",location);
            jsonObject.put("payRange",payRange);
            jsonObject.put("phoneModel",phoneModel);
            jsonObject.put("manufacturer",manufacturer);

            String apiUrl = "https://zany-spoon-q7464gpq74jrcx6q5-8080.app.github.dev/api/jobs?jobTitle="+jobTitle+"&location="+location+"&payRange="+payRange+"&phoneModel="+phoneModel+"&manufacturer="+manufacturer;

            URL url = new URL(apiUrl);

            HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String str;
            StringBuffer respStr = new StringBuffer();
            while((str = br.readLine())!=null ){
                respStr.append(str);
            }

            Log.i("api",respStr.toString());

            Log.i("api",connection.getResponseCode()+"");


        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return new ArrayList<>();
    }
}