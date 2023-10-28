package com.example.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import java.io.FileInputStream;
import java.io.IOException;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/**").permitAll()  // Allow access to the root URL
                .anyRequest().authenticated() // Require authentication for all other requests
                .and()
                .oauth2Login(); // Enable OAuth2 login

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public Firestore firebaseApp() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("./appsettings.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);

        return FirestoreClient.getFirestore(FirebaseApp.getInstance());
    }
}
