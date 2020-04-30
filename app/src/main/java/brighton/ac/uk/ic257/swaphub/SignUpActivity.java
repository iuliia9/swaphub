package brighton.ac.uk.ic257.swaphub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // initialize all fields
        mFirebase = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editText3);
        password = findViewById(R.id.editText4);
        btnSignUp = findViewById(R.id.button2);
        tvSignIn = findViewById(R.id.textView2);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get email and password from fields
                String email = emailId.getText().toString();
                String pwd = password.getText().toString().trim();

                if (email.isEmpty()) {
                    emailId.setError("Please enter your email");
                    emailId.requestFocus();
                }
                else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if (pwd.length() < 6){
                    password.setError("Password must be at least 6 characters long");
                    password.requestFocus();
                }
                else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebase.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUpActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(SignUpActivity.this, EditUserProfile.class));
                                        Toast.makeText(SignUpActivity.this, "Your account has been created", Toast.LENGTH_SHORT).show();

                                    }
                                    else{
                                        Toast.makeText(SignUpActivity.this, "Sign Up failed", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }

            }
        });

        // go to sign in activity
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
}
