package com.example.clo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UploadFileActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private EditText etSubjectName, etFileName;
    private Button btnUpload;
    private Uri fileUri;
    private String selectedCategory;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        etSubjectName = findViewById(R.id.etSubjectName);
        etFileName = findViewById(R.id.etFileName);
        btnUpload = findViewById(R.id.btnUpload);

        // Initialize Firebase references
        databaseRef = FirebaseDatabase.getInstance().getReference("category");
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        // Ask for category selection (Books, StudyMaterials, Links, QuestionPapers)
        showCategorySelectionDialog();

        // Select file button
        findViewById(R.id.btnSelectFile).setOnClickListener(view -> openFileChooser());

        // Upload button
        btnUpload.setOnClickListener(view -> {
            if (fileUri != null && !TextUtils.isEmpty(etSubjectName.getText()) && !TextUtils.isEmpty(etFileName.getText()) && selectedCategory != null) {
                uploadFile();
            } else {
                Toast.makeText(UploadFileActivity.this, "Please fill all fields and select a file", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCategorySelectionDialog() {
        // Simple dialog to choose category (Books, StudyMaterials, Links, QuestionPapers)
        String[] categories = {"Books", "Study materials", "links", "QuestionPapers"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setItems(categories, (dialog, which) -> {
                    selectedCategory = categories[which];
                    dialog.dismiss();
                })
                .show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {
        final String subjectName = etSubjectName.getText().toString().trim();
        final String fileName = etFileName.getText().toString().trim();

        // Capitalize the subject name before uploading
        final String capitalizedSubjectName = subjectName.toUpperCase();

        if (fileUri == null || TextUtils.isEmpty(subjectName) || TextUtils.isEmpty(fileName)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a reference to Firebase Storage for the file upload
        StorageReference fileReference = storageRef.child(selectedCategory).child(capitalizedSubjectName).child(fileName);

        fileReference.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // File uploaded successfully, now save metadata to Firebase Realtime Database
                        saveFileUrlToDatabase(capitalizedSubjectName, fileName, uri.toString());
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(UploadFileActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveFileUrlToDatabase(String subjectName, String fileName, String fileUrl) {
        // Save the file URL directly under the filename, using the capitalized subject name
        databaseRef.child(subjectName)
                .child(selectedCategory)
                .child(fileName) // Using the fileName as the key
                .setValue(fileUrl) // Storing the fileUrl directly under the filename
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadFileActivity.this, "File uploaded and URL saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UploadFileActivity.this, "Failed to save file URL", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
