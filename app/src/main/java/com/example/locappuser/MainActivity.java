package com.example.locappuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginButton;
    private EditText username,password;
    SharedPreferences pref;
    Editor editor;
    public static Socket mSocket;
//    public static final String URL="http://10.150.24.25:9222";
//    public static final String URL="http://192.168.2.30:9222";
//    public static final String URL="http://192.168.43.237:9222";
    public static final String URL="http://192.168.1.109:9222";
    public static String studentId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setComponents();
        socketHandle();
    }

    private void socketHandle(){
        try {
            mSocket= IO.socket(URL);
            mSocket.on("incomingLocation",getLocation);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener getLocation=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if(((String)args[0]).equals(MainActivity))
                        if(studentId!=null)
                        {
                            if(studentId.equals((String)args[2])){
                                editor.clear();
                                editor.putString("lattitude",(String)args[0]);
                                editor.putString("longtitude",(String)args[1]);
                                editor.commit();
                            }
                        }

                }
            });
        }
    };


    private void setComponents() {
        pref=getApplicationContext().getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(this),0);
        editor=pref.edit();
        loginButton=findViewById(R.id.btn_login);
        username=findViewById(R.id.input_email);
        password=findViewById(R.id.input_password);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==loginButton){
            final String username_=username.getText().toString();
            final String password_=password.getText().toString();
            final Boolean isSuccess;
            new AsyncTask<Void,Void,Void>(){
                String response;
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                    String idpass="{\"username\":\""+username_+"\",\"password\":\""+password_+"\"}";
                    Requests request=new Requests();
                    response=request.makePostRequest(URL+"/login",idpass);
//                        Log.i("responseeee", " response "+ response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e1){
                            e1.printStackTrace();
                        }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    String responseMessage,longAndLatAndStudentid,longtitude,lattitude,studentid;
//                    String token;
                    try {
                        if(response==null){
                            Log.i("LOCAPP DEBUG","Empty JSON response when login");
                        }
                        else{
                            JSONObject responseJSON=new JSONObject(response);
                            responseMessage=responseJSON.getString("message");
                            if(responseMessage.equals("Success")){
                                longAndLatAndStudentid=responseJSON.getString("longandlatandstudentid");
                                JSONObject LLSJson= new JSONObject(longAndLatAndStudentid);
                                lattitude=LLSJson.getString("lattitude");
                                longtitude=LLSJson.getString("longtitude");
                                studentid=LLSJson.getString("studentId");
                                editor.clear();
                                editor.putString("longtitude",longtitude);
                                editor.putString("lattitude",lattitude);
                                studentId=studentid;
                                editor.commit();
                            }
                            else{
                                Log.i("LOGIN INFO",responseMessage);
                            }

//                        token=responseJSON.getString("token");
//                        editor.putString("token",(String)token);
//                        editor.commit();
                            if(responseMessage.equals("Success")){
                                Log.i("SUCCESS AUTHENTICATION","SUCCESS AUTHENTICATION");
                                Intent i=new Intent(MainActivity.this,SecondActivity.class);
                                startActivity(i);
                            }
                            else if(responseMessage.equals("Unsuccess")){
                                Toast.makeText(getApplicationContext(),"Yanlış kullanıcı adı ve ya şifre",Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//
                    super.onPostExecute(aVoid);
                }

            }.execute();
        }

    }
}
