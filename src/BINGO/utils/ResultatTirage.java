package BINGO.utils;

import java.io.Serializable;
import java.util.Arrays;

public class ResultatTirage implements Serializable {
    private static final long serialVersionUID = 1L;

    private int score;
    private int[] boulesTirees;

    public ResultatTirage(int score, int[] boulesTirees) {
        this.score = score;
        this.boulesTirees = boulesTirees;
    }

    public int getScore() {
        return score;
    }

    public int[] getBoulesTirees() {
        return boulesTirees;
    }

    @Override
    public String toString() {
        return "Score: " + score + "/10, Boules tirées: " + Arrays.toString(boulesTirees);
    }
}
