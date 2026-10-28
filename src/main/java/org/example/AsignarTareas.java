package org.example;

import java.util.Vector;

public class AsignarTareas {

    public static int minimizarTiempoSinGreedy(Vector<Integer> T, Vector<Integer> SFin, int M) {
        int[] max = {Integer.MAX_VALUE};
        minimizarTiempo(T, nuevaSAct(T), SFin, max, M, 0);
        return max[0];
    }

    public static int minimizarTiempoConGreedy(Vector<Integer> T, Vector<Integer> SFin, int M) {
        int[] max = {greedy(T, SFin, M)};               // con Greedy (cota incial)
        minimizarTiempo(T, nuevaSAct(T), SFin, max, M, 0);
        return max[0];
    }

    private static void minimizarTiempo(Vector<Integer> T, Vector<Integer> SAct, Vector<Integer> SFin,
                                        int[] max, int M, int E) {
        for (int i = 0; i < M; i++) {
            SAct.set(E, i);
            int tiempo = calcularMax(SAct, M, T, E);
            if (tiempo < max[0]) {
                if (E == T.size() - 1) {                // todas las tareas asignadas
                    max[0] = tiempo;
                    SFin.clear();
                    SFin.addAll(SAct);
                } else {
                    minimizarTiempo(T, SAct, SFin, max, M, E + 1);
                }
            }
        }
    }

    private static Vector<Integer> nuevaSAct(Vector<Integer> T) {
        Vector<Integer> SAct = new Vector<>();
        for (int i = 0; i < T.size(); i++) {
            SAct.add(0);
        }
        return SAct;
    }


    
    private static int greedy(Vector<Integer> T, Vector<Integer> SFin, int M) {

        Ordenamiento.mergeSort(T); // Se ordena decreciente para asignar primero las tareas más largas
        int[] carga = new int[M];
        SFin.clear();
        for (int j = 0; j < T.size(); j++) {
            int menor = 0;
            for (int i = 1; i < M; i++) {
                if (carga[i] < carga[menor]) {
                    menor = i;
                }
            }
            carga[menor] += T.get(j);
            SFin.add(menor);
        }
        int mayor = carga[0];
        for (int i = 1; i < M; i++) {
            if (carga[i] > mayor) {
                mayor = carga[i];
            }
        }
        return mayor;
    }

    private static int calcularMax(Vector<Integer> SAct, int M, Vector<Integer> T, int E) {
        int[] carga = new int[M];
        for (int j = 0; j <= E; j++) {
            carga[SAct.get(j)] += T.get(j);
        }
        int mayor = carga[0];
        for (int i = 1; i < M; i++) {
            if (carga[i] > mayor) {
                mayor = carga[i];
            }
        }
        return mayor;
    }
}
