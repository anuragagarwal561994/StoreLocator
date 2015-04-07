package com.storelocator.altaiezior;

import android.net.Uri;
import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by altaiezior on 7/4/15.
 */
public class ApiCall {
    private String responseString;
    private Uri buildUri;
    private String HTTPMethod;

    public ApiCall(Uri buildUri, String HTTPMethod){
        this.buildUri = buildUri;
        this.HTTPMethod = HTTPMethod;
    }
    public String sendRequest() throws IOException, MalformedURLException, ProtocolException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try{
            URL url = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(HTTPMethod);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            return buffer.toString();
        } catch(MalformedURLException e){
            e.printStackTrace();
            return null;
        } catch(ProtocolException e){
            e.printStackTrace();
            return null;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(getClass().getSimpleName(), "Error closing stream", e);
                }
            }
        }
    }
}
