package in.indekode.hrushi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;

public class FirstActivity extends Activity {

    EditText Lemail, Lpassword;
    Button LLogin;
    TextView Lreg;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Lemail = findViewById(R.id.LET_email);
        Lpassword = findViewById(R.id.LET_password);
        LLogin = findViewById(R.id.LBTN_submit);
        Lreg = findViewById(R.id.TV_Reg);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(FirstActivity.this);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            finish();
            startActivity(new Intent(FirstActivity.this, MainActivity.class));
        }

        LLogin.setOnClickListener(view -> validate(Lemail.getText().toString(), Lpassword.getText().toString()));

        Lreg.setOnClickListener(view -> startActivity(new Intent(FirstActivity.this, Registration.class)));

    }

    private void validate(final String username, final String passwords) {

        progressDialog.setMessage("सर्व्हरशी कनेक्ट करीत आहे ...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(username, passwords).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                progressDialog.dismiss();
                startActivity(new Intent(FirstActivity.this, MainActivity.class));
            } else {
                progressDialog.dismiss();
                Toast.makeText(FirstActivity.this, "अयशस्वी लॉग इन", Toast.LENGTH_LONG).show();
            }
        });
    }
}