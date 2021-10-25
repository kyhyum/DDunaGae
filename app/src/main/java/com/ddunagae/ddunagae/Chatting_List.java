package com.ddunagae.ddunagae;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.ddunagae.ddunagae.fragment.RoomFragment;

public class Chatting_List extends AppCompatActivity {

    private ImageView go_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_list);

        go_back = (ImageView)findViewById(R.id.matching_roomlist_img_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });


        RoomFragment roomFragment = new RoomFragment();

       Intent getintent = getIntent();
       String chatting_room_option_selector = getintent.getExtras().getString("chatting_room_option_selector");

       FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
       Bundle bundle = new Bundle();
       bundle.putString("chatting_room_option_selector",chatting_room_option_selector);

       roomFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.chatting_list_framelayout,roomFragment).commit();


    }
}

