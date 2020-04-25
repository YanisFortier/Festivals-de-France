package com.yfortier.koifaire;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.yfortier.koifaire.model.Festival;

import java.util.List;
import java.util.Random;

public class LaunchActivity extends AppCompatActivity {

    private boolean isDatabaseLoaded;
    private boolean isPermissionGiven;

    public LaunchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ImageView imageView = findViewById(R.id.splash);

        // Splash random
        int[] ListOfImages = {R.drawable.splash1, R.drawable.splash2, R.drawable.splash3, R.drawable.splash4, R.drawable.splash5};
        Random random = new Random(System.currentTimeMillis());
        int posOfImage = random.nextInt(ListOfImages.length);
        imageView.setBackgroundResource(ListOfImages[posOfImage]);

        // Permission
        if (ActivityCompat.checkSelfPermission(LaunchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LaunchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            setPermissionGiven(true);
        }

        //Connexion à la BDD
        new FirebaseDatabaseHelper().readDatabase(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Festival> festivals, List<String> keys) {
                MainActivity.festivals = festivals;
                setDatabaseLoaded(true);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setPermissionGiven(true);
            } else {
                Toast.makeText(this, "L'application ne peut pas fonctionner sans la location.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void setPermissionGiven(boolean permissionGiven) {
        isPermissionGiven = permissionGiven;
        startMapsActity();
    }

    public void setDatabaseLoaded(boolean databaseLoaded) {
        isDatabaseLoaded = databaseLoaded;
        startMapsActity();
    }

    public void startMapsActity() {
        if (isPermissionGiven && isDatabaseLoaded) {
            Intent i = new Intent(LaunchActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

    }
}
