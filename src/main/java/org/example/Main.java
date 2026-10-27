package org.example;

import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        // Ejemplo de la práctica: M = 2, T = {3, 5, 2, 4} -> tiempo 7
        Vector<Integer> T = new Vector<>();
        T.add(3);
        T.add(5);
        T.add(2);
        T.add(4);
        int M = 2;

        Vector<Integer> SAct = new Vector<>();
        for (int i = 0; i < T.size(); i++) {
            SAct.add(0);
        }
        Vector<Integer> SFin = new Vector<>();
        int[] max = {Integer.MAX_VALUE};   // por referencia: persiste entre ramas

        AsignarTareas.minimizarTiempoSinGreedy(T, SAct, SFin, max, M, 0);

        System.out.println("Tiempo: " + max[0]);
        System.out.println("Asignación: " + SFin);
    }
}
