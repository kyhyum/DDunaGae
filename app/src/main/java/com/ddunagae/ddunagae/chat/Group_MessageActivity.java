package com.ddunagae.ddunagae.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ddunagae.ddunagae.New_ChatMainActivity;
import com.ddunagae.ddunagae.Profile_Detail;
import com.ddunagae.ddunagae.R;
import com.ddunagae.ddunagae.model.ChatModel;
import com.ddunagae.ddunagae.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group_MessageActivity extends AppCompatActivity {
    Map<String, UserModel> users = new HashMap<>();


    String uid;
    EditText editText;

    String chatroomuid;
    String chat_masterUid;
    String room_name;
    String option_selector;
    Button chattingroom_exit;

    ImageButton group_chat_hbg;
    ImageButton groupchat_goback;



    DrawerLayout drawerLayout;
    View drawerView;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private RecyclerView recyclerView;

    private RecyclerView groupmember_recyclerView;

    List<ChatModel.Comment> comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_message_activity);

        //햄버거

        drawerLayout = findViewById(R.id.chat_drawlayout);
        drawerView = findViewById(R.id.chatting_drawer);

        group_chat_hbg = (ImageButton) findViewById(R.id.talkmenu_open);

        groupchat_goback = (ImageButton)findViewById(R.id.group_chattingroom_back);
        groupchat_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });

        groupmember_recyclerView = (RecyclerView)findViewById(R.id.chatting_drawer_recyclerview);
        groupmember_recyclerView.setAdapter(new GroupMemberRecyclerViewAdapter());
        groupmember_recyclerView.setLayoutManager(new LinearLayoutManager(Group_MessageActivity.this));


        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText = (EditText) findViewById(R.id.group_messageActivity_editText);

        Intent intent = getIntent();
        chat_masterUid = intent.getStringExtra("chat_masterUid");
        room_name = intent.getStringExtra("room_name");
        option_selector = intent.getStringExtra("option_selector");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);




        FirebaseDatabase.getInstance().getReference().child("users").child(chat_masterUid).child("my_chatting_list").child("그룹 채팅방").child(room_name).child("chatroomuid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chatroomuid = snapshot.getValue().toString();
                FirebaseDatabase.getInstance().getReference().child("chatting_room").child(option_selector).child("Room_Name").child(room_name).child("talk").child(chatroomuid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserModel  userModel = new UserModel();
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            users.put(item.getKey(),userModel);
                        }

                        init();
                        recyclerView = (RecyclerView) findViewById(R.id.group_messageActivity_recyclerview);
                        recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
                        recyclerView.setLayoutManager(new LinearLayoutManager(Group_MessageActivity.this));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        chattingroom_exit = (Button)findViewById(R.id.group_room_exit);

        chattingroom_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(Group_MessageActivity.this);
                alt_bld.setMessage("방을 나가시겠습니까??").setCancelable(false)
                        .setPositiveButton("네",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FirebaseDatabase.getInstance().getReference().child("users").child(chat_masterUid).child("my_chatting_list").child("그룹 채팅방").child(room_name).child("chatroomuid").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                chatroomuid = snapshot.getValue().toString();
                                                FirebaseDatabase.getInstance().getReference().child("chatting_room").child(option_selector).child("Room_Name").child(room_name).child("talk").child(chatroomuid).child("users").child(uid).setValue(null);
                                                if(uid.equals(chat_masterUid))
                                                {
                                                    FirebaseDatabase.getInstance().getReference().child("chatting_room").child(option_selector).child("Room_Name").child(room_name).child("talk").child(chatroomuid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        String[] user = new String[8];
                                                        @Override
                                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                            for (DataSnapshot item : snapshot.getChildren()) {
                                                                int i=0;
                                                                user[i] = item.getKey();
                                                                i++;
                                                            }
                                                            if(user[1]==null){
                                                                FirebaseDatabase.getInstance().getReference().child("chatting_room").child(option_selector).child("Room_Name").child(room_name).setValue(null);
                                                            }
                                                            else{
                                                                FirebaseDatabase.getInstance().getReference().child("chatting_room").child(option_selector).child("Room_Name").child(room_name).child("master_uid").setValue(user[0]);
                                                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("my_chatting_list").child("그룹 채팅방").child(room_name).setValue(null);
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                                        }
                                                    });
                                                }
                                                else{
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("my_chatting_list").child("그룹 채팅방").child(room_name).setValue(null);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                                        onBackPressed();
                                    }
                                }).setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alt_bld.create();
                // 대화창 클릭시 뒷 배경 어두워지는 것 막기
                //alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alert.setTitle("정말 나가개?");
                alert.setIcon(R.drawable.logo);
                // 대화창 배경 색 설정
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 220, 213)));
                alert.show();

            }
        });

     /*   group_chat_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), My_ChatFragment.class);
                startActivity(intent);
            }
        });

*/

        group_chat_hbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    void init() {
        Button button = (Button) findViewById(R.id.group_messageActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();

                FirebaseDatabase.getInstance().getReference().child("chatting_room").child(option_selector).child("Room_Name").child(room_name).child("talk").child(chatroomuid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editText.setText("");
                    }
                });

            }
        });
    }

    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public GroupMessageRecyclerViewAdapter() {

            getMessageList();
        }

        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatting_room").child(option_selector).child("Room_Name").child(room_name).child("talk").child(chatroomuid).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                        comment_motify.readUsers.put(uid, true);

                        readUsersMap.put(key, comment_motify);
                        comments.add(comment_origin);
                    }
                    if (comments.size() == 0) {
                        comments.clear();

                    }
                    else if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {


                        FirebaseDatabase.getInstance().getReference().child("chatting_room").child(option_selector).child("Room_Name").child(room_name).child("talk").child(chatroomuid).child("comments")
                                .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size() - 1);
                            }
                        });
                    } else {
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size() - 1);
                    }
                    //메세지가 갱신


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_message, parent, false);


            return new GroupMessageViewHodler(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            final GroupMessageViewHodler messageViewHolder = ((GroupMessageViewHodler) holder);


            //내가보낸 메세지
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                //setReadCounter(position, messageViewHolder.textView_readCounter_left);

                //상대방이 보낸 메세지

            } else {
                FirebaseDatabase.getInstance().getReference().child("users").child(comments.get(position).uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        UserModel userModel =  snapshot.getValue(UserModel.class);
                        Glide.with(holder.itemView.getContext())
                                .load(userModel.imageUri)
                                .apply(new RequestOptions().circleCrop())
                                .into(messageViewHolder.imageView_profile);
                        messageViewHolder.textview_name.setText(userModel.nickname);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                //setReadCounter(position, messageViewHolder.textView_readCounter_right);



            }


        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class GroupMessageViewHodler extends RecyclerView.ViewHolder {

            public TextView textView_message;
            public TextView textview_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;

            public GroupMessageViewHodler(View view) {
                super(view);

                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textview_name = (TextView) view.findViewById(R.id.messageItem_textview_name);
                imageView_profile = (ImageView) view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_main);
                textView_readCounter_left = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_right);
            }
        }
    }



    class GroupMemberRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        List<String> members;

        Intent intent = getIntent();
        String chat_masterUid = intent.getStringExtra("chat_masterUid");
        String room_name = intent.getStringExtra("room_name");
        String option_selector = intent.getStringExtra("option_selector");

        public GroupMemberRecyclerViewAdapter() {

            members = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(chat_masterUid).child("my_chatting_list").child("그룹 채팅방").child(room_name).child("chatroomuid").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    String chatroomuid1 = snapshot.getValue().toString();
                    getmemberslist(chatroomuid1);
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

        public void getmemberslist(String chatroomuid){
            FirebaseDatabase.getInstance().getReference().child("chatting_room").child(option_selector).child("Room_Name").child(room_name).child("talk").child(chatroomuid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    members.clear();
                    for (DataSnapshot item : snapshot.getChildren()) {
                        members.add(item.getKey());
                    }
                    notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                }
            });
        }

        @NonNull
        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom_people, parent, false);
            return new GroupMemberViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
            GroupMemberViewHolder groupMemberViewHolder = (GroupMemberViewHolder)holder;

            FirebaseDatabase.getInstance().getReference().child("users").child(members.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    groupMemberViewHolder.group_member_nickname.setText(userModel.nickname);

                    Glide.with(holder.itemView.getContext())
                            .load(userModel.imageUri)
                            .apply(new RequestOptions().circleCrop())
                            .into(groupMemberViewHolder.group_member_profile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseDatabase.getInstance().getReference().child("users").child(members.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel userModel = snapshot.getValue(UserModel.class);

                            Intent intent1 = new Intent(getApplicationContext(), Profile_Detail.class);
                            intent1.putExtra("destinationuid", userModel.uid);
                            startActivityForResult(intent1, 1);

                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });

                }
            });
        }

        @Override
        public int getItemCount() {
            return members.size();
        }

        private class GroupMemberViewHolder extends RecyclerView.ViewHolder {

            public TextView group_member_nickname;
            public ImageView group_member_profile;


            public GroupMemberViewHolder(View view) {
                super(view);

                group_member_nickname = (TextView)view.findViewById(R.id.memeber_nickname);
                group_member_profile = (ImageView)view.findViewById(R.id.memeber_profile);

            }
        }


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopPlay(); //이 액티비티에서 종료되어야 하는 활동 종료시켜주는 함수
        Intent intent = new Intent(Group_MessageActivity.this, New_ChatMainActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    private void stopPlay() {
    }

}