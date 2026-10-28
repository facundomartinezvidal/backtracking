package org.example;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Random;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        // Medición de tiempos: ambas versiones para distintos M y cantidades de tareas N,
        // con tiempos de tarea aleatorios entre 1 y 100 (semilla fija para poder repetir).
        // N crece de a 1 para ver cómo escala el tiempo; "Crece" es el tiempo dividido el de N-1
        // (si el costo es O(M^N), debería rondar M).
        // Cada fila es el promedio de 3 entradas distintas del mismo tamaño.
        int repeticiones = 3;
        int minTareas = 4;
        int[] procesadores = {2, 3, 4};
        int[] maxTareas = {24, 18, 16};        // el máximo de N baja al crecer M porque el árbol tiene M^N hojas

        // Ventana: un gráfico de tiempos por cada M y el de mejora arriba, la tabla abajo.
        // Se llenan a medida que se mide.
        DefaultTableModel tabla = new DefaultTableModel(
                new String[]{"M", "N", "Sin Greedy (ms)", "Crece", "Con Greedy (ms)", "Crece", "Mejora"}, 0);
        JTable jtabla = new JTable(tabla);
        jtabla.setDefaultEditor(Object.class, null);
        jtabla.setRowHeight(22);
        DefaultTableCellRenderer alineadoDerecha = new DefaultTableCellRenderer();
        alineadoDerecha.setHorizontalAlignment(SwingConstants.RIGHT);
        jtabla.setDefaultRenderer(Object.class, alineadoDerecha);

        int nDesdeMejora = 8;                  // con N más chico los tiempos son de milésimas de ms y son ruido
        int maxN = 0;
        for (int max : maxTareas) {
            maxN = Math.max(maxN, max);
        }
        GraficoMejora graficoMejora = new GraficoMejora(
                "Mejora: tiempo sin Greedy / tiempo con Greedy (desde N = " + nDesdeMejora + ")",
                nDesdeMejora, maxN, procesadores);

        Grafico[] graficos = new Grafico[procesadores.length];
        JPanel panelGraficos = new JPanel(new GridLayout(2, 2));
        for (int p = 0; p < procesadores.length; p++) {
            graficos[p] = new Grafico("M = " + procesadores[p] + " procesadores (tiempo en escala logarítmica)",
                    minTareas, maxTareas[p]);
            panelGraficos.add(graficos[p]);
        }
        panelGraficos.add(graficoMejora);

        JLabel encabezado = new JLabel("<html><b style='font-size:15px'>Asignación de tareas a procesadores: "
                + "Backtracking sin y con cota inicial Greedy</b><br>Tiempo de ejecución en milisegundos. "
                + "Cada fila es el promedio de " + repeticiones + " entradas aleatorias (tiempos de tarea entre 1 y 100). "
                + "\"Crece\" = tiempo / tiempo con N-1; si el costo es O(M^N) debería rondar M.</html>");
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Los gráficos ocupan el 70% del alto y la tabla el resto; al agrandar la ventana crecen los gráficos
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelGraficos, new JScrollPane(jtabla));
        division.setResizeWeight(0.7);

        JFrame ventana = new JFrame("Tiempo de ejecución: sin Greedy vs con Greedy");
        ventana.add(encabezado, BorderLayout.NORTH);
        ventana.add(division, BorderLayout.CENTER);
        ventana.setSize(1500, 900);
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);
        division.setDividerLocation(0.7);

        // Las mediciones corren en otro hilo para que la ventana se vaya actualizando
        new Thread(() -> {
            Random random = new Random(42);
            for (int p = 0; p < procesadores.length; p++) {
                int m = procesadores[p];
                int indiceM = p;
                Grafico grafico = graficos[p];
                double anteriorSin = 0;
                double anteriorCon = 0;

                // Una lista base por repetición: cada N usa sus primeras N tareas, así las filas
                // consecutivas comparten datos y la comparación entre N y N-1 no depende del azar.
                Vector<Vector<Integer>> listasBase = new Vector<>();
                for (int r = 0; r < repeticiones; r++) {
                    Vector<Integer> base = new Vector<>();
                    for (int i = 0; i < maxTareas[p]; i++) {
                        base.add(random.nextInt(100) + 1);
                    }
                    listasBase.add(base);
                }

                for (int n = minTareas; n <= maxTareas[p]; n++) {
                    double msSin = 0;
                    double msCon = 0;
                    for (int r = 0; r < repeticiones; r++) {
                        Vector<Integer> tareas = new Vector<>(listasBase.get(r).subList(0, n));

                        long inicio = System.nanoTime();
                        int tiempoSin = AsignarTareas.minimizarTiempoSinGreedy(new Vector<>(tareas), new Vector<>(), m);
                        msSin += (System.nanoTime() - inicio) / 1_000_000.0;

                        inicio = System.nanoTime();
                        int tiempoCon = AsignarTareas.minimizarTiempoConGreedy(new Vector<>(tareas), new Vector<>(), m);
                        msCon += (System.nanoTime() - inicio) / 1_000_000.0;

                        if (tiempoSin != tiempoCon) {
                            throw new IllegalStateException("Las versiones difieren para M=" + m + ", T=" + tareas);
                        }
                    }
                    msSin /= repeticiones;
                    msCon /= repeticiones;
                    String crecSin = anteriorSin == 0 ? "-" : String.format("x%.1f", msSin / anteriorSin);
                    String crecCon = anteriorCon == 0 ? "-" : String.format("x%.1f", msCon / anteriorCon);
                    Object[] fila = {m, n, String.format("%.3f", msSin), crecSin,
                            String.format("%.3f", msCon), crecCon, String.format("x%.1f", msSin / msCon)};
                    anteriorSin = msSin;
                    anteriorCon = msCon;

                    int nActual = n;
                    double sinActual = msSin;
                    double conActual = msCon;
                    SwingUtilities.invokeLater(() -> {
                        tabla.addRow(fila);
                        jtabla.scrollRectToVisible(jtabla.getCellRect(tabla.getRowCount() - 1, 0, true));
                        grafico.agregar(nActual, sinActual, conActual);
                        graficoMejora.agregar(indiceM, nActual, sinActual / conActual);
                    });
                }
            }
        }).start();
    }
}
