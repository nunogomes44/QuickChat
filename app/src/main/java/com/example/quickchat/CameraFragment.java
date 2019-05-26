package com.example.quickchat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class CameraFragment extends Fragment {

    private View viewCamera;
    private static final int CAMERA_REQUEST_CODE = 1;
    private String pathToFile;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private Uri photoUri;
    private String myUrl;
    private StorageTask uploadTask;

    public CameraFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();

        if(Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewCamera = inflater.inflate(R.layout.fragment_camera, container, false);

        mProgress = new ProgressDialog(getContext());
        mStorage = FirebaseStorage.getInstance().getReference();

        dispatchPictureTakerAction();

        return viewCamera;
    }

    private void dispatchPictureTakerAction() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getActivity().getPackageManager())!=null){
            File photoFile = null;
            photoFile = createPhotoFile();

            if(photoFile!=null){
                pathToFile = photoFile.getAbsolutePath();
                photoUri = FileProvider.getUriForFile(getActivity(), "com.example.quickchat.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePic, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE){
            mProgress.setMessage("Uploading Image....");
            mProgress.show();

            final Uri uri = photoUri;

            final StorageReference filepath = mStorage.child("Image Files").child(uri.getLastPathSegment());


            /*
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgress.dismiss();
                    Toast.makeText(getContext(), "Uploading Finished", Toast.LENGTH_SHORT).show();
                    myUrl = uri.toString();

                    Intent intent = new Intent(getActivity(), ImageChatActivity.class);
                    intent.putExtra("myUrl", myUrl);
                    startActivity(intent);
                }
            });*/

            uploadTask = filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Uploading Finished", Toast.LENGTH_SHORT).show();
                }
            });

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        Intent intent = new Intent(getActivity(), ImageChatActivity.class);
                        intent.putExtra("myUrl", myUrl);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private File createPhotoFile() {
        File image = null;
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return image;
    }
}
