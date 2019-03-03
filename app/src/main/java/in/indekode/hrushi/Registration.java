package in.indekode.hrushi;

import android.app.Activity;
import android.content.Intent;
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

public class Registration extends Activity  implements AdapterView.OnItemSelectedListener{

    EditText Ename , Emobile, Eemail, Eage, Epassword, Eemergency;
    TextView signup;
    Button signin;
    FirebaseAuth firebaseAuth;
    Spinner Egender;
    String name, email, password, age, mobile, gen, ehelp,gen2;
    String[] gender={"पुरुष (Male)", "स्त्री (Female)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Ename = findViewById(R.id.ET_name);
        Epassword = findViewById(R.id.ET_password);
        Eage = findViewById(R.id.ET_age);
        Emobile = findViewById(R.id.ET_mobile);
        Eemail = findViewById(R.id.ET_email);
        Eemergency = findViewById(R.id.ET_emergency_help);
        signin = findViewById(R.id.BTN_submit);
        signup = findViewById(R.id.TV_Login);

        firebaseAuth = FirebaseAuth.getInstance();

        Egender = findViewById(R.id.SPR_gender);
        Egender.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Egender.setAdapter(aa);
        Egender.setOnItemSelectedListener(new SpinnerClass());

        signin.setOnClickListener(view -> {
            if(validate()){
                String emailuser = Eemail.getText().toString().trim();
                String passworduser = Epassword.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(emailuser, passworduser).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registration.this, "नोंदणी यशस्वी (Registration Successful)", Toast.LENGTH_SHORT).show();
                        sendUserData();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Registration.this, FirstActivity.class));
                    }else {
                        Toast.makeText(Registration.this, "नोंदणी अयशस्वी (Registration Failed) !!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        signup.setOnClickListener(view -> startActivity(new Intent(Registration.this, FirstActivity.class)));
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Patient").child(firebaseAuth.getUid());
        UserProfile userProfile = new UserProfile(name, age, mobile, gen2, ehelp);
        databaseReference.setValue(userProfile);
    }

    public boolean validate(){
        Boolean result = false;

        name = Ename.getText().toString();
        email = Eemail.getText().toString();
        password = Epassword.getText().toString();
        age = Eage.getText().toString();
        mobile = Emobile.getText().toString();
        ehelp = Eemergency.getText().toString();
        gen2 = gen;

        if (name.isEmpty()){
            Ename.setError("आपले नाव टाइप करा");
            Ename.requestFocus();
        }else if(email.isEmpty()){
            Eemail.setError("आपला मोबाइल क्रमांक टाइप करा");
            Eemail.requestFocus();
        }else if(password.isEmpty()){
            Eemail.setError("आपला ईमेल टाइप करा");
            Eemail.requestFocus();
        }else if(age.isEmpty()){
            Eemail.setError("आपले वय टाइप करा");
            Eemail.requestFocus();
        }else if(mobile.isEmpty()){
            Eemail.setError("आपला पासवर्ड टाइप करा");
            Eemail.requestFocus();
        }else if(ehelp.isEmpty()){
            Eemergency.setError("आपातकालीन संपर्क क्रमांक टाइप करा");
            Eemergency.requestFocus();
        }else{
            result = true;
        }
        return result;

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class SpinnerClass implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            gen = gender[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}