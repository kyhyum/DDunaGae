package com.ddunagae.ddunagae;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ddunagae.ddunagae.model.Accommodation_Model;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Category_Accommodation extends AppCompatActivity {

    private RecyclerView recyclerView;

    WebView webView;

    ArrayList<String> city = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> address = new ArrayList<>();

    ListView accommodation_city;

    ArrayList<String> city_selected = new ArrayList<>();
    ArrayAdapter adapter;

    TextView textView;

    String selected_city;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_accommodation);

        webView = findViewById(R.id.accommodation_webView);
        String url;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setUserAgentString("app");
        url = "https://map.kakao.com/";

        webView.loadUrl(url);

        textView = findViewById(R.id.textView);

        accommodation_city = findViewById(R.id.accommodation_city);
        city_selected.add("서울");
        city_selected.add("인천");
        city_selected.add("경기도");
        city_selected.add("강원도");
        city_selected.add("충청도");
        city_selected.add("전라도");
        city_selected.add("경상도");
        city_selected.add("제주도");
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, city_selected);
        accommodation_city.setAdapter(adapter);

        recyclerView = findViewById(R.id.accommodation_list);
        recyclerView.setLayoutManager(new LinearLayoutManager( this));

        accommodation_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_city = city_selected.get(position);

                textView.setText(selected_city + " 반려동물 동반 가능 숙소");
                recyclerView.setAdapter(new AccommodationRecyclerViewAdapter(selected_city));
            }
        });
    }

    InputStream inputStream;

    class AccommodationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<Accommodation_Model> contents;

        public AccommodationRecyclerViewAdapter(String str) {
            AssetManager assetManager = getAssets();
            contents = new ArrayList<>();
            contents.clear();

            try {
                switch (str) {
                    case "서울" :
                        recyclerView.removeAllViewsInLayout();
                        inputStream = assetManager.open("jsons/1_Seoul_Pet-friendly_Accommodation.json");
                        break;
                    case "인천":
                        recyclerView.removeAllViewsInLayout();
                        inputStream = assetManager.open("jsons/2_Incheon_Pet-friendly_accommodation.json");
                        break;
                    case "경기도":
                        recyclerView.removeAllViewsInLayout();
                        inputStream = assetManager.open("jsons/3_Gyeonggi-do_Pet-friendly_accommodation.json");
                        break;
                    case "강원도":
                        recyclerView.removeAllViewsInLayout();
                        inputStream = assetManager.open("jsons/5_Gangwon-do_Pet-friendly_accommodation.json");
                        break;
                    case "충청도":
                        recyclerView.removeAllViewsInLayout();
                        inputStream = assetManager.open("jsons/4_Chungcheong-do_Pet-friendly_accommodation.json");
                        break;
                    case "전라도":
                        recyclerView.removeAllViewsInLayout();
                        inputStream = assetManager.open("jsons/6_Jeolla-do_Pet-friendly_accommodation.json");
                        break;
                    case "경상도":
                        recyclerView.removeAllViewsInLayout();
                        inputStream = assetManager.open("jsons/7_Gyeongsang-do_Pets-friendly_accommodation.json");
                        break;
                    case "제주도":
                        recyclerView.removeAllViewsInLayout();
                        inputStream = assetManager.open("jsons/8_Jeju-do_Pet-friendly_accommodation.json");
                        break;
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                StringBuffer buffer = new StringBuffer();
                String line = reader.readLine();
                while (line != null) {
                    buffer.append(line + "\n");
                    line = reader.readLine();
                }

                String jsonData = buffer.toString();

                JSONObject obj = new JSONObject(jsonData);
                JSONArray get_data = (JSONArray) obj.get("contents");


                for (int i = 0; i < get_data.length(); i++) {
                    JSONObject temp = get_data.getJSONObject(i);

                    String get_city = temp.getString("시/군/도");
                    String get_name = temp.getString("이름");
                    String get_address = temp.getString("주소");
                    Accommodation_Model model = new Accommodation_Model();

                    model.accommodation_title = get_name;
                    model.accommodation_address = get_address;
                    model.accommodation_city = get_city;

                    if (model.accommodation_city.equals(str)) {
                        contents.add(model);
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        @NonNull
        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accommodation,parent, false);


            return new AccommodationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
            AccommodationViewHolder AccommodationHolder = ((AccommodationViewHolder)holder);

            AccommodationHolder.address.setText(contents.get(position).accommodation_address);
            AccommodationHolder.title.setText(contents.get(position).accommodation_title);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetJavaScriptEnabled")
                @Override
                public void onClick(View v) {
                    webView = findViewById(R.id.accommodation_webView);
                    String url;

                    url = "https://map.kakao.com/?q=" + contents.get(position).accommodation_title;
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                    webView.getSettings().setUseWideViewPort(true);
                    webView.getSettings().setLoadWithOverviewMode(true);
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setUseWideViewPort(true);
                    webView.getSettings().setSupportZoom(false);
                    webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                    webView.getSettings().setAppCacheEnabled(false);
                    webView.getSettings().setDomStorageEnabled(true);
                    webView.getSettings().setAllowFileAccess(true);
                    webView.setWebChromeClient(new WebChromeClient());
                    webView.getSettings().setUserAgentString("app");

                    webView.loadUrl(url);
                }
            });
        }

        @Override
        public int getItemCount() {
            return contents.size();
        }

        private class AccommodationViewHolder extends RecyclerView.ViewHolder {

            public TextView address;
            public TextView title;

            public AccommodationViewHolder(View view) {
                super(view);
                address = (TextView)view.findViewById(R.id.accommodation_address);
                title = (TextView)view.findViewById(R.id.accommodation_title);
            }
        }
    }
}