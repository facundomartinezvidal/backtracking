package org.example;

import java.util.Vector;

public class AsignarTareas {

    // ALGORITMO MinimizarTiempoSinGreedy
    // Entrada: T: Vector<entero>, SAct: Vector<entero>, SFin: Vector<entero>,
    //          Max: entero, M: entero, E: entero
    // Versión sin cota superior Greedy. Procesadores numerados de 0 a M-1.
    // Llamada inicial desde Main: SAct con T.size() posiciones, max[0] = infinito, E = 0.
    public static void minimizarTiempoSinGreedy(Vector<Integer> T, Vector<Integer> SAct, Vector<Integer> SFin,
                                                int[] max, int M, int E) {
        for (int i = 0; i < M; i++) {
            SAct.set(E, i);
            int tiempo = calcularMax(SAct, M, T, E);
            if (tiempo < max[0]) {                      // poda por cota inferior
                if (E == T.size() - 1) {                // todas las tareas asignadas
                    max[0] = tiempo;
                    SFin.clear();
                    SFin.addAll(SAct);
                } else {
                    minimizarTiempoSinGreedy(T, SAct, SFin, max, M, E + 1);
                }
            }
        }
    }

    // ALGORITMO CalcularMax
    // Entrada: SAct: Vector<entero>, M: entero, T: Vector<entero>, E: entero
    // Salida: carga del procesador más cargado en la asignación parcial SAct[0..E]
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
