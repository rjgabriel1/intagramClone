package com.example.igclone;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Post")
@Parcel(analyze = Comment.class)

public class Post  extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_TOTAL_LIKES = "amountLikes";

    public static final String KEY_COMMENT = "comments";

    // Define getters and setters for each field we need
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);

    }


    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE,parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public JSONArray getComment(){return getJSONArray(KEY_COMMENT);}

    public JSONArray getLikes(){
        return getJSONArray(KEY_LIKES);
    }
    public  int getNumLikes(){
        return getInt(KEY_TOTAL_LIKES);
    }
    public void setNumberLike(int amount){put(KEY_TOTAL_LIKES, amount);}

    public void setListLikers(ParseUser userLike){add(KEY_LIKES, userLike);}
    public void removeItemListLikers(List<String> listUserLike){
        remove(KEY_LIKES);
        put(KEY_LIKES, listUserLike);
    }

    public void setUser(ParseUser user){
        put(KEY_USER,user);

    }

    public void setComment(ParseObject comment){add(KEY_COMMENT, comment);}

    public static ArrayList<String> fromJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<String> listofLike = new ArrayList<String>();
        try {
            for (int i = 0; i < jsonArray.length(); i++){
                listofLike.add(jsonArray.getJSONObject(i).getString("objectId"));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return listofLike;
    }
}
