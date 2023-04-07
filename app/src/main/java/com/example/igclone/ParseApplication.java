package com.example.igclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

         // Register your parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("BCb39Zxqex1BeEjhrTxcGxF9y9Z3YmJe5QrVEPF3")
                .clientKey("z6DoBsYB1OapZF96rYcQr4tWibOLCqyGFhuYIz20")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
