package com.cs309.nerdsbattle.nerds_battle.Login;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs309.nerdsbattle.nerds_battle.HomeActivity;
import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateInter;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for user to sign up for an account.
 */
public class SignUpActivity extends AppCompatActivity implements updateInter {
    private static final int MESSAGE_DATA_RECEIVED = 1;
    private Button signUpButton;
    private Button backButton;
    private TextView outputText;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Thread updateThread;
    private boolean doneAdding;
    private Player newPlayer;
    private updateHandler updateHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        outputText = (TextView) findViewById(R.id.SignUpOutputText);
        etUsername = (EditText) findViewById(R.id.ETUsername);
        etPassword = (EditText) findViewById(R.id.ETPassword);
        etConfirmPassword = (EditText) findViewById(R.id.ETConfirmPassword);

        signUpButton = (Button) findViewById(R.id.SignUpBtn);
        updateHandler = new updateHandler(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String username = etUsername.getText().toString();
                String pw = etPassword.getText().toString();
                String confirmpw = etConfirmPassword.getText().toString();
                if(username==null||username.trim().equals("")) {
                    outputText.setText("Error in username");
                    clearTextFields();
                    return;
                }
                if(!pw.equals(confirmpw)){
                    outputText.setText("Different password");
                    clearTextFields();
                    return;
                }
                final Map<String, String> toAdd = new HashMap<>();
                toAdd.put("Username", username);
                toAdd.put("Password", pw);
                updateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServerRequest sr = new ServerRequest(getApplicationContext());
                        newPlayer = sr.addPlayer(toAdd, ServerRequest.REQUEST_TAG_ADD_PLAYER);
                        updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
                    }
                });
                updateThread.start();
            }
        });

        backButton = (Button) findViewById(R.id.SignupBackBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });

    }

    /**
     * Clear the texts in the places where user can edit the texts.
     */
    private void clearTextFields(){
        etUsername.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
        etUsername.requestFocus();
    }

    @Override
    public void update() {
        if (newPlayer == null) {
            return;
        }
        if(newPlayer.getUsername() != null){
            //Success
            Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
            i.putExtra("User", newPlayer);
            startActivity(i);
        }
        else {
            //Failure, username probably taken
            Context context = getApplicationContext();
            CharSequence text = "Username already taken";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context,text,duration);
            toast.show();
            clearTextFields();
        }
    }
}
