package BINGO;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class BingoServiceImpl extends UnicastRemoteObject implements IBingoService {
    private int meilleurScore;
    private Random random;

    public BingoServiceImpl() throws RemoteException {
        super();
        this.meilleurScore = 0;
        this.random = new Random();
    }

    @Override
    public synchronized ResultatTirage verifierPrediction(int[] prediction) throws RemoteException {
        // Validation de la prédiction
        if (prediction == null || prediction.length != 10) {
            throw new RemoteException("La prédiction doit contenir exactement 10 numéros");
        }

        // Tirage des boules sans remise (0 à 9)
        int[] boulesTirees = tirerBoules();

        // Calcul du score
        int score = calculerScore(prediction, boulesTirees);

        System.out.println("Prédiction: " + Arrays.toString(prediction));
        System.out.println("Boules tirées: " + Arrays.toString(boulesTirees));
        System.out.println("Score: " + score + "/10");

        return new ResultatTirage(score, boulesTirees);
    }

    /**
     * Tire 10 boules aléatoirement sans remise (0-9)
     */
    private int[] tirerBoules() {
        List<Integer> urne = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            urne.add(i);
        }

        int[] boules = new int[10];
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(urne.size());
            boules[i] = urne.remove(index);
        }

        return boules;
    }

    /**
     * Calcule le score en comparant la prédiction et le tirage
     */
    private int calculerScore(int[] prediction, int[] tirage) {
        int score = 0;

        for (int i = 0; i < prediction.length; i++) {
            if (prediction[i] == tirage[i]) {
                score++; // +1 seulement si même nombre à la même position
            }
        }

        return score;
    }
    @Override
    public synchronized int getMeilleurScore() throws RemoteException {
        return meilleurScore;
    }

    @Override
    public synchronized void enregistrerScore(int score) throws RemoteException {
        if (score > meilleurScore) {
            meilleurScore = score;
            System.out.println("Nouveau meilleur score: " + meilleurScore);
        }
    }
}
