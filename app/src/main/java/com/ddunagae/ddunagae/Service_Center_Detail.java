package com.ddunagae.ddunagae;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ddunagae.ddunagae.database.Service_Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Service_Center_Detail extends AppCompatActivity {

    private TextView title;
    private TextView content;

    private ImageView photo;
    private ImageView go_back;

    private String articleid;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user != null ? user.getUid() : null;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_center_detail);

        uid = FirebaseAuth.getInstance().getUid();
        Intent intent = getIntent();
        articleid =  intent.getStringExtra("articleid");




        title = (TextView)findViewById(R.id.service_center_detail_title) ;
        content = (TextView)findViewById(R.id.service_center_detail_content);

        photo = (ImageView)findViewById(R.id.service_center_image);


        go_back = (ImageView)findViewById(R.id.service_center_detail_img_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Service_Center").child(articleid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Service_Database article;
                article = snapshot.getValue(Service_Database.class);
                title.setText(article.title);
                content.setText(article.content);

                Glide.with(Service_Center_Detail.this)
                        .load(article.imageUri)
                        .apply(new RequestOptions().circleCrop())
                        .into(photo);
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    public void set_answer_true(String articleid){


                FirebaseDatabase.getInstance().getReference().child("Service_Center").child(articleid).child("have_answer").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


    }

}

