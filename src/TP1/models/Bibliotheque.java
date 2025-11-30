package TP1.models;

import TP1.iterators.BibliIterator;
import TP1.iterators.Iterator;

import java.util.ArrayList;
import java.util.List;

public class Bibliotheque {
    private String libelle;
    private List<Livre> livres;

    public Bibliotheque(String libelle) {
        this.libelle = libelle;
        this.livres = new ArrayList<>();
    }

    public void ajouterLivre(Livre livre) {
        livres.add(livre);
    }

    public List<Livre> getLivres() {
        return new ArrayList<>(livres);
    }
    public String getLibelle() {
        return libelle;
    }

    public Iterator<Livre> iteratorTheme(String theme) {
        return new BibliIterator(this, theme);
    }
}


