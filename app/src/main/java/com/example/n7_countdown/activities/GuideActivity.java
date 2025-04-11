package com.example.n7_countdown.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.R;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // Thiết lập nút quay lại
        ImageView ivClose = findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> finish());

        // Có thể thêm logic khác nếu cần (ví dụ: hiển thị nội dung động)
    }
}