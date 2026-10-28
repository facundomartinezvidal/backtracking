package org.example;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

// Gráfico de la mejora (tiempo sin Greedy / tiempo con Greedy) en función de N, con una línea por cada M.
// Muestra directamente cuántas veces más rápida es la versión con cota Greedy. Escala lineal.
public class GraficoMejora extends JPanel {

    private static final Color[] COLORES = {new Color(0x1B9E77), new Color(0x7570B3), new Color(0xE7298A)};

    private final String titulo;
    private final int nMin;
    private final int nMax;
    private final int[] procesadores;
    private final List<List<Integer>> ns = new ArrayList<>();
    private final List<List<Double>> mejoras = new ArrayList<>();

    public GraficoMejora(String titulo, int nMin, int nMax, int[] procesadores) {
        this.titulo = titulo;
        this.nMin = nMin;
        this.nMax = nMax;
        this.procesadores = procesadores;
        for (int i = 0; i < procesadores.length; i++) {
            ns.add(new ArrayList<>());
            mejoras.add(new ArrayList<>());
        }
        setBackground(Color.WHITE);
    }

    // indiceM es la posición de M en el vector de procesadores
    public void agregar(int indiceM, int n, double mejora) {
        if (n < nMin) {
            return;                                 // con N chico los tiempos son ruido de la JVM
        }
        ns.get(indiceM).add(n);
        mejoras.get(indiceM).add(mejora);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int izq = 70, der = 20, arriba = 40, abajo = 45;
        int ancho = getWidth() - izq - der;
        int alto = getHeight() - arriba - abajo;
        FontMetrics fm = g2.getFontMetrics();

        g2.setColor(Color.BLACK);
        g2.drawString(titulo, izq + (ancho - fm.stringWidth(titulo)) / 2, 22);

        // Eje Y: de 0 al máximo medido, redondeado a un paso "lindo" para que haya entre 4 y 10 líneas
        double max = 2;
        for (List<Double> lista : mejoras) {
            for (double mejora : lista) {
                max = Math.max(max, mejora);
            }
        }
        double paso = 1;
        for (double candidato : new double[]{1, 2, 5, 10, 20, 50, 100, 200, 500}) {
            paso = candidato;
            if (max / candidato <= 8) {
                break;
            }
        }
        double tope = Math.ceil(max / paso) * paso;

        for (double v = 0; v <= tope; v += paso) {
            int y = yDe(v, tope, arriba, alto);
            g2.setColor(new Color(0xE0E0E0));
            g2.drawLine(izq, y, izq + ancho, y);
            g2.setColor(Color.DARK_GRAY);
            String etiqueta = String.format("x%.0f", v);
            g2.drawString(etiqueta, izq - 8 - fm.stringWidth(etiqueta), y + 4);
        }

        // Referencia en x1: por debajo, la versión con Greedy sería más lenta
        int yUno = yDe(1, tope, arriba, alto);
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{5f, 5f}, 0f));
        g2.drawLine(izq, yUno, izq + ancho, yUno);
        g2.setStroke(new BasicStroke(1f));

        // Eje X
        for (int n = nMin; n <= nMax; n++) {
            if (n % 2 == 0) {
                g2.setColor(Color.DARK_GRAY);
                String etiqueta = String.valueOf(n);
                g2.drawString(etiqueta, xDe(n, izq, ancho) - fm.stringWidth(etiqueta) / 2, arriba + alto + 18);
            }
        }
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(izq, arriba, izq, arriba + alto);
        g2.drawLine(izq, arriba + alto, izq + ancho, arriba + alto);
        String ejeX = "N (cantidad de tareas)";
        g2.drawString(ejeX, izq + (ancho - fm.stringWidth(ejeX)) / 2, arriba + alto + 38);
        g2.drawString("Mejora", 10, arriba - 8);

        // Una curva por cada M
        for (int i = 0; i < procesadores.length; i++) {
            g2.setColor(COLORES[i % COLORES.length]);
            g2.setStroke(new BasicStroke(2f));
            int xAnt = 0;
            int yAnt = 0;
            for (int j = 0; j < ns.get(i).size(); j++) {
                int x = xDe(ns.get(i).get(j), izq, ancho);
                int y = yDe(mejoras.get(i).get(j), tope, arriba, alto);
                if (j > 0) {
                    g2.drawLine(xAnt, yAnt, x, y);
                }
                g2.fillOval(x - 3, y - 3, 6, 6);
                xAnt = x;
                yAnt = y;
            }
            g2.setStroke(new BasicStroke(1f));
        }

        // Referencias
        int xLeyenda = izq + 12;
        for (int i = 0; i < procesadores.length; i++) {
            int y = arriba + 8 + i * 20;
            g2.setColor(COLORES[i % COLORES.length]);
            g2.fillRect(xLeyenda, y, 12, 12);
            g2.setColor(Color.BLACK);
            g2.drawString("M = " + procesadores[i], xLeyenda + 18, y + 11);
        }
    }

    private int xDe(int n, int izq, int ancho) {
        return izq + (int) ((double) (n - nMin) / (nMax - nMin) * ancho);
    }

    private int yDe(double valor, double tope, int arriba, int alto) {
        return arriba + alto - (int) (valor / tope * alto);
    }
}
