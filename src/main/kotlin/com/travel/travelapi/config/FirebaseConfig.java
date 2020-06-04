package com.travel.travelapi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="firebase")
public class FirebaseConfig {

    @Bean
    public DatabaseReference firebaseDatabase() {
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
        return firebase;
    }

    @PostConstruct
    public void init() {

        FileInputStream serviceAccount =
                null;
        try {
            serviceAccount = new FileInputStream(ResourceUtils.getFile("classpath:serviceAccount.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://travel-fd76c.firebaseio.com")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseApp.initializeApp(options);

    }
}
