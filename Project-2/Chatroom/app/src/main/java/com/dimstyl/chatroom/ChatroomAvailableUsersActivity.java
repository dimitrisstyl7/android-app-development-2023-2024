package com.dimstyl.chatroom;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatroomAvailableUsersActivity extends AppCompatActivity {
    private LinearLayout usersLinearLayout;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_available_users);

        users = new ArrayList<>();
        String uid = getIntent().getStringExtra("uid");
        String nickname = getIntent().getStringExtra("nickname");

        TextView userNicknameTextView = findViewById(R.id.userNicknameTextView);
        userNicknameTextView.setText(getString(R.string.signed_in_as, nickname));

        usersLinearLayout = findViewById(R.id.usersLinearLayout);
        fillLinearLayout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        signOut();
    }

    public void signOut(View view) {
        signOut();
    }

    void signOut() {
        FirebaseUtil.signOut();
        finish();
    }

    private void fillLinearLayout() {
        // Add button for chat with all users
        addUserToLinearLayout(null);

        // Add button for chat with current user (himself)
        addUserToLinearLayout(new User(FirebaseUtil.getUid(), FirebaseUtil.getEmail(), FirebaseUtil.getNickname()));

        // Add button for each registered user (except current user)
        FirebaseUtil.getAllUsers(this);
    }

    void addUserToLinearLayout(User user) {
        // Add user to users list
        users.add(user);

        // Create new button
        Button button = new Button(this);
        button.setId(users.size());

        // Set button text alignment, text size and background color
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        button.setTextSize(22);
        button.setBackgroundColor(getColor(R.color.white));

        // Set button font family
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/AkayaTelivigala-Regular.ttf");
        button.setTypeface(typeface);

        // Set button margins
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(params);
        marginParams.setMargins(0, 0, 0, 10);
        button.setLayoutParams(marginParams);

        // Set button text
        button.setAllCaps(false);
        button.setText(user.getNickname());

        // set OnclickListener
        button.setOnClickListener(view -> {
            showMessage("User info", user.toString());
        });

        // Add button to LinearLayout
        usersLinearLayout.addView(button);
    }

    void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", null)
                .show();
    }
}