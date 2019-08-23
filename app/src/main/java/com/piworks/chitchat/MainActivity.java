package com.piworks.chitchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

import bolts.Task;

public class MainActivity extends AppCompatActivity {
    public static int SIGN_IN_REQUEST_CODE = 10;
    private FirebaseListAdapter<ChatMessage> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            displayChatMessages();
        }
    }






    @Override
    public void onBackPressed() {
        finishAffinity();

    }


    private void displayChatMessages() {
        ListView listView = (ListView)findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
               // Toast.makeText(getApplicationContext(),"hello_world",Toast.LENGTH_SHORT).show();
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listView.setAdapter(adapter);



    }

    public void sendmessage(View view) {
        EditText input = (EditText)findViewById(R.id.editext_mymessage);
        FirebaseDatabase.getInstance()
                .getReference()
                .push()
                .setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName())
                );

        // Clear the input
        input.setText("");
    }

    public void domenu(View view) {


        final AlertDialog.Builder a1 = new AlertDialog.Builder(this);
        a1.setTitle("Sign Out");
        a1.setMessage("Are you Sure?");
        a1.setPositiveButton("Yes" , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AuthUI.getInstance().signOut(MainActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have been signed out.",
                                                Toast.LENGTH_LONG)
                                                .show();

                                        // Close activity
                                        finishAffinity();
                                    }

                                });


                    }
                }


        );
        a1.setNegativeButton("No" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        a1.show();
        a1.setCancelable(false);




    }

    public void dolisten(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 100);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    EditText e1 = (EditText)findViewById(R.id.editext_mymessage);
                    e1.setText(result.get(0));
                }
                break;
            case 10:
                if(requestCode == SIGN_IN_REQUEST_CODE) {
                    if(resultCode == RESULT_OK) {
                        Toast.makeText(this,
                                "Successfully signed in. Welcome!",
                                Toast.LENGTH_LONG)
                                .show();
                        displayChatMessages();
                    } else {
                        Toast.makeText(this,
                                "We couldn't sign you in. Please try again later.",
                                Toast.LENGTH_LONG)
                                .show();

                        // Close the app
                        finishAffinity();
                    }
                }

        }
    }
}
