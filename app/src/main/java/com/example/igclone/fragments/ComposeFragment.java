package com.example.igclone.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.igclone.MainActivity;
import com.example.igclone.Post;
import com.example.igclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;


public class ComposeFragment extends Fragment {
    public static final String  TAG ="ComposeFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE =42;
    private EditText etCaption;
    private Button btnCapture;
    private ImageView ivPost;
    private Button btnPost;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    ProgressBar progressBar;



    public ComposeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCaption = view.findViewById(R.id.etCaption);
        btnCapture = view.findViewById(R.id.btnCapture);
        ivPost = view.findViewById(R.id.ivPost);
        btnPost = view.findViewById(R.id.btnPost);
        progressBar = view.findViewById(R.id.progressBar);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        queryPost();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = etCaption.getText().toString();
                if (caption.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || ivPost.getDrawable() == null){
                    Toast.makeText(getContext(), "There's no Image", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                SavePost(caption, currentUser, photoFile);
            }
        });
    }
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                btnPost.setVisibility(View.VISIBLE);
                btnCapture.setVisibility(View.INVISIBLE);
                etCaption.setVisibility(View.VISIBLE);
                ivPost.setVisibility(View.VISIBLE);
                progressBar.setVisibility(ProgressBar.VISIBLE);

                // Load the taken image into a preview
                ivPost.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Error taking picture", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return  new File(mediaStorageDir.getPath() + File.separator + fileName);

    }


    private void SavePost(String caption, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(caption);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post Saved succesfully!");
                etCaption.setText("");
                ivPost.setImageResource(0);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                btnPost.setVisibility(View.INVISIBLE);
                btnCapture.setVisibility(View.VISIBLE);
                etCaption.setVisibility(View.INVISIBLE);
                ivPost.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void queryPost() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG,"issue getting post",e);
                    return;
                }
                for(Post post : posts){
                    Log.i(TAG, "Post" + post.getDescription() +" username:" + post.getUser().getUsername());
                }

            }
        });
    }
}