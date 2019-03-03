package in.indekode.hrushi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class D_P_Activity extends AppCompatActivity {

    TextView doctor, patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d__p_);

        doctor =findViewById(R.id.Doctor);
        patient = findViewById(R.id.Patient);

        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(D_P_Activity.this, DoctorLoginActivity.class));
            }
        });

        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(D_P_Activity.this, FirstActivity.class));
            }
        });

    }
}
