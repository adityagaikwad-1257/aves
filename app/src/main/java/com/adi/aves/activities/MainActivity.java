package com.adi.aves.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.adi.aves.R;
import com.adi.aves.databinding.ActivityMainBinding;
import com.adi.aves.models.PRResponse;
import com.adi.aves.models.Results;
import com.adi.aves.retrofit.PlateRecognizerAPI;
import com.adi.aves.retrofit.RetrofitHelper;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "aditya";

    ActivityMainBinding binding;
    Uri imageUri;

    File file;

    ActivityResultLauncher<CropImageContractOptions> cropLauncher;

    ActivityResultLauncher<Uri> cameraLauncher;

    ActivityResultLauncher<String> imageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        createImageFile();

        registerLaunchers();

        clickEvents();
    }

    private void clickEvents() {
        binding.clickImageBtn.setOnClickListener(v -> cameraLauncher.launch(imageUri));
        binding.uploadNExtractBtn.setOnClickListener((view) -> uploadAndExtract());
    }

    private void createImageFile() {
        file = new File(getApplicationContext().getFilesDir(), "camera.png");
        imageUri = FileProvider.getUriForFile(getApplicationContext(), "com.adi.aves.fileProvider", file);
    }

    private void registerLaunchers() {
        cropLauncher = registerForActivityResult(new CropImageContract(), result -> {
            setImageUri(result.getUriContent());
            saveFile(getApplicationContext(), result.getUriContent());
        });

        imageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Log.d(TAG, "registerLaunchers: " + result);

            if (result != null){
                CropImageOptions cropImageOptions = new CropImageOptions();
                cropLauncher.launch(new CropImageContractOptions(result, cropImageOptions));
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), clicked -> {
            if (clicked){
                CropImageOptions cropImageOptions = new CropImageOptions();
                cropLauncher.launch(new CropImageContractOptions(imageUri, cropImageOptions));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        if (item.getItemId() == R.id.entries){
//            Intent intent = new Intent(this, EntriesActivity.class);
//            startActivity(intent);
//        }
//
//        return true;
//    }

    public boolean saveFile(Context context, Uri sourceuri) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        InputStream input = null;
        boolean hasError = false;

        try {

            input = context.getContentResolver().openInputStream(sourceuri);

            String destination = getApplicationContext().getFilesDir() + File.separator + "camera.png";
            int originalsize = input.available();

            Log.d(TAG, "saveFile: original size " + originalsize);
            Log.d(TAG, "saveFile: size before writing it " + (file.length()/1024));

            bis = new BufferedInputStream(input);
            bos = new BufferedOutputStream(new FileOutputStream(destination));
            byte[] buf = new byte[originalsize];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);

        } catch (Exception e) {
            e.printStackTrace();
            hasError = true;
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (Exception ignored) {
            }
        }

        Log.d(TAG, "saveFile: size after writing it " + (file.length()/1024));

        return !hasError;
    }

    private MultipartBody.Part getFile(){
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("upload", "upload", requestFile);
    }

    private void uploadAndExtract() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("while we extract the characters");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RetrofitHelper.getRetrofit(this).create(PlateRecognizerAPI.class)
                .extractCharacter(RetrofitHelper.TOKEN, getFile())
                .enqueue(new Callback<PRResponse>() {
                    @Override
                    public void onResponse(Call<PRResponse> call, Response<PRResponse> response) {
                        progressDialog.dismiss();
                        if (response.body() == null || !response.isSuccessful()){
                            try {
                                Log.d(TAG, "one: " + response.message() + " " + response.code() + " " +
                                        response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "onResponse: couldn't connect");
                            Toast.makeText(MainActivity.this, "Couldn't connect", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        showExtractedDetails(response.body());
                        Log.d(TAG, "onResponse: 2" + response.body() + " " + response.message() + " " + response.code());
                    }

                    @Override
                    public void onFailure(Call<PRResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onResponse: " + t.getLocalizedMessage());
                        Toast.makeText(MainActivity.this, "Couldn't connect", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveToFireBase(PRResponse body, String downloadUrl) {
        String key = FirebaseDatabase.getInstance().getReference()
                .child("Entries")
                .push().getKey();

        key = (key == null)?""+System.currentTimeMillis():key;

        body.setEntry_id(key);
        body.setType(PRResponse.TYPE_IN);
        body.setImageUrl(downloadUrl);

        FirebaseDatabase.getInstance().getReference()
                .child("Entries")
                .child(key)
                .setValue(body);
    }

    private void showExtractedDetails(PRResponse response){
        List<Results> results = response.getResults();

        if (results == null || results.size() == 0 || results.get(0) == null) {
            Toast.makeText(this, "No number plate found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String plate = results.get(0).getPlate();
        double score = results.get(0).getScore() * 100;

        String plateText = plate.substring(0, 2).toUpperCase() + " " + plate.substring(2).toUpperCase();
        String scoreText = String.format("%.2f", score) + "% Accuracy";

        binding.npTextView.setText(plateText);
        binding.accuracyTextView.setText(scoreText);

        if (score > 0) binding.accuracyTextView.setVisibility(View.VISIBLE);

        if (score < 50) binding.accuracyTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        else binding.accuracyTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        response.setNumber_plate(plateText);
        uploadImage(response);
    }

    private void uploadImage(PRResponse response) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        FirebaseStorage.getInstance()
                .getReference()
                .child("captured_images")
                .child(timeStamp)
                .putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // fetching download url
                        FirebaseStorage.getInstance()
                                .getReference()
                                .child("captured_images")
                                .child(timeStamp)
                                .getDownloadUrl().addOnCompleteListener(downloadTask -> {
                                    if (downloadTask.isSuccessful()){
                                        saveToFireBase(response, downloadTask.getResult().toString());
                                    }else{
                                        Toast.makeText(this, "Couldn't upload image.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        Toast.makeText(this, "Couldn't upload image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setImageUri(Uri imageUri){
        if (imageUri == null){
            binding.uploadNExtractBtn.setVisibility(View.GONE);
            return;
        }
        Log.d(TAG, "setImageUri: " + imageUri);
        Picasso.get().load(imageUri)
                        .fit().centerCrop().into(binding.imageView);
        binding.uploadNExtractBtn.setVisibility(View.VISIBLE);
    }
}