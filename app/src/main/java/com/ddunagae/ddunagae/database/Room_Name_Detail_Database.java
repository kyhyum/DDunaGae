package com.ddunagae.ddunagae.database;

public class Room_Name_Detail_Database {


    public String Room_name;
    public String master_uid;
    public String chatting_room_option_selector;
    public String group_member_number;

    public Room_Name_Detail_Database() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Room_Name_Detail_Database(String room_name, String master_uid, String chatting_room_option_selector, String group_member_number) {
        this.chatting_room_option_selector = chatting_room_option_selector;
        this.Room_name = room_name;
        this.master_uid = master_uid;
        this.group_member_number = group_member_number;

    }

}