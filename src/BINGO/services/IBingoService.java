package BINGO.services;

import BINGO.utils.ResultatTirage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBingoService extends Remote {
    // Vérifie la prédiction du joueur et retourne le résultat
    ResultatTirage verifierPrediction(int[] prediction) throws RemoteException;

    // Récupère le meilleur score enregistré
    int getMeilleurScore() throws RemoteException;

    // Enregistre un nouveau score si c'est un record
    void enregistrerScore(int score) throws RemoteException;
}