package in.indekode.hrushi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DoctorLoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    EditText DLemail, DLpassword;
    Button DLogin;
    TextView Dreg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);

        DLemail = findViewById(R.id.DLET_email);
        DLpassword = findViewById(R.id.DLET_password);
        DLogin = findViewById(R.id.DLBTN_submit);
        Dreg = findViewById(R.id.DTV_Reg);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(DoctorLoginActivity.this);


        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            finish();
            startActivity(new Intent(DoctorLoginActivity.this, DoctorActivity.class));
        }

        DLogin.setOnClickListener(view -> validate(DLemail.getText().toString(), DLpassword.getText().toString()));

        Dreg.setOnClickListener(view -> startActivity(new Intent(DoctorLoginActivity.this, DoctorRegistrationActivity.class)));

    }

    private void validate(final String username, final String passwords) {

        progressDialog.setMessage("Connecting with server ...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(username, passwords).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                progressDialog.dismiss();
                startActivity(new Intent(DoctorLoginActivity.this, DoctorActivity.class));
            } else {
                progressDialog.dismiss();
                Toast.makeText(DoctorLoginActivity.this, "Unsuccessful Login", Toast.LENGTH_LONG).show();
            }
        });
    }
}
