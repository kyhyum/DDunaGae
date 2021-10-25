package com.ddunagae.ddunagae;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ddunagae.ddunagae.model.UserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Mainactivity extends AppCompatActivity {

    int nCurrentPermission = 0;
    static final int PERMISSIONS_REQUEST = 0x0000001;

    LinearLayout category1;
    LinearLayout category2;
    LinearLayout category3;
    LinearLayout category4;

    DatabaseReference mDatabase;

    //네비게이션바
    DrawerLayout drawerLayout;
    View drawerView;
    ListView listview = null;
    LinearLayout my_page;
    //네비게이션바

    BottomNavigationView bottomNavigationView;

    ImageView profile_photo;
    TextView my_nickname;
    TextView unlogin;
    LinearLayout layout_account;

    ArrayList<String> Main_urls = new ArrayList<>();
    ArrayList<String> Main_titles = new ArrayList<>();
    ArrayList<String> Main_addrs = new ArrayList<>();
    ArrayList<String> Main_conIds = new ArrayList<>();

    int region_code = 1;
    private RecyclerView main_recyclerview;
    private ArrayList<MainRecyclerViewItem> mainlist;
    private MainRecyclerViewAdapter main_recyclerviewadapter;

    private BackkeyHandler backkeyHandler = new BackkeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OnCheckPermission();

        profile_photo = (ImageView) findViewById(R.id.profile_photo);
        listview = findViewById(R.id.navi_list);
        layout_account = (LinearLayout) findViewById(R.id.my_account);
        unlogin = (TextView) findViewById(R.id.my_page_unlogin);
        FirebaseUser user;

        Spinner region_spinner = findViewById(R.id.region_spinner);
        ArrayAdapter region_adapter = ArrayAdapter.createFromResource(this, R.array.region, android.R.layout.simple_spinner_dropdown_item);
        region_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        region_spinner.setAdapter(region_adapter);

        Init();
        for (int i = 0; i < Main_conIds.size(); i++) {
            addItem(Main_urls.get(i), Main_titles.get(i), Main_addrs.get(i), Main_conIds.get(i));
        }

        main_recyclerviewadapter = new MainRecyclerViewAdapter(mainlist);
        main_recyclerview.setAdapter(main_recyclerviewadapter);
        main_recyclerview.setLayoutManager(new LinearLayoutManager(Mainactivity.this));

        Uri uri = getIntent().getData();
        if (uri != null){
            List<String> params = uri.getPathSegments();
            String id = params.get(params.size()-1);
            Intent intent = new Intent(this, Search_Selected.class);
            intent.putExtra("conId", id);
            startActivity(intent);
        }

        region_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView region_travel = (TextView) findViewById(R.id.region_travel);
                region_travel.setText(region_spinner.getSelectedItem() + " 관광지");
                if (position < 8) {
                    region_code = position + 1;
                } else {
                    region_code = position + 23;
                }

                Main_api main_api = new Main_api(String.valueOf(region_code));
                Thread main_thread = new Thread(main_api);

                try {
                    main_thread.start();
                    main_thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Main_urls = main_api.getMain_urls();
                Main_titles = main_api.getMain_titles();
                Main_addrs = main_api.getMain_addrs();
                Main_conIds = main_api.getMain_contentids();

                Init();
                for (int i = 0; i < Main_conIds.size(); i++) {
                    addItem(Main_urls.get(i), Main_titles.get(i), Main_addrs.get(i), Main_conIds.get(i));
                }

                main_recyclerviewadapter = new MainRecyclerViewAdapter(mainlist);
                main_recyclerview.setAdapter(main_recyclerviewadapter);
                main_recyclerview.setLayoutManager(new LinearLayoutManager(Mainactivity.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.matching:
                        Intent intent = new Intent(getApplicationContext(), New_ChatMainActivity.class);
                        startActivity(intent);

                        break;

                    case R.id.home:
                        Intent intent2 = new Intent(getApplicationContext(), Mainactivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.freeboard:
                        Intent intent3 = new Intent(getApplicationContext(), Freeboard_Activity.class);
                        startActivity(intent3);

                        break;

                }
                return false;
            }
        });

        //네비게이션바(햄버거) 클릭 이벤트 삽입 구간
        drawerLayout = findViewById(R.id.drawlayout);
        drawerView = findViewById(R.id.drawer);

        ImageButton nvg_open = findViewById(R.id.nvg_open);
        nvg_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        ImageButton nvg_close = findViewById(R.id.nvg_close);
        nvg_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        List<String> list = new ArrayList<>();
        list.add("공지사항");
        list.add("이벤트");
        list.add("고객센터");
        list.add("이용약관");
        list.add("개인정보 처리방식");
        list.add("로그인");


        listview = findViewById(R.id.navi_list);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            list.set(5, "로그아웃");

            layout_account.setVisibility(View.VISIBLE);
            my_nickname = (TextView) findViewById(R.id.my_page_login);
            String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(myuid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    Glide.with(Mainactivity.this)
                            .load(userModel.imageUri)
                            .apply(new RequestOptions().circleCrop())
                            .into(profile_photo);
                    my_nickname.setText(userModel.nickname);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
            my_nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), My_Page.class);
                    startActivity(intent);
                }
            });

        } else {
            layout_account.setVisibility(View.GONE);
            unlogin.setVisibility(View.VISIBLE);
            unlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Login_New_Page.class);
                    startActivity(intent);
                }
            });
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.
                simple_list_item_1, list);

        listview.setAdapter(adapter);


        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtime database 에서 정보 가져오기

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent_notice =
                                new Intent(getApplicationContext(), Notice.class);
                        intent_notice.putExtra("key","1");
                        startActivity(intent_notice);
                        break;

                    case 1:
                        // 공지사항이랑 이벤트는 같은 페이지임 나중에 이벤트 클릭된 상태로 넘어가게 하면됨
                        Intent intent_event =
                                new Intent(getApplicationContext(), Notice.class);
                        intent_event.putExtra("key","2");
                        startActivity(intent_event);
                        break;

                    case 2:
                        Intent intent_service_center =
                                new Intent(getApplicationContext(), Service_Center.class);
                        startActivity(intent_service_center);
                        break;

                    case 3:
                        Intent intent_terms_of_service = new Intent(getApplicationContext(), Terms_of_service.class);
                        startActivity(intent_terms_of_service);
                        break;

                    case 4:
                        Intent intent_personal = new Intent(getApplicationContext(), Personal_information.class);
                        startActivity(intent_personal);
                        break;

                    case 5:
                        if (list.get(5).equals("로그아웃")) {
                            AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
                            alt_bld.setMessage("로그아웃 하시겠습니까?").setCancelable(false)
                                    .setPositiveButton("네",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    FirebaseAuth.getInstance().signOut();
                                                    Intent intent = getIntent();
                                                    finish();
                                                    overridePendingTransition(0, 0);
                                                    startActivity(intent);
                                                    overridePendingTransition(0, 0);
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
                            alert.setTitle("로그아웃");
                            alert.setIcon(R.drawable.logo);
                            // 대화창 배경 색 설정
                            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 220, 213)));
                            alert.show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), Login_New_Page.class);
                            startActivity(intent);
                        }
                        break;

                }
            }
        });

        my_page = findViewById(R.id.my_account);
        my_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), My_Page.class);
                startActivity(intent);
            }
        });

        //네비게이션바

        Intent intent = getIntent();

        String new_id;
        String new_pw;
        String new_animal;
        String animal_info;

        new_id = getIntent().getStringExtra("NEW_ID");
        new_pw = getIntent().getStringExtra("NEW_PW");
        new_animal = getIntent().getStringExtra("ANIMAL_INFO");
        animal_info = getIntent().getStringExtra("ANIMAL_MORE_INFO");

        onTextViewClick();
    }


    //네비게이션바
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
    //네비게이션바

    // serach box
    String url;
    GpsTracker gpsTracker;
    double latitude;
    double longitude;

    public void onTextViewClick() {
        category1 = (LinearLayout) findViewById(R.id.category_1);
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(Mainactivity.this, "카카오맵으로 이동합니다.", Toast.LENGTH_SHORT).show();

                    gpsTracker = new GpsTracker(Mainactivity.this);
                    latitude = gpsTracker.latitude;
                    longitude = gpsTracker.longitude;
                    url = "kakaomap://search?q=애견카페" +
                            "&p=" + latitude + "," + longitude;

                    System.out.println(latitude + ", " + longitude);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Mainactivity.this, "카카오맵 설치를 위해 Play Store로 이동합니다.", Toast.LENGTH_SHORT).show();

                    url = "https://play.google.com/store/apps/details?id=net.daum.android.map";

                    Intent intent_error = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent_error);
                }

            }
        });
        category2 = (LinearLayout) findViewById(R.id.category_2);
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Category_Accommodation.class);

                startActivity(intent);
            }
        });
        category3 = (LinearLayout) findViewById(R.id.category_3);
        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(Mainactivity.this, "카카오맵으로 이동합니다.", Toast.LENGTH_SHORT).show();

                    url = "kakaomap://search?q=애견미용실" +
                            "&p=" + gpsTracker.latitude + "," + gpsTracker.longitude;

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Mainactivity.this, "카카오맵 설치를 위해 Play Store로 이동합니다.", Toast.LENGTH_SHORT).show();

                    url = "https://play.google.com/store/apps/details?id=net.daum.android.map";

                    Intent intent_error = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent_error);
                }
            }
        });
        category4 = (LinearLayout) findViewById(R.id.category_4);
        category4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(Mainactivity.this, "카카오맵으로 이동합니다.", Toast.LENGTH_SHORT).show();

                    url = "kakaomap://search?q=동물병원" +
                            "&p=" + gpsTracker.latitude + "," + gpsTracker.longitude;

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Mainactivity.this, "카카오맵 설치를 위해 Play Store로 이동합니다.", Toast.LENGTH_SHORT).show();

                    url = "https://play.google.com/store/apps/details?id=net.daum.android.map";

                    Intent intent_error = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent_error);
                }
            }
        });
    }

    public void Init() {
        main_recyclerview = (RecyclerView) findViewById(R.id.main_recyclerview);
        mainlist = new ArrayList<>();
    }

    public void addItem(String url, String title, String addr, String conId) {
        MainRecyclerViewItem main_item = new MainRecyclerViewItem();

        main_item.setUrl(url);
        main_item.setTitle(title);
        main_item.setAddr(addr);
        main_item.setConId(conId);
        mainlist.add(main_item);
    }

    public void OnCheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정하셔야 합니다.", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST);
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "앱 실행을 위한 권한이 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
        backkeyHandler.onBackPressed("한번 더 누르시면 종료됩니다.", 5);
    }
}