package com.example.project01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private static final int PICK_MEDIA = 1;
    private FirebaseAuth mAuth;
    private Map<Integer, Runnable> menuActions;

    private FrameLayout mediaContainer;
    private ArrayList<String> countryList;
    private ArrayAdapter<String> adapter;
    private String deletedItem;
    private int deletedPosition;


    String[] countries = {"India", "USA", "China", "UK", "Germany","pakistan"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize list of items

        countryList = new ArrayList<>(Arrays.asList(countries));

        // Set up the ListView and Adapter
        ListView listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countryList);
        listView.setAdapter(adapter);

        // Handle long press on items
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(position);
            return true;
        });

        mAuth = FirebaseAuth.getInstance();

        // Initialize the map with actions for each menu item
        menuActions = new HashMap<>();
        menuActions.put(R.id.new_home_id, this::handleHome);
        menuActions.put(R.id.new_add_id, this::handleAdd);
        menuActions.put(R.id.new_logout_id, this::handleLogout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnvigatiobar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Initialize FrameLayout for media
        mediaContainer = findViewById(R.id.media_container);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Runnable action = menuActions.get(item.getItemId());
        if (action != null) {
            action.run();
            return true;
        }
        return false;
    }

    private void handleHome() {
        // Handle home action
        Toast.makeText(Home.this, "Home Selected", Toast.LENGTH_SHORT).show();
    }

    private void handleAdd() {
        // Open media picker to select images and videos
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/* video/*"); // Allow selecting both images and videos
        startActivityForResult(Intent.createChooser(intent, "Select Media"), PICK_MEDIA);
    }

    private void handleLogout() {
        // Sign out from Firebase and navigate to the SignUp activity
        mAuth.signOut();
        Toast.makeText(Home.this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Home.this, LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the HomeActivity
    }

    private void showOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an option");

        // Options: Edit, Delete, and Exit
        String[] options = {"Edit item", "Delete item", "Exit item"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Edit
                    showEditDialog(position);
                    break;
                case 1: // Delete
                    deleteItem(position);
                    break;
                case 2: // Exit
                    restoreDeletedItem();
                    break;
            }
        });

        // Display the dialog
        builder.show();
    }

    private void showEditDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Item");

        // Input field for editing the item
        final EditText input = new EditText(this);
        input.setText(countryList.get(position));
        builder.setView(input);

        // Save or cancel the edit
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newValue = input.getText().toString();
            countryList.set(position, newValue);
            adapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteItem(int position) {
        // Save the deleted item and its position
        deletedItem = countryList.get(position);
        deletedPosition = position;

        // Remove the item from the list
        countryList.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void restoreDeletedItem() {
        if (deletedItem != null) {
            // Re-insert the deleted item at its original position
            countryList.add(deletedPosition, deletedItem);
            adapter.notifyDataSetChanged();
            deletedItem = null; // Clear the saved deleted item
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA && resultCode == RESULT_OK && data != null) {
            Uri selectedMediaUri = data.getData();
            if (selectedMediaUri != null) {
                String mimeType = getContentResolver().getType(selectedMediaUri);
                mediaContainer.removeAllViews(); // Clear previous content

                if (mimeType != null) {
                    if (mimeType.startsWith("image/")) {
                        // Display image
                        ImageView imageView = new ImageView(this);
                        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT));
                        imageView.setImageURI(selectedMediaUri);
                        mediaContainer.addView(imageView);
                    } else if (mimeType.startsWith("video/")) {
                        // Display video
                        VideoView videoView = new VideoView(this);
                        videoView.setLayoutParams(new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT));
                        videoView.setVideoURI(selectedMediaUri);
                        videoView.start(); // Start playing video
                        mediaContainer.addView(videoView);
                    }
                }
            }
        }
    }
}