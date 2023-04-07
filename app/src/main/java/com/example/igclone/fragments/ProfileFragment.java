package com.example.igclone.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.igclone.LoginActivity;
import com.example.igclone.Post;
import com.example.igclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ProfileFragment extends Fragment {
    public static final String  TAG ="ProfileFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE =42;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    Button btnLogout;
    GridView gridView;
    GridAdapter adapter;
    ArrayList<Post> list;
    ImageView ivProfile;
    TextView username;
    ParseUser currentUser;

    public static ProfileFragment newInstance(String title) {
        ProfileFragment frag = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogout= view.findViewById(R.id.btnLogout);
        gridView = view.findViewById(R.id.gridView);
        ivProfile = view.findViewById(R.id.ivProfile);
        username = view.findViewById(R.id.tvUname);
        Bundle bundle = getArguments();
        if (bundle==null){
            currentUser = ParseUser.getCurrentUser(); // this will now be null
        }else{
            Post post = Parcels.unwrap(bundle.getParcelable("Post"));
            currentUser = post.getUser(); // get user clicked on
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);

            }
        });

       list = new ArrayList<>();
       adapter = new GridAdapter(getContext(),list);
       gridView.setAdapter(adapter);

       username.setText(currentUser.getUsername());

       Glide.with(getContext()).load(currentUser.getParseFile("profile").getUrl()).transform(new CircleCrop()).into(ivProfile);

       queryPost();

//       MAKE PROFILE PICTURE CLICKABLE
       ivProfile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {launchCamera();}
       });
    }




    protected void queryPost() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){

                    return;
                }

                list.addAll(posts);
                adapter.notifyDataSetChanged();

            }
        });

    }
// LAUCNH CAMERA WHEN PROFILE PICTURE IS CLICKED
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

                // Load the taken image into a preview
                ivProfile.setImageBitmap(takenImage);
                SaveProfile(photoFile);
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

//SAVE PROFILE PICTURE
    private void SaveProfile( File photoFile) {
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put("profile",new ParseFile(photoFile));

        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while changing profile", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "profile changed succesfully!");

            }
        });
    }
}
