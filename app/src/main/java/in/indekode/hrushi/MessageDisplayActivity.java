package in.indekode.hrushi;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MessageDisplayActivity extends AppCompatActivity {

    EditText et_msg;
    ImageButton voice_ibtn, send_ibtn;
    final int RESULT_SPEECH = 100;
    TextToSpeech mTextToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_display);

        et_msg =findViewById(R.id.text_send);
        voice_ibtn = findViewById(R.id.img_btn_voice);
        send_ibtn = findViewById(R.id.img_btn_send);

        voice_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVoiceInput();
            }
        });

    }

    private void getVoiceInput() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent data){
        super.onActivityResult(requestCode, resultcode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultcode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    et_msg.setText(text.get(0));
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
}