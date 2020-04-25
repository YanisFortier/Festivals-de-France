package com.yfortier.koifaire;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.yfortier.koifaire.model.Festival;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.location.Location.distanceBetween;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private List<Festival> festivals = MainActivity.festivals;
    private static LatLngBounds FranceMetroBounds = new LatLngBounds(
            new LatLng(42.6965954131, -4.32784220122),
            new LatLng(50.4644483399, 7.38468690323)
    );

    //Location
    private Location mCurrentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MapView mapView;

    //Interface
    private GoogleMap mMap;
    private ImageButton btnNom;
    private ImageButton btnRecherche;
    private EditText editTxtRecherche;
    private Spinner spinnerDepartements;
    private Spinner spinnerDomaine;

    //Markers
    private LatLng mLatLng;
    private ArrayList<MarkerOptions> mMarkers = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Setup - FindViewById
        btnNom = view.findViewById(R.id.btnNom);
        btnRecherche = view.findViewById(R.id.btnAvancee);
        editTxtRecherche = view.findViewById(R.id.txtRecherche);
        spinnerDepartements = view.findViewById(R.id.spinnerDepartements);
        spinnerDomaine = view.findViewById(R.id.spinnerDomaines);

        //Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        fetchLastLocation();

        // Call du mapView
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Spinner
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings();
        mMap.setMaxZoomPreference(17);

        //Fonction pour ajouter un festival en favoris
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!marker.getTitle().endsWith("⭐")) {// Pas en favori ! On l'ajoute
                    Toast.makeText(getActivity(), marker.getTitle() + " ajouté en favoris.", Toast.LENGTH_SHORT).show();
                    marker.hideInfoWindow();
                    marker.setTitle(marker.getTitle().concat(" ⭐")); // Ajout d'un petit emoji
                    marker.setSnippet(marker.getSnippet().substring(0, marker.getSnippet().length() - 32));
                    marker.showInfoWindow();
                }
            }
        });

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                focusFrance(); //On recentre sur la France métropolitaine
                }
        });

        //Fonction utilisée pour formater le snippet du marker et avoir plusieurs lignes
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override //Obligatoire, non utilisé
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override // Formatage
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getActivity());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getActivity()); // Formatage du titre
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getActivity()); // Formatage du snippet
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        //Recherche par nom
        btnNom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean festivalFound = false;
                mMap.clear(); //Reset de la Map
                for (Festival festival : festivals) {
                    if (festival.getNom_de_la_manifestation().equalsIgnoreCase(editTxtRecherche.getText().toString())) {
                        festivalFound = true;
                        mLatLng = new LatLng(festival.getLatitude(), festival.getLongitude());
                        placementMarkers(festival);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 7));
                        fetchLastLocation();
                    }
                }
                if (!festivalFound) {
                    Toast.makeText(getActivity(), "Festival introuvable", Toast.LENGTH_LONG).show();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(FranceMetroBounds, 150), 2000, null); // On recentre sur la France métropolitaine
                }
            }
        });

        //Recherche avancée
        btnRecherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String departementSelected = spinnerDepartements.getSelectedItem().toString();
                String domaineSelected = spinnerDomaine.getSelectedItem().toString();
                String departementVierge = "Recherche par département          ";
                String domaineVierge = "Recherche par domaine              ";
                mMap.clear(); //Reset de la Map
                if (!departementSelected.equals(departementVierge) && !domaineSelected.equals(domaineVierge)) {
                    for (Festival festival : festivals) {
                        switch (departementSelected.substring(0, 3)) {
                            case "971":
                                rechercheDepartementDomaine(festival, "971", domaineSelected);
                            case "972":
                                rechercheDepartementDomaine(festival, "972", domaineSelected);
                            case "973":
                                rechercheDepartementDomaine(festival, "973", domaineSelected);
                            case "974":
                                rechercheDepartementDomaine(festival, "974", domaineSelected);
                            case "976":
                                rechercheDepartementDomaine(festival, "976", domaineSelected);
                            default:
                                rechercheDepartementDomaine(festival, departementSelected.substring(0, 2), domaineSelected);
                        }
                    }
                    fetchLastLocation();
                    setFocus();
                } else if (!departementSelected.equals(departementVierge)) {
                    for (Festival festival : festivals) {
                        switch (spinnerDepartements.getSelectedItem().toString().substring(0, 3)) {
                            case "971":
                                rechercheDepartement(festival, "971");
                            case "972":
                                rechercheDepartement(festival, "972");
                            case "973":
                                rechercheDepartement(festival, "973");
                            case "974":
                                rechercheDepartement(festival, "974");
                            case "976":
                                rechercheDepartement(festival, "976");
                            default:
                                rechercheDepartement(festival, spinnerDepartements.getSelectedItem().toString().substring(0, 2));
                        }
                    }
                    fetchLastLocation();
                    setFocus();
                } else if (!domaineSelected.equals(domaineVierge)) {
                    for (Festival festival : festivals) {
                        if (festival.getDomaine().equals(domaineSelected)) {
                            mLatLng = new LatLng(festival.getLatitude(), festival.getLongitude());
                            placementMarkers(festival);
                        }
                    }
                    fetchLastLocation();
                    setFocus();
                } else { //On retourne une erreur
                    Toast.makeText(getActivity(), "Veuillez sélectionner un département ou un domaine d'activité", Toast.LENGTH_LONG).show();
                    focusFrance(); //On recentre sur la France métropolitaine
                }
            }
        });
    }

    private void rechercheDepartement(Festival festival, String departement) {
        if (festival.getDepartement().equals(departement)) {
            mLatLng = new LatLng(festival.getLatitude(), festival.getLongitude());
            placementMarkers(festival);
        }
    }

    private void rechercheDepartementDomaine(Festival festival, String departement, String domaine) {
        if (festival.getDepartement().equals(departement) && festival.getDomaine().equals(domaine)) {
            mLatLng = new LatLng(festival.getLatitude(), festival.getLongitude());
            placementMarkers(festival);
        }
    }

    private void setFocus() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (!mMarkers.isEmpty()) {
            for (MarkerOptions marker : mMarkers) {
                builder.include(marker.getPosition());
            }

            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
            mMarkers.clear(); //Reset des markers
        } else {
            Toast.makeText(getActivity(), "Aucune festival correspondant à la recherche trouvé", Toast.LENGTH_SHORT).show();
            focusFrance(); //On recentre sur la France métropolitaine
        }
    }

    private void placementMarkers(Festival festival) {
        float[] distance = {0}; //Initalisation de tableau pour la distance
        String dates = getDatesFestivals(festival); //Récupération des dates formatées
        distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), festival.getLatitude(), festival.getLongitude(), distance); //Calcul de la distance entre les markers
        mMarkers.add(new MarkerOptions().position(mLatLng)); //On ajoute dans un tableau dédié au focus

        //On ajoute sur la map
        mMap.addMarker(new MarkerOptions()
                .position(mLatLng)
                .title(festival.getNom_de_la_manifestation())
                .snippet(dates + "\n"
                        + "Domaine : \t" + festival.getDomaine() + "\n"
                        + "Site Web : \t" + festival.getSite_web() + "\n"
                        + "Distance : \t" + (int) distance[0] / 1000 + " km \n" //Petite conversion en km
                        + "Cliquez pour ajouter en favoris"));

    }



    //Récupération & Formatage des dates de festivals
    public static String getDatesFestivals(Festival festival) {
        String dates = "Dates inconnues";
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("fr", "FR"));
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMMM", new Locale("fr", "FR"));

        if (festival.getDate_de_debut() != null && festival.getDate_de_fin() != null) {
            try {
                Date dateDebutBrut = oldDateFormat.parse(festival.getDate_de_debut());
                Date dateFinBrut = oldDateFormat.parse(festival.getDate_de_fin());

                assert dateDebutBrut != null;
                assert dateFinBrut != null;

                String dateDebut = newDateFormat.format(dateDebutBrut);
                String dateFin = newDateFormat.format(dateFinBrut);
                dates = String.format("Du %1$s au %2$s", dateDebut, dateFin);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    private void fetchLastLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mCurrentLocation = location;
                    mLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                }
            }
        });
    }

    private void focusFrance(){
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(FranceMetroBounds, 150), 2000, null); // On recentre sur la France métropolitaine
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}

