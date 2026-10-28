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

        Vector<Integer> SFin = new Vector<>();
        int tiempo = AsignarTareas.minimizarTiempoSinGreedy(T, SFin, M);
        System.out.println("Sin Greedy -> Tiempo: " + tiempo);
        System.out.println("Asignación: " + SFin);

        // La versión con Greedy ordena T, por eso se usa una copia
        Vector<Integer> TOrd = new Vector<>(T);
        Vector<Integer> SFinG = new Vector<>();
        int tiempoG = AsignarTareas.minimizarTiempoConGreedy(TOrd, SFinG, M);
        System.out.println("Con Greedy -> Tiempo: " + tiempoG);
        System.out.println("Tareas ordenadas: " + TOrd);
        System.out.println("Asignación: " + SFinG);
    }
}
