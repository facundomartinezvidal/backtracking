package org.example;

import java.util.Vector;

public class Ordenamiento {

    // Ordena todo el vector en forma decreciente
    public static void mergeSort(Vector<Integer> S) {
        mergeSort(S, 0, S.size() - 1);
    }

    // ALGORITMO MergeSort
    // Entrada: S: Vector<entero>, inicio: entero, fin: entero
    private static void mergeSort(Vector<Integer> S, int inicio, int fin) {
        if (inicio < fin) {
            int medio = (fin + inicio) / 2;
            mergeSort(S, inicio, medio);
            mergeSort(S, medio + 1, fin);
            merge(S, inicio, fin);
        }
    }

    // ALGORITMO Merge
    // Entrada: S: Vector<entero>, inicio: entero, fin: entero
    private static void merge(Vector<Integer> S, int inicio, int fin) {
        Vector<Integer> R = new Vector<>(fin - inicio + 1);
        int medio = (inicio + fin) / 2;
        int i = inicio;
        int j = medio + 1;
        for (int k = 0; k <= fin - inicio; k++) {
            if (i <= medio && (j > fin || S.get(i) >= S.get(j))) {   // >= : orden decreciente
                R.add(S.get(i));
                i = i + 1;
            } else {
                R.add(S.get(j));
                j = j + 1;
            }
        }
        for (int k = 0; k <= fin - inicio; k++) {
            S.set(inicio + k, R.get(k));
        }
    }
}
