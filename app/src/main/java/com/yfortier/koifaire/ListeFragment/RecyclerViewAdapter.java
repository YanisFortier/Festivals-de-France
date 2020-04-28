package com.yfortier.koifaire.ListeFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yfortier.koifaire.R;
import com.yfortier.koifaire.model.Festival;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.parseColor;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private ArrayList<Festival> favoris;
    private ArrayList<RecyclerViewItem> mRecyclerList;
    private Context mContext;
    private OnItemClickListener mListener;
    private RecyclerViewItem mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;

    public RecyclerViewAdapter(ArrayList<RecyclerViewItem> recyclerList, Context context) {
        mRecyclerList = recyclerList;
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(v, mListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        RecyclerViewItem currentItem = mRecyclerList.get(position);
        holder.TextViewTitre.setText(currentItem.getmTitre().substring(0, 1).toUpperCase() + currentItem.getmTitre().substring(1).toLowerCase());
        holder.TextViewDomaine.setText(currentItem.getmDomaine());
        holder.TextViewDates.setText(currentItem.getmDates());
        holder.TextViewVille.setText(currentItem.getmVille().substring(0, 1).toUpperCase() + currentItem.getmVille().substring(1).toLowerCase());
        holder.TextViewDistance.setText(currentItem.getmDistance() + " km - ");

        holder.ImageView.setBackgroundColor(parseColor(getDomaineColor(currentItem.getmDomaine())));
    }

    @Override
    public int getItemCount() {
        return mRecyclerList.size();
    }

    //Utilisé pour afficher une couleur différente en fonction du domaine du festival
    private String getDomaineColor(String domaine) {
        switch (domaine) {
            case "Arts plastiques et visuels":
                return "#000000";
            case "Cinéma et audiovisuel":
                return "#f8d898";
            case "Cirque et Arts de la rue":
                return "#f8a830";
            case "Danse":
                return "#c04800";
            case "Divers spectacle vivant":
                return "#f80000";
            case "Domaines divers":
                return "#c868e8";
            case "Livre et littérature":
                return "#10c0c8";
            case "Musiques actuelles":
                return "#2868c0";
            case "Pluridisciplinaire Musique":
                return "#089050";
            case "Pluridisciplinaire Spectacle vivant":
                return "#70d038";
            case "Théâtre":
                return "#f8f858";
            case "Transdisciplinaire":
                return "#787878";
            case "Musiques classiques":
                return "#f8f8f8";
            default:
                return "#c0c0c0";
        }
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = mRecyclerList.get(position);
        mRecentlyDeletedItemPosition = position;
        mRecyclerList.remove(position);
        notifyItemRemoved(position);

        //Delete from SharedPrefs
        //Recuperation de la liste des favoris
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("SHARED", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonOutput = sharedPreferences.getString("favoris", null);
        Type type = new TypeToken<ArrayList<Festival>>() {
        }.getType();
        favoris = gson.fromJson(jsonOutput, type);

        //On l'efface du sharedpref
        editor.remove("favoris");
        editor.apply();

        //On remet la liste avec le festival retiré
        favoris.remove(position);
        String jsonInput = gson.toJson(favoris);
        editor.putString("favoris", jsonInput);
        editor.apply();

        editor.commit();

        Log.e("Festivals", favoris.toString());
        if (favoris == null)
            favoris = new ArrayList<>();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView ImageView;
        public TextView TextViewDistance;
        public TextView TextViewTitre;
        public TextView TextViewDomaine;
        public TextView TextViewDates;
        public TextView TextViewVille;

        public RecyclerViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            ImageView = itemView.findViewById(R.id.imageView);
            TextViewTitre = itemView.findViewById(R.id.textViewTitre);
            TextViewDomaine = itemView.findViewById(R.id.textViewDomaine);
            TextViewDistance = itemView.findViewById(R.id.textViewDistance);
            TextViewDates = itemView.findViewById(R.id.textViewDates);
            TextViewVille = itemView.findViewById(R.id.textViewVille);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
