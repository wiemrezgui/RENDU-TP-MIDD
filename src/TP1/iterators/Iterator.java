package TP1.iterators;

public interface Iterator<E> {
     boolean hasNext(); // Vérifie s'il reste des éléments
     E next(); // Retourne l'élément courant et avance
}
