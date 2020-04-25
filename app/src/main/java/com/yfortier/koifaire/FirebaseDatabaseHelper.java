package com.yfortier.koifaire;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yfortier.koifaire.model.Festival;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private DatabaseReference mReferenceFestivals;
    private List<Festival> festivals = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mReferenceFestivals = mDatabase.getReference("festivals");
    }

    public void readDatabase(final DataStatus dataStatus) {
        mReferenceFestivals.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                festivals.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Festival festival = keyNode.child("fields").getValue(Festival.class);

                    //Parfois, les coordonnées sont vides.
                    if (keyNode.child("fields/coordonnees_insee/").getValue() != null) {
                        festival.setLatitude((Double) keyNode.child("fields/coordonnees_insee/0").getValue()); // Latitude
                        festival.setLongitude((Double) keyNode.child("fields/coordonnees_insee/1").getValue()); // Longitude
                    }
                    festivals.add(festival);
                }
                dataStatus.DataIsLoaded(festivals, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface DataStatus {
        void DataIsLoaded(List<Festival> festivals, List<String> keys);
    }
}
