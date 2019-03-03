package in.indekode.hrushi;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import com.google.cloud.dialogflow.v2beta1.TextInput;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;

    private static final String API_KEY = "AIzaSyBa8IxFrGti6qwe07CygDQnRE5qbdQvUmQ";

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;

    ImageButton voice_ibtn, bot_voice;
    public String v_msg;
    String emergency_phone_no;
    // TTS
    final int RESULT_SPEECH = 100;
    TextToSpeech mTextToSpeech;

    // Java V2
    private SessionsClient sessionsClient;
    private SessionName session;
    final Handler th = new Handler();

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    String date=" ", time=" ", R_name=" ", R_name2=" ",year=" ", month=" ", day=" ", hrs=" ", min=" ";
    private long startTime,endTime;
    String username, drno;
    TextToSpeech speech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));

        voice_ibtn = findViewById(R.id.img_btn_voice);

        speech = new TextToSpeech(this, this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Patient").child(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                try{
                    emergency_phone_no = userProfile.ehelp;
                    username = userProfile.name;

                }catch (NullPointerException ex){
                    Toast.makeText(MainActivity.this, " ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,databaseError.getCode(),Toast.LENGTH_SHORT).show();
            }
        });



        chatLayout = findViewById(R.id.chatLayout);

        initV2Chatbot();
        showTextView("नमस्कार, मी तुमची मदत कशी करू शकतो?", BOT);

        voice_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.getVoiceInput(view);
            }
        });

//        bot_voice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                voiceOP(BResponce);
//            }
//        });

    }


    private void initV2Chatbot() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.test_agent_credentials);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials)credentials).getProjectId();
            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getVoiceInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "कृपया बाबाजीशी बोला...");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        try{
            startActivityForResult(intent, RESULT_SPEECH);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),"अरेरे! तुमचा मोबाइल मायक्रोफोनला समर्थन देत नाही..", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent data){
        super.onActivityResult(requestCode, resultcode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultcode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    v_msg = text.get(0);
                    if (v_msg.trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter your query!", Toast.LENGTH_LONG).show();
                    } else {

                        final AsyncTask<Void, Void, Void> de = new AsyncTask<Void, Void, Void>() {
                            @SuppressLint("WrongThread")
                            @Override
                            protected Void doInBackground(Void... voids) {

                                TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                                final Translate translate = options.getService();
                                final Translation translation = translate.translate(v_msg, Translate.TranslateOption.targetLanguage("en"));
                                th.post(() -> {
                                    String MrUserReply = translation.getTranslatedText();
                                    showTextView(v_msg, USER);

                                    //Reminder process
                                    String[] split_reply = MrUserReply.split(" ");

                                    for (String sr : split_reply) {
                                        if (sr.equals("emergency") || sr.equals("call") || sr.equals("help") || sr.equals("Emergency") || sr.equals("Call") || sr.equals("Help")) {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:"+emergency_phone_no));
                                            startActivity(callIntent);
                                        }else if (sr.equals("ambulance") || sr.equals("Ambulance")) {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:"+"108"));
                                            startActivity(callIntent);
                                        }else {
                                            // Java V2
                                            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(MrUserReply).setLanguageCode("en-US")).build();
                                            new RequestJavaV2Task(MainActivity.this, session, sessionsClient, queryInput).execute();
                                            break;
                                        }
                                    }

                                });
                                return null;
                            }
                        }.execute();
                    }
                }
                break;
            }
        }

        mTextToSpeech=new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                mTextToSpeech.setLanguage(new Locale("mr","IND"));
            }
        });
    }

    public void voiceOP(String responce){
        CharSequence charSequence = responce;
        speech.speak(charSequence, TextToSpeech.QUEUE_FLUSH, null, "id1");
    }

    @SuppressLint("StaticFieldLeak")
    public void callbackV2(DetectIntentResponse response) {
        if (response != null) {

            String botReply = response.getQueryResult().getFulfillmentText();

            try{
                String[] splitted_BR = botReply.split(" ");
                for(String ss : splitted_BR) {

                    //For Appointment
                    if (ss.equals("Scheduled!!")) {
                        for (String aSplitted_BR : splitted_BR) {
                            date = splitted_BR[1];
                            time = splitted_BR[3];
                            R_name = splitted_BR[5];
                            R_name2 = splitted_BR[6];
                        }
                        String[] split_date = date.split("-");
                        for (String aSplit_date : split_date) {
                            year = split_date[0];
                            month = split_date[1];
                            day = split_date[2];
                        }
                        String[] split_time = time.split(":");
                        for (String aSplit_time : split_time) {
                            hrs = split_time[0];
                            min = split_time[1];
                        }
                        drno= R_name +" "+R_name2;
                        String reminder_name = "Appointment Booked with " + drno ;
                        CalenderActivity(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hrs), Integer.parseInt(min), reminder_name);
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("Appointments");
                        AppointmentProfile appointmentProfile = new AppointmentProfile(username, drno, date, time);
                        databaseReference.setValue(appointmentProfile);

                        //For Reminder
                    }else  if (ss.equals("Scheduled!!")) {
                        for (String aSplitted_BR : splitted_BR) {
                            date = splitted_BR[1];
                            time = splitted_BR[3];
                            R_name = splitted_BR[5];
                            R_name2 = splitted_BR[6];
                        }
                        String[] split_date = date.split("-");
                        for (String aSplit_date : split_date) {
                            year = split_date[0];
                            month = split_date[1];
                            day = split_date[2];
                        }
                        String[] split_time = time.split(":");
                        for (String aSplit_time : split_time) {
                            hrs = split_time[0];
                            min = split_time[1];
                        }
                        drno= R_name +" "+R_name2;
                        String reminder_name = "Appointment Booked with " + drno ;
                        CalenderActivity(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hrs), Integer.parseInt(min), reminder_name);
                    }
                }
            }catch (NumberFormatException ex) {
                Toast.makeText(MainActivity.this, " Error ", Toast.LENGTH_SHORT).show();
            }catch ( ArrayIndexOutOfBoundsException index){
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

            final AsyncTask<Void, Void, Void> de = new AsyncTask<Void, Void, Void>() {
                @SuppressLint("WrongThread")
                @Override
                protected Void doInBackground(Void... voids) {

                    TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                    final Translate translate = options.getService();
                    final Translation translation = translate.translate(botReply, Translate.TranslateOption.targetLanguage("mr"));
                    th.post(() -> {
                        String mrbotReply = translation.getTranslatedText();
                        Log.d(TAG, "Bot Reply: " + mrbotReply);
                        showTextView(mrbotReply, BOT);
                        voiceOP(mrbotReply);
                    });
                    return null;
                }
            }.execute();
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("संवाद साधण्यात समस्या आली. कृपया पुन्हा प्रयत्न करा!", BOT);
        }
    }

    private void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }


    public void CalenderActivity(int year, int mnth, int day, int hrs, int min, String summary){
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(year, mnth, day, hrs, min);
        startTime = beginCal.getTimeInMillis();

        Calendar endCal = Calendar.getInstance();
        endCal.set(year, mnth, day, hrs, min);
        endTime = endCal.getTimeInMillis();

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, summary);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, summary);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginCal.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.STATUS, 1);
        intent.putExtra(CalendarContract.Events.VISIBLE, 0);
        intent.putExtra(CalendarContract.Events.HAS_ALARM, 1);
        try {
            startActivity(intent);
        }catch (ActivityNotFoundException ErrVar) {
            Toast.makeText(MainActivity.this, "कॅलेंडर ॲप डाऊनलोड करा", Toast.LENGTH_LONG).show();
        }

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
                Toast.makeText(MainActivity.this, "यशस्वीरित्या साइन आउट केले", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, FirstActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInit(int status) {
        if ( status == TextToSpeech.SUCCESS){
            int result = speech.setLanguage(Locale.forLanguageTag("hin"));

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(MainActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(MainActivity.this, "Initialization Failed", Toast.LENGTH_SHORT).show();
        }
    }
}