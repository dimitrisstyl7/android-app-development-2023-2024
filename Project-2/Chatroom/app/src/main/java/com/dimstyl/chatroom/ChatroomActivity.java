package com.dimstyl.chatroom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatroomActivity extends AppCompatActivity {
    private EditText inputEditText;
    private LinearLayout messagesLinearLayout;
    private String receiverUid;
    private String receiverNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        messagesLinearLayout = findViewById(R.id.messagesLinearLayout);
        TextView chattingWithtextView = findViewById(R.id.chattingWithTextView);
        inputEditText = findViewById(R.id.inputEditText);

        receiverUid = getIntent().getStringExtra("receiverUid");
        if (receiverUid == null) {
            Log.e("ChatroomActivity", "onCreate: receiverUid == null");
            showMessage("Oops...", "Something went wrong, please try again.", "Close", (dialogInterface, i) -> finish());
            return;
        }

        receiverNickname =
                receiverUid.equals(FirebaseUtil.getUid()) ? "yourself" : getIntent().getStringExtra("receiverNickname");
        chattingWithtextView.setText(getString(R.string.chatting_with, receiverNickname));
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * Add listener for messages.
         * At the beginning, it will fill linearLayout with old messages.
         * After that, it will add new messages to linearLayout.
         * */
        FirebaseUtil.addChildEventListener(receiverUid, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        messagesLinearLayout.removeAllViews();
        inputEditText.setText("");
        FirebaseUtil.removeChildEventListeners();
    }

    void addMessageToLinearLayout(Message message) {
        TextView textView = createTextView(message);
        messagesLinearLayout.addView(textView);

        // Set focus to each message (scroll to bottom)
        messagesLinearLayout.requestChildFocus(textView, textView);
    }

    private TextView createTextView(Message message) {
        boolean isSenderCurrentUser = FirebaseUtil.isSenderCurrentUser(message.getSenderUid());

        // Create new textView
        TextView textView = new TextView(this);

        // Set text alignment
        if (isSenderCurrentUser) {
            // Message is sent by current user
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            // Message is sent by other user
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
        // Set text size
        textView.setTextSize(20);

        // Set text color
        textView.setTextColor(getColor(R.color.white));

        // Set textView font family
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/AkayaTelivigala-Regular.ttf");
        textView.setTypeface(typeface);

        // Set textView margins
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(params);
        if (isSenderCurrentUser) {
            // Message is sent by current user
            marginParams.setMargins(0, 0, 40, 0);
        } else {
            // Message is sent by other user
            marginParams.setMargins(40, 0, 0, 0);
        }
        textView.setLayoutParams(marginParams);

        // Set textView user nickname
        if (isSenderCurrentUser) {
            // Message is sent by current user
            addNicknameToLinearLayout(FirebaseUtil.getNickname(), true);
        } else if (receiverUid.equals(FirebaseUtil.EVERYONE)) {
            // Message is sent by other user to everyone
            addNicknameToLinearLayout(message.getSenderNickname(), false);
        } else {
            // Message is sent by other user to current user
            addNicknameToLinearLayout(receiverNickname, false);
        }

        // Set textView message text
        textView.setText(message.getText());

        // Disable textView all caps
        textView.setAllCaps(false);

        return textView;
    }

    private void addNicknameToLinearLayout(String nickname, boolean isSenderCurrentUser) {
        // Create new textView
        TextView textView = new TextView(this);

        // Set text alignment
        if (isSenderCurrentUser) {
            // Message is sent by current user
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            // Message is sent by other user
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }

        // Set text size
        textView.setTextSize(24);

        // Set text color
        textView.setTextColor(getColor(R.color.green));

        // Set textView font family
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/AkayaTelivigala-Regular.ttf");
        textView.setTypeface(typeface);

        // Set textView margins
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(params);
        if (isSenderCurrentUser) {
            // Message is sent by current user
            marginParams.setMargins(0, 0, 40, 0);
        } else {
            // Message is sent by other user
            marginParams.setMargins(40, 0, 0, 0);
        }
        textView.setLayoutParams(marginParams);

        // Set textView text
        textView.setText(nickname);

        // Underline textView
        textView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        // Set textView bold and italic
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC);

        // Disable textView all caps
        textView.setAllCaps(false);

        // Add textView to linearLayout
        messagesLinearLayout.addView(textView);
    }

    public void sendMessage(View view) {
        if (editTextEmpty(inputEditText)) {
            showMessage("Error", "Please write a message first!");
            return;
        }
        String messageText = inputEditText.getText().toString();
        FirebaseUtil.saveMessage(receiverUid, messageText, this);
        inputEditText.setText("");
    }

    private boolean editTextEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }

    void showMessage(String title, String message, String neutralButtonText, DialogInterface.OnClickListener onClickListenerForNeutralButton) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(neutralButtonText, onClickListenerForNeutralButton)
                .show();
    }
}