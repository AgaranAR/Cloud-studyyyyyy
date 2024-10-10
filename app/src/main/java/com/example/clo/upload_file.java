package com.example.clo;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class upload_file extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private EditText fileName, authorName;
    private Button chooseFileButton, uploadButton;
    private TextView filePathTextView;
    private Uri fileUri;

   private Button chooseFIleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_page);

        // Initialize UI components
        fileName = findViewById(R.id.file_name);
        authorName = findViewById(R.id.author_name);
        chooseFileButton = findViewById(R.id.choose_file_button);
        filePathTextView = findViewById(R.id.file_path_display);
        uploadButton = findViewById(R.id.upload_button);

        // Set onClick listener for choosing a file
        chooseFileButton.setOnClickListener(view -> openFileChooser());

        // Set onClick listener for uploading the file
        uploadButton.setOnClickListener(view -> uploadFile(fileUri));
    }

    private void uploadFile(Uri fileUri) {
        if (fileUri != null) {
            // Show ProgressBar
            ProgressBar progressBar = findViewById(R.id.upload_progress_bar);
            progressBar.setVisibility(View.VISIBLE);

            // Get a reference to Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");

            // Create a reference with a unique file name
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(fileUri));

            // Start the file upload
            fileReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Hide ProgressBar
                        progressBar.setVisibility(View.GONE);

                        // Display success message
                        Toast.makeText(upload_file.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();

                        // Get the download URL
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            // Save downloadUrl to your database if needed
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Hide ProgressBar
                        progressBar.setVisibility(View.GONE);

                        // Show error message
                        Toast.makeText(upload_file.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        // Update ProgressBar percentage
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        // Update the progress of the upload in real-time
                        progressBar.setProgress((int) progress);

                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }


    private void openFileChooser() {
        Intent pdfintent = new Intent(Intent.ACTION_GET_CONTENT);
        pdfintent.setType("*/*");
        startActivityForResult(Intent.createChooser(pdfintent, "Select PDF"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the file's URI
            fileUri = data.getData();

            // Get the file path (can be displayed to the user)
            String filePath = fileUri.getPath();
            filePathTextView.setText(filePath);

            // Now proceed to upload the file (to Firebase, server, etc.)

        }
    }


}

