package in.indekode.hrushi;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorActivity extends AppCompatActivity {

    TextView dr, patient, date, time;
    String Date, Time, Patientname, Drname;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        dr = findViewById(R.id.Dr_name);
        patient = findViewById(R.id.Patient_name);
        date = findViewById(R.id.Date);
        time = findViewById(R.id.Time);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Appointments");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                AppointmentProfile appointmentProfile = dataSnapshot.getValue(AppointmentProfile.class);
                try {
                    Date = appointmentProfile.date;
                    Time = appointmentProfile.time;
                    Patientname = appointmentProfile.patientname;
                    Drname = appointmentProfile.drname;

                    dr.setText("Dr. Name : " + Drname);
                    patient.setText("Patient Name : "+ Patientname);
                    date.setText("Date : "+ Date);
                    time.setText("Time : "+ Time);

                }catch (NullPointerException ex){
                    Toast.makeText(DoctorActivity.this, "Error in firebase connectivity", Toast.LENGTH_SHORT).show();
                }
            }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorActivity.this,databaseError.getCode(),Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout:
                firebaseAuth.signOut();
                finish();
                Toast.makeText(DoctorActivity.this, "यशस्वीरित्या साइन आउट केले", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DoctorActivity.this, DoctorLoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
