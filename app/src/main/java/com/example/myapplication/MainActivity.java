package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;


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

        mStorageRef= FirebaseStorage.getInstance().getReference("uploadsImage");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploadsImage");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFilechooser();
            }

        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(MainActivity.this,"Upload in Progress",Toast.LENGTH_LONG).show();
                }else {
                    UploadFile();
                }

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImageActivity();
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


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void UploadFile() {

        if(mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    +"." + getFileExtension(mImageUri));

            //multiple time jeno ek image set na hoi tai fileReference ta mUploadTask r moddhe diye dibo
            mUploadTask=
                    fileReference.putFile(mImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    },500);

                                    Toast.makeText(MainActivity.this,"Upload Successfull",Toast.LENGTH_LONG).show();

                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    Upload upload = new Upload(editText.getText().toString().trim()
                                            ,task.toString());

                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressBar.setProgress((int) progress);
                                }
                            });
        }else{
            Toast.makeText(this,"waiting",Toast.LENGTH_LONG).show();
        }
    }

    private void OpenImageActivity(){
        Intent intent = new Intent(MainActivity.this,ShowImages.class);
        startActivity(intent);
    }
}
