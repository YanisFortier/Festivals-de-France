package com.yfortier.koifaire.ListeFragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.yfortier.koifaire.model.Festival;
import com.yfortier.koifaire.MainActivity;
import com.yfortier.koifaire.MapFragment;
import com.yfortier.koifaire.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.location.Location.distanceBetween;

public class ListeFragment extends Fragment {
    private List<Festival> festivals = MainActivity.festivals;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //Location
    private Location mCurrentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_liste, container, false);


        //Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        fetchLastLocation();

        //RecyclerView builder
        final ArrayList<RecyclerViewItem> recyclerList = new ArrayList<>();
        for (Festival festival : festivals) {
            recyclerList.add(new RecyclerViewItem(festival.getNom_de_la_manifestation(), MapFragment.getDatesFestivals(festival), festival.getDomaine(), getDistanceFestival(festival), festival.getCommune_principale()));
        }
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new RecyclerViewAdapter(recyclerList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getActivity(), recyclerList.get(position).getmTitre(), Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }

    private int getDistanceFestival(Festival festival) {
        float[] distance = {0}; //Initalisation de tableau pour la distance
        //distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), festival.getLatitude(), festival.getLongitude(), distance); //Calcul de la distance entre les markers
        distanceBetween(47.5, -0.5, festival.getLatitude(), festival.getLongitude(), distance); //Calcul de la distance entre les markers
        return (int) (distance[0] / 1000); //Petite conversion en km
    }

    private void fetchLastLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mCurrentLocation = location;
                }
            }
        });
    }
}
