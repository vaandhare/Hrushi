package in.indekode.hrushi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class FirstActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText et_name, et_mob, et_email, et_otp;
    Spinner sp_gender;
    Button btn_submit, btn_otp;

    String[] genders = {"Male", "Female"};
    static String gen;
    static String Sname, Semail, Smob, Sgen;
    String OTP;

    FirebaseAuth mAuth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        et_name = findViewById(R.id.ET_name);
        et_mob = findViewById(R.id.ET_mobile);
        et_email = findViewById(R.id.ET_email);
        et_otp = findViewById(R.id.ET_otp);
        btn_submit = findViewById(R.id.BTN_submit);
        btn_otp = findViewById(R.id.BTN_otp);

        sp_gender = findViewById(R.id.SP_gender);

        sp_gender.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_gender.setAdapter(aa);
        sp_gender.setOnItemSelectedListener(new SpinnerClass());

        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(FirstActivity.this);

        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString().trim();
                String mobile = et_mob.getText().toString().trim();
                String email = et_email.getText().toString().trim();

//                if (name.isEmpty()) {
//                    et_name.setError("Enter a valid name");
//                    et_name.requestFocus();
//                } else
                if (mobile.isEmpty() || mobile.length() < 10) {
                    et_mob.setError("Enter a valid mobile");
                    et_mob.requestFocus();
//                } else if (email.isEmpty()) {
//                    et_email.setError("Enter a valid Email");
//                    et_email.requestFocus();
                } else {
//                Sname = name;
//                Semail = email;
//                Smob = mobile;
//                Sgen = gen;
                    String indMo = "+91" + mobile;
                    sendVerification(indMo);
                }
            }
        });

        btn_submit.setOnClickListener(this::onClick);

    }

    private void onClick(View view) {
        String otp = et_otp.getText().toString().trim();
        verifyCode(otp);
    }

    class SpinnerClass implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            gen = genders[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String gend = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void sendVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        dialog.setMessage("Getting Code...");
        dialog.show();
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            Toast.makeText(FirstActivity.this, "Verification Successful", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
//            Toast.makeText(FirstActivity.this, "Enter correct OTP or Valid Credentials", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            OTP = s;
            dialog.dismiss();
        }
    };

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, code);
        signInWithPhoneAuthCredential(credential);
        dialog.setMessage("Verifying Code...");
        dialog.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FirstActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            startActivity(new Intent(FirstActivity.this, MainActivity.class));
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                dialog.dismiss();
                                Toast.makeText(FirstActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
