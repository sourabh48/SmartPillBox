package com.example.doctorbag;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity
{

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private EditText EmailField;
    private EditText PasswordField;
    private Button ELoginButton;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EmailField=(EditText)findViewById(R.id.username);
        PasswordField=(EditText)findViewById(R.id.password);
        ELoginButton=(Button)findViewById(R.id.login);

        ELoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });


        mProgress =new ProgressDialog(this);
        String titleId="Signing in...";
        mProgress.setTitle(titleId);
        mProgress.setMessage("Please Wait...");

        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void checkLogin()
    {
        String email=EmailField.getText().toString().trim();
        String password=PasswordField.getText().toString().trim();

        if(TextUtils.isEmpty(email)&& TextUtils.isEmpty(password))
        {
            EmailField.setError("Required");
            PasswordField.setError("Required");
        }
        else if(TextUtils.isEmpty(email))
        {
            EmailField.setError("Required");
        }
        else if(TextUtils.isEmpty(password))
        {
            PasswordField.setError("Required");
        }
        else
        {
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        mProgress.dismiss();
                        checkUserExist();
                    }
                    else
                    {
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkUserExist()
    {
        final String userid=mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(userid))
                {
                    String device_Token = FirebaseInstanceId.getInstance().getToken();
                    String user_id = mAuth.getCurrentUser().getUid();

                    mDatabase.child(user_id).child("device_token").setValue(device_Token).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Intent login=new Intent(LoginActivity.this,HomePageActivity.class);
                                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(login);
                            }
                        }
                    });
                }
                else
                {
                    Intent setup=new Intent(LoginActivity.this,SignUpActivity.class);
                    setup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setup);
                    Toast.makeText(LoginActivity.this,"Not Resistered!",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void signup(View view)
    {
        Intent account = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(account);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            updateUI();
        }
    }

    private void updateUI()
    {
        // Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
        mProgress.dismiss();
        Intent account = new Intent(LoginActivity.this,HomePageActivity.class);
        startActivity(account);
        finish();
    }

    private static final int TIME_INTERVAL = 1000;
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else
        {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }
}
