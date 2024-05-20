package com.example.kamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button b1;
    ImageView iv;
    private static final int CAMERA_REQUEST = 1888;
    String nmFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);

        b1.setOnClickListener(view -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            someActivityResultLauncher.launch(cameraIntent);
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    assert data != null;
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    assert bm != null;
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    makePost(Base64.encodeToString(b, Base64.NO_WRAP));
                    Toast.makeText(this, "Data Telah Terload ke Server", Toast.LENGTH_SHORT).show();
                }
            });

    private void makePost(String b64_img){
        OkHttpClient okHttpClient = new OkHttpClient();


        RequestBody formbody = new FormBody.Builder()
                .add("content", ";base64,"+b64_img)
                .build();
        Request request = new Request.Builder().url("http://35.197.147.23/")
                .post(formbody)
                .build();
        // making call asynchronously
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            // called if server is unreachable
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(MainActivity.this, "server down", Toast.LENGTH_SHORT).show();
//                        pagenameTextView.setText("error connecting to the server");
                    }
                });
            }

            @Override
            // called if we get a
            // response from the server
            public void onResponse(
                    @NotNull Call call,
                    @NotNull Response response)
                    throws IOException {
//                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
//                pagenameTextView.setText(response.body().string());
            }
        });
    }
}