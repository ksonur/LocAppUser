package com.example.locappuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

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
    public static final String URL="http://10.150.25.158:9222";
//    public static final String URL="http://192.168.43.237:9222";


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
                        editor.clear();
                        editor.putString("loc",(String)args[0]);
                        editor.commit();
                }
            });
        }
    };


    private void setComponents() {
        pref=getApplicationContext().getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(this),0);
        editor=pref.edit();
        loginButton=findViewById(R.id.btn_login);
        username=(EditText)findViewById(R.id.input_email);
        password=(EditText)findViewById(R.id.input_password);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==loginButton){
            final String username_=username.getText().toString();
            final String password_=password.getText().toString();
            final Boolean isSuccess;
            new AsyncTask<Void,Void,Void>(){
                String response =" ";
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                    String idpass="{\"username\":\""+username_+"\",\"password\":\""+password_+"\"}";
                    Requests request=new Requests();
                    response=request.makePostRequest(URL+"/login",idpass);
//                        Log.i("responseeee", " response "+ response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if(response.equals("Success")){
                        Intent i=new Intent(MainActivity.this,SecondActivity.class);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Yanlış kullanıcı adı ve ya şifre",Toast.LENGTH_LONG).show();
                    }
                    super.onPostExecute(aVoid);
                }

            }.execute();
        }

    }
}
