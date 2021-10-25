package com.ddunagae.ddunagae;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ddunagae.ddunagae.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile_Detail extends Activity {

    String destinationuid;
    TextView members_nickname;
    TextView members_sex;
    TextView members_age;
    TextView members_pet_type;
    ImageView member_profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profile_detail);

        members_nickname = findViewById(R.id.members_nickname);
        members_sex = findViewById(R.id.members_sex);
        members_age = findViewById(R.id.members_age);
        members_pet_type = findViewById(R.id.members_pet_type);
        member_profile = findViewById(R.id.member_profile);

        Intent intent = getIntent();

        destinationuid = intent.getStringExtra("destinationuid");

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("users").child(destinationuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                members_nickname.setText(userModel.nickname);
                members_sex.setText(userModel.my_sex);
                members_age.setText(userModel.myage);
                members_pet_type.setText(userModel.petage);
                Glide.with(Profile_Detail.this)
                        .load(userModel.imageUri)
                        .apply(new RequestOptions().circleCrop())
                        .into(member_profile);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void mOnClose(View v){
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        finish();
    }
}

