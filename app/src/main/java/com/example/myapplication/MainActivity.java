package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;


    private Button button1,button2;
    private EditText editText;
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1=(Button) findViewById(R.id.buttonChooseImage);
        button2=(Button) findViewById(R.id.Button_Upload);
        editText=(EditText) findViewById(R.id.edit_text_fileName);
        imageView=(ImageView) findViewById(R.id.image_view);
        progressBar=(ProgressBar) findViewById(R.id.progress_ber);
        textView=(TextView) findViewById(R.id.text_View_Show_uploads);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFilechooser();
            }

        });

    }

    private void OpenFilechooser() {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent,PICK_IMAGE_REQUEST);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



            if (resultCode == RESULT_OK ) {
               if (requestCode == PICK_IMAGE_REQUEST) {

                    Uri selectedImageURI = data.getData();

                   Picasso.with(MainActivity.this).load(selectedImageURI).noPlaceholder()
                          .into((ImageView) findViewById(R.id.image_view));
                }
        }
    }
}
