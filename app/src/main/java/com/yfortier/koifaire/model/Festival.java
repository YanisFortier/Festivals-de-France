package com.yfortier.koifaire.model;

public class Festival {
    private String nom_de_la_manifestation;
    private String date_de_debut;
    private String date_de_fin;
    private String region;
    private String nom_departement;
    private String departement;
    private String site_web;
    private String domaine;
    private String complement_domaine;
    private String commune_principale;
    private Boolean isFavori;
    private double latitude; // Valeur 0 dans le json - coordonnees_insee
    private double longitude; // Valeur 1 dans le json - coordonnees_insee

    public Festival() {
    }

    public Festival(String nom_de_la_manifestation, String date_de_debut, String date_de_fin, String region, String nom_departement, String departement, String site_web, String domaine, String complement_domaine, String commune_principale, Boolean isFavori, double latitude, double longitude) {
        this.nom_de_la_manifestation = nom_de_la_manifestation;
        this.date_de_debut = date_de_debut;
        this.date_de_fin = date_de_fin;
        this.region = region;
        this.nom_departement = nom_departement;
        this.departement = departement;
        this.site_web = site_web;
        this.domaine = domaine;
        this.complement_domaine = complement_domaine;
        this.commune_principale = commune_principale;
        this.isFavori = isFavori;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNom_de_la_manifestation() {
        return nom_de_la_manifestation;
    }

    public void setNom_de_la_manifestation(String nom_de_la_manifestation) {
        this.nom_de_la_manifestation = nom_de_la_manifestation;
    }

    public String getDate_de_debut() {
        return date_de_debut;
    }

    public void setDate_de_debut(String date_de_debut) {
        this.date_de_debut = date_de_debut;
    }

    public String getDate_de_fin() {
        return date_de_fin;
    }

    public void setDate_de_fin(String date_de_fin) {
        this.date_de_fin = date_de_fin;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNom_departement() {
        return nom_departement;
    }

    public void setNom_departement(String nom_departement) {
        this.nom_departement = nom_departement;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getSite_web() {
        return site_web;
    }

    public void setSite_web(String site_web) {
        this.site_web = site_web;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public String getComplement_domaine() {
        return complement_domaine;
    }

    public void setComplement_domaine(String complement_domaine) {
        this.complement_domaine = complement_domaine;
    }

    public String getCommune_principale() {
        return commune_principale;
    }

    public void setCommune_principale(String commune_principale) {
        this.commune_principale = commune_principale;
    }

    public Boolean getFavori() {
        return isFavori;
    }

    public void setFavori(Boolean favori) {
        isFavori = favori;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}