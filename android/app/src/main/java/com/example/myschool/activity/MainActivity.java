package com.example.myschool.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myschool.dialog.LoginDialog;
import com.example.myschool.R;

public class MainActivity extends AppCompatActivity {

    private Button mainLoginButton, exitButton;
    private Button loginButton;

    private ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLoginButton = (Button)findViewById(R.id.button);
        exitButton = (Button)findViewById(R.id.button_exit);
        image = (ImageView) findViewById(R.id.image_school);

        image.setImageResource(R.mipmap.school);

        //登录
        mainLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        //退出
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Are you sure to exit?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAndRemoveTask();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this).setTitle("Are you sure to exit?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO
                    }
                }).show();
    }

    public void showLoginDialog(){
        Dialog dialog = new LoginDialog(MainActivity.this);
        dialog.setTitle("Login");
        dialog.show();
    }
}
