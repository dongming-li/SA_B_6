package com.cs309.nerdsbattle.nerds_battle.Login;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs309.nerdsbattle.nerds_battle.HomeActivity;
import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateInter;

/**
 * Activity that handle user's log in.
 */
public class LogInActivity extends AppCompatActivity implements updateInter {
    private static final int MESSAGE_DATA_RECEIVED = 1;

    private TextView signUp;
    private TextView forgotPw;
    private Button logInButton;
    private Player player;
    private com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler updateHandler;

    private EditText uName;
    private EditText pw;
    private Thread updateThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInButton = (Button) findViewById(R.id.btnLogIn);
        updateHandler = new updateHandler(this);

        uName = (EditText) findViewById(R.id.Username);
        pw = (EditText) findViewById(R.id.Password);

        uName.setText("Player1");
        pw.setText("idknewpassword");

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uName.getText().length()>0&&pw.getText().length()>0) {
                    updateThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String username = uName.getText().toString();
                            ServerRequest sr = new ServerRequest(getApplicationContext());
                            player = sr.getPlayer(ServerRequest.PLAYER + username, ServerRequest.REQUEST_TAG_PLAYER);
                            updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
                        }
                    });
                    updateThread.start();
                }
                else{
                    Context context = getApplicationContext();
                    CharSequence text = "Missing Username or Password";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context,text,duration);
                    toast.show();
                }
            }
        });

        forgotPw = (TextView) findViewById(R.id.txtForgotpassword);
        forgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent startForgotPw = new Intent(LogInActivity.this, ForgotPasswordActivity.class);
                startActivity(startForgotPw);
            }
        });

        signUp = (TextView) findViewById(R.id.txtSignupnow);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent startSignUp = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(startSignUp);
            }
        });

    }

    /**
     * Check whether log in is successful
     */
    public void update() {
        //UI UPDATES
        //PLAYER IS RECEIVED

        if (player == null) {
            return;
        }
        if (player.getUsername()==null) {
            Context context = getApplicationContext();
            CharSequence text = "Incorrect Username";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context,text,duration);
            toast.show();
            return;
        }
        final String pword = pw.getText().toString();
        String realpw = player.getPassword();
        if(realpw!=null && realpw.equals(pword)){
            Intent i = new Intent(LogInActivity.this, HomeActivity.class);
            i.putExtra("User", player);
            startActivity(i);
        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "Incorrect Username or Password";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context,text,duration);
            toast.show();
        }
    }
}
