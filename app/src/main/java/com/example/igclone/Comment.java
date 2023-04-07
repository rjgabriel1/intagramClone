package com.example.igclone;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Comment")


public class Comment extends ParseObject {

    public  Comment(){}

    public static final String KEY_USER = "user";
    public static final String KEY_COMMENT = "comment";

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public String getComment(){
        return getString(KEY_COMMENT);
    }


    public static List<String> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<String> Listofcomments = new ArrayList<String>();

        try {
            for (int i = 0; i < jsonArray.length(); i++){
                Listofcomments.add(jsonArray.getJSONObject(i).getString("objectId"));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return Listofcomments;
    }

}
