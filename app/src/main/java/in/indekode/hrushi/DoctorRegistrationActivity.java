package in.indekode.hrushi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorRegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText Dname , Demail, Dmobile, Dpassword;
    Button Dregister;
    TextView Dlogin;
    FirebaseAuth firebaseAuth;
    String[] speciality={"M.B.B.S", "Dermatologist", "Dentist", "Cardiologist"};
    String[] locality = {"Pune", "Mumbai", "Nagpur", "Nashik"};
    Spinner Dspeciality, Dlocation;
    String spec, loc, DN, DE,DP, DM, DS, DL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);

        Dname = findViewById(R.id.DRET_name);
        Demail = findViewById(R.id.DRET_email);
        Dmobile = findViewById(R.id.DRET_mobile);
        Dpassword = findViewById(R.id.DRET_password);

        Dlogin = findViewById(R.id.DRTV_Login);
        Dregister  = findViewById(R.id.DRBTN_reg);

        firebaseAuth = FirebaseAuth.getInstance();

        Dspeciality = findViewById(R.id.DRSPR_speciality);
        Dspeciality.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, speciality);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Dspeciality.setAdapter(aa);
        Dspeciality.setOnItemSelectedListener(new SpecilistSpinnerClass());

        Dlocation = findViewById(R.id.DRSPR_location);
        Dlocation.setOnItemSelectedListener(this);
        ArrayAdapter<String> bb = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, locality);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Dlocation.setAdapter(bb);
        Dlocation.setOnItemSelectedListener(new LocationSpinnerClass());


        Dregister.setOnClickListener(view -> {
            if(validate()){
                String emailuser = Demail.getText().toString().trim();
                String passworduser = Dpassword.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(emailuser, passworduser).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DoctorRegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        sendUserData();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(DoctorRegistrationActivity.this, DoctorLoginActivity.class));
                    }else {
                        Toast.makeText(DoctorRegistrationActivity.this, "Registration Failed !!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Dlogin.setOnClickListener(view -> startActivity(new Intent(DoctorRegistrationActivity.this, DoctorLoginActivity.class)));

    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Doctor").child(firebaseAuth.getUid());
        DoctorProfile doctorProfile = new DoctorProfile(DN, DE, DM, DS, DL);
        databaseReference.setValue(doctorProfile);
    }

    public boolean validate(){
        Boolean result = false;

        DN = Dname.getText().toString();
        DE = Demail.getText().toString();
        DP = Dpassword.getText().toString();
        DM = Dmobile.getText().toString();
        DS = spec;
        DL = loc;

        if (DN.isEmpty()){
            Dname.setError("Name required");
            Dname.requestFocus();
        }else if(DE.isEmpty()){
            Demail.setError("Email required");
            Demail.requestFocus();
        }else if(DP.isEmpty()){
            Dpassword.setError("Password required");
            Dpassword.requestFocus();
        }else if(DM.isEmpty()){
            Dmobile.setError("Mobile number required");
            Dmobile.requestFocus();
        } else{
            result = true;
        }
        return result;

    }


    class SpecilistSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            spec = speciality[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class LocationSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            loc = locality[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String unitf = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
