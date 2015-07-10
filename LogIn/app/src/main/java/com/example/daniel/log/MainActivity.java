package com.example.daniel.log;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

//api SigIn
public class MainActivity extends Activity implements
        OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient client;

    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;

    private String TAG = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_out_btn).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        TextView txt = (TextView) findViewById(R.id.textView1);

        Toast.makeText(getApplicationContext(), "Hola", Toast.LENGTH_LONG);

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    @Override
    protected void onStart(){
        super.onStart();
        client.connect();

    }

    @Override
    protected void onStop(){
        super.onStop();
        client.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onClick(View view){
       if(view.getId() == R.id.sign_in_button){
           onSignedInCliked();
       }

        if(view.getId() == R.id.sign_out_btn){
            onSignedOutCliked();
        }

    }

    private void onSignedInCliked() {
        mShouldResolve = true;
        client.connect();

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);

    }

    private void onSignedOutCliked(){
        if (client.isConnected()){
            Plus.AccountApi.clearDefaultAccount(client);
            client.disconnect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: "+ connectionResult);

        if(!mIsResolving && mShouldResolve){
            if(connectionResult.hasResolution()){
                try{
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                }catch (IntentSender.SendIntentException e){
                    Log.e(TAG,"Could not resolve ConnectionResult. ",e);
                    mIsResolving = false;
                    client.connect();
                }
            }else {

            }
        }else {

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: " + bundle);
        mShouldResolve = false;

        if (Plus.PeopleApi.getCurrentPerson(client) != null){
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(client);
            String name = currentPerson.getDisplayName();
            String profileUrl = currentPerson.getUrl();
            String email = Plus.AccountApi.getAccountName(client);
            TextView textView = (TextView)findViewById(R.id.textView1);
            textView.setText(name);
        }
    }

    @Override
    public void onActivityResult(int resquestCode, int resultCode, Intent data){
        super.onActivityResult(resquestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: "+ resquestCode +": "+resultCode+": "+data);
        if(resquestCode == RC_SIGN_IN){
            if(resultCode != RESULT_OK){
                mShouldResolve = false;
            }
            mIsResolving = false;
            client.connect();
        }
    }


}
