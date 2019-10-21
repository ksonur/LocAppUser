package com.example.locappuser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Requests {

    public Requests() {
    }

    public String makePostRequest(String url,String data) throws IOException {

        URL url_=new URL(url);
        HttpURLConnection urlConnection= (HttpURLConnection) url_.openConnection();
        String isSuccess="no success";
        StringBuffer response=new StringBuffer();

        urlConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();

        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
        writer.write(data);
        writer.close();

        BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        while ((isSuccess=br.readLine())!=null){
            response.append(isSuccess);
        }
        br.close();

        urlConnection.disconnect();
        return response.toString();
    }

}
