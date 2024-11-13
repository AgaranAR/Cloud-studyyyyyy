package com.example.clo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class upload_file extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private String selectedCategory;

    private EditText subjectNameEditText;
    private EditText fileNameEditText;
    private Button selectCategoryButton;
    private Button selectFileButton;
    private Button uploadButton;
    private TextView selectedFileUriTextView;  // TextView to display file URI

    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    BottomNavigationView bottomNavigationView;// Use Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_page);

        // Initialize Firebase
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();  // Initialize Realtime Database

        // Initialize UI elements
        subjectNameEditText = findViewById(R.id.subjectNameEditText);
        fileNameEditText = findViewById(R.id.fileNameEditText);
        selectCategoryButton = findViewById(R.id.selectCategoryButton);
        selectFileButton = findViewById(R.id.selectFileButton);
        uploadButton = findViewById(R.id.uploadButton);
        selectedFileUriTextView = findViewById(R.id.selectedFileUriTextView);  // Initialize TextView

        // Category Selection Button
        selectCategoryButton.setOnClickListener(view -> showCategoryDialog());

        // File Picker Button
        selectFileButton.setOnClickListener(view -> openFileChooser());

        // Upload Button
        uploadButton.setOnClickListener(view -> uploadFile());

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.nav_upload);

        // Set up Bottom Navigation View listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    // Start MainActivity (or another home activity if needed)
                    startActivity(new Intent(upload_file.this, MainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_all_courses) {
                    // Start AllCoursesActivity (your existing page for courses)
                    startActivity(new Intent(upload_file.this, MyCourses.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_upload) {
                    // Start UploadActivity (your existing page for uploading)
                    startActivity(new Intent(upload_file.this, upload_file.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_logout) {
                    // Handle logout, e.g., clear session and redirect to login page
                    // logoutUser(); // Uncomment this when needed
                    return true;
                }
                return false;
            }
        });
    }

    private void showCategoryDialog() {
        String[] categories = {"Books", "Study materials", "links", "qp"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category")
                .setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategory = categories[which];
                        Toast.makeText(upload_file.this, "Category selected: " + selectedCategory, Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            // Display the selected file URI in the TextView
            selectedFileUriTextView.setText("Selected file URI: " + fileUri.toString());
        }
    }

    private void uploadFile() {
        String subjectName = subjectNameEditText.getText().toString().trim();
        String fileName = fileNameEditText.getText().toString().trim();

        if (subjectName.isEmpty() || fileName.isEmpty() || fileUri == null || selectedCategory == null) {
            Toast.makeText(this, "Please fill all the fields and select a file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Define the folder structure dynamically
        String folderPath = "uploads/" + selectedCategory + "/" + subjectName + "/";

        // Create a reference to the file location in Firebase Storage
        StorageReference fileReference = firebaseStorage.getReference().child(folderPath + fileName + System.currentTimeMillis());

        fileReference.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveFileMetadata(uri.toString(), subjectName, fileName, folderPath);
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(upload_file.this, "File Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveFileMetadata(String fileUrl, String subjectName, String fileName, String folderPath) {
        Map<String, Object> fileMetadata = new HashMap<>();
        fileMetadata.put("fileName", fileName);
        fileMetadata.put("fileUrl", fileUrl);
        fileMetadata.put("subjectName", subjectName);
        fileMetadata.put("folderPath", folderPath);  // Store the folder path for reference

        // Store metadata in Firebase Realtime Database under the selected category and subject
        DatabaseReference categoryRef = firebaseDatabase.getReference("category")
                .child(subjectName)
                .child(selectedCategory)
                .child(fileName);
        categoryRef.setValue(fileMetadata)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(upload_file.this, "File Metadata Saved", Toast.LENGTH_SHORT).show();
                    clearInputs();
                })
                .addOnFailureListener(e -> {
                    // Print the error in the log and show a Toast
                    Log.e("UploadFileError", "File Upload Failed: " + e.getMessage());
                    Toast.makeText(upload_file.this, "File Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputs() {
        subjectNameEditText.setText("");
        fileNameEditText.setText("");
        selectCategoryButton.setText("Select Category");
        selectFileButton.setText("Choose File");
        selectedFileUriTextView.setText("");  // Clear the URI TextView
    }
}
