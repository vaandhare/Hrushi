package in.indekode.hrushi;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;

    private static final String API_KEY = "AIzaSyDoR8abbyu_4BKomJClKA2y1_Sn_M9PJ3A";

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;

    ImageButton voice_ibtn;
    public String v_msg;

    // TTS
    final int RESULT_SPEECH = 100;
    TextToSpeech mTextToSpeech;

    // Java V2
    private SessionsClient sessionsClient;
    private SessionName session;

    final Handler th = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));

        voice_ibtn = findViewById(R.id.img_btn_voice);

        chatLayout = findViewById(R.id.chatLayout);

        initV2Chatbot();
        showTextView("नमस्कार, मी तुमची मदत कशी करू शकतो?", BOT);

        voice_ibtn.setOnClickListener(this::getVoiceInput);
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "कृपया बाबाजीशी बोला...\n");
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
                                th.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String MrUserReply = translation.getTranslatedText();
                                        showTextView(v_msg, USER);

                                        // Java V2
                                        QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(MrUserReply).setLanguageCode("en-US")).build();
                                        new RequestJavaV2Task(MainActivity.this, session, sessionsClient, queryInput).execute();

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

        mTextToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(new Locale("mr","IND"));
                }
            }
        });
    }

//    @SuppressLint("StaticFieldLeak")
//    private void sendMessage(View view) {
//        String msg = queryEditText.getText().toString();
//        Toast.makeText(MainActivity.this, v_msg, Toast.LENGTH_SHORT).show();
//
//        if (msg.trim().isEmpty()) {
//            Toast.makeText(MainActivity.this, "Please enter your query!", Toast.LENGTH_LONG).show();
//        } else {
//
//            final AsyncTask<Void, Void, Void> de = new AsyncTask<Void, Void, Void>() {
//                @SuppressLint("WrongThread")
//                @Override
//                protected Void doInBackground(Void... voids) {
//
//                    TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
//                    final Translate translate = options.getService();
//                    final Translation translation = translate.translate(msg, Translate.TranslateOption.targetLanguage("en"));
//                    th.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            String MrUserReply = translation.getTranslatedText();
//                            showTextView(msg, USER);
//                            queryEditText.setText("");
//
//                            // Android client
//                //            aiRequest.setQuery(msg);
//                //            RequestTask requestTask = new RequestTask(MainActivity.this, aiDataService, customAIServiceContext);
//                //            requestTask.execute(aiRequest);
//
//                            // Java V2
//                            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(MrUserReply).setLanguageCode("en-US")).build();
//                            new RequestJavaV2Task(MainActivity.this, session, sessionsClient, queryInput).execute();
//
//                        }
//                    });
//                    return null;
//                }
//            }.execute();
//
//        }
//    }

    @SuppressLint("StaticFieldLeak")
    public void callbackV2(DetectIntentResponse response) {
        if (response != null) {
            // process aiResponse here
            String botReply = response.getQueryResult().getFulfillmentText();

            final AsyncTask<Void, Void, Void> de = new AsyncTask<Void, Void, Void>() {
                @SuppressLint("WrongThread")
                @Override
                protected Void doInBackground(Void... voids) {

                    TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                    final Translate translate = options.getService();
                    final Translation translation = translate.translate(botReply, Translate.TranslateOption.targetLanguage("mr"));
                    th.post(new Runnable() {
                        @Override
                        public void run() {
                            String mrbotReply = translation.getTranslatedText();
                            Log.d(TAG, "Bot Reply: " + mrbotReply);
                            showTextView(mrbotReply, BOT);
                        }
                    });
                    return null;
                }
            }.execute();
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
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
//        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }

}