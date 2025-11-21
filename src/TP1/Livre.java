package TP1;

import java.util.ArrayList;
import java.util.List;

public class Livre {
    private String titre;
    private List<String> themes;

    public Livre(String titre, List<String> themes) {
        this.titre = titre;
        this.themes = new ArrayList<>(themes);
    }

    public String getTitre() {
        return titre;
    }

    public List<String> getThemes() {
        return themes;
    }

    public boolean contientTheme(String theme) {
        for (String t : themes) {
            if (t.equalsIgnoreCase(theme)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Livre : " + titre + " | Thèmes : " + themes;
    }
}

