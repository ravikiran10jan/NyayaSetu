package com.nyayasetu.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.path}")
    private String firebaseCredentialsPath;

    @PostConstruct
    public void initialize() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions.Builder builder = FirebaseOptions.builder();
            if (firebaseCredentialsPath != null && !firebaseCredentialsPath.isEmpty()
                    && java.nio.file.Files.exists(java.nio.file.Paths.get(firebaseCredentialsPath))) {
                try (FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath)) {
                    builder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
                }
            } else {
                // Cloud Run: no key file baked in - use the runtime service account
                // via Application Default Credentials (metadata server).
                builder.setCredentials(GoogleCredentials.getApplicationDefault());
            }
            String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
            if (projectId == null || projectId.isEmpty()) {
                projectId = "nyayasetu-ravi";
            }
            builder.setProjectId(projectId);
            FirebaseApp.initializeApp(builder.build());
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance(FirebaseApp.getInstance());
    }
}
