package com.cs309.nerdsbattle.nerds_battle.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateInter;

import static com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler.MESSAGE_DATA_RECEIVED;

/**
 * Handles the forgot password event
 */
public class ForgotPasswordActivity extends AppCompatActivity implements updateInter {

    private TextView outputText;
    private EditText etUsername;
    private Button getPwBtn;
    private Button backButton;
    private updateHandler updateHandler;
    private Thread updateThread;
    private boolean[] usernameChecking;
    private Player player;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        outputText = (TextView) findViewById(R.id.ForgotpwOutputText);
        etUsername = (EditText) findViewById(R.id.ETForgotpwUsername);
        updateHandler = new updateHandler(this);

        getPwBtn = (Button) findViewById(R.id.GetPwBtn);
        getPwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                username = etUsername.getText().toString();

                updateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String username = etUsername.getText().toString();
                        ServerRequest sr = new ServerRequest(getApplicationContext());
                        player = sr.getPlayer(ServerRequest.PLAYER + username, ServerRequest.REQUEST_TAG_PLAYER);
                        updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
//                        ServerRequest sr = new ServerRequest(getApplicationContext());
//                        usernameChecking = sr.checkUsername(username, ServerRequest.REQUEST_TAG_ALL_USERNAME);
//                        updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
                    }
                });
                updateThread.start();
            }
        });

        backButton = (Button) findViewById(R.id.ForgotpwBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });
    }

    @Override
    public void update() {
        if (player == null) {
            return;
        }
        if (player.getUsername()==null) {
            outputText.setText("Username does not exist");
            return;
        }
        String pw = player.getPassword();
        Intent sendAccountInfo = new Intent(Intent.ACTION_SEND);
        sendAccountInfo.setType("text/plain");
        sendAccountInfo.putExtra(Intent.EXTRA_EMAIL  , new String[]{"steven96@iastate.edu"});
        sendAccountInfo.putExtra(Intent.EXTRA_SUBJECT, "Nerds Battle: Retrieve Account");
        sendAccountInfo.putExtra(Intent.EXTRA_TEXT   , "Password: ");
        try {
            startActivity(Intent.createChooser(sendAccountInfo, "Send mail..."));
            outputText.setText("Password is sent to email :"+pw);
        } catch (android.content.ActivityNotFoundException ex) {
            outputText.setText("Fail to send email");
        }

    }
}
