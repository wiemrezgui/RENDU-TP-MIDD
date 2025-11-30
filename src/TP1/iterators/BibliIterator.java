package TP1.iterators;

import TP1.models.Bibliotheque;
import TP1.models.Livre;

import java.util.ArrayList;
import java.util.List;

public class BibliIterator implements Iterator<Livre> {

    private List<Livre> livresFiltres;
    private int index = 0;

    public BibliIterator(Bibliotheque biblio, String theme) {
        livresFiltres = new ArrayList<>();

        for (Livre l : biblio.getLivres()) {
            if (l.contientTheme(theme)) {
                livresFiltres.add(l);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return index < livresFiltres.size();
    }

    @Override
    public Livre next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException("Plus de livres disponibles");
        }
        return livresFiltres.get(index++);    }
}

