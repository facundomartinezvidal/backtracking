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

// Gráfico de líneas con el eje del tiempo en escala logarítmica: una curva O(M^N) se ve como una recta.
// Los puntos se van agregando a medida que se miden, así que se redibuja con cada uno.
public class Grafico extends JPanel {

    private static final Color COLOR_SIN = new Color(0x2A6FDB);
    private static final Color COLOR_CON = new Color(0xE8740C);

    private final String titulo;
    private final int nMin;
    private final int nMax;
    private final List<Integer> ns = new ArrayList<>();
    private final List<Double> msSin = new ArrayList<>();
    private final List<Double> msCon = new ArrayList<>();

    public Grafico(String titulo, int nMin, int nMax) {
        this.titulo = titulo;
        this.nMin = nMin;
        this.nMax = nMax;
        setBackground(Color.WHITE);
    }

    public void agregar(int n, double sinGreedy, double conGreedy) {
        ns.add(n);
        msSin.add(sinGreedy);
        msCon.add(conGreedy);
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

        // Marcas del eje X cada 2 tareas
        for (int n = nMin; n <= nMax; n++) {
            if (n % 2 == 0) {
                int x = xDe(n, izq, ancho);
                g2.setColor(Color.DARK_GRAY);
                String etiqueta = String.valueOf(n);
                g2.drawString(etiqueta, x - fm.stringWidth(etiqueta) / 2, arriba + alto + 18);
            }
        }
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(izq, arriba, izq, arriba + alto);
        g2.drawLine(izq, arriba + alto, izq + ancho, arriba + alto);
        String ejeX = "N (cantidad de tareas)";
        g2.drawString(ejeX, izq + (ancho - fm.stringWidth(ejeX)) / 2, arriba + alto + 38);
        g2.drawString("ms", 10, arriba - 8);

        if (ns.isEmpty()) {
            return;
        }

        // Rango del eje Y: potencias de 10 que contienen todos los tiempos medidos hasta ahora
        double min = Double.MAX_VALUE;
        double max = 0;
        for (int i = 0; i < ns.size(); i++) {
            min = Math.min(min, Math.min(msSin.get(i), msCon.get(i)));
            max = Math.max(max, Math.max(msSin.get(i), msCon.get(i)));
        }
        int expMin = (int) Math.floor(Math.log10(Math.max(min, 1e-3)));
        int expMax = (int) Math.ceil(Math.log10(max));
        if (expMax == expMin) {
            expMax = expMin + 1;
        }

        // Líneas horizontales en cada potencia de 10
        for (int e = expMin; e <= expMax; e++) {
            int y = yDe(Math.pow(10, e), expMin, expMax, arriba, alto);
            g2.setColor(new Color(0xE0E0E0));
            g2.drawLine(izq, y, izq + ancho, y);
            g2.setColor(Color.DARK_GRAY);
            String etiqueta = String.format(e >= 0 ? "%.0f" : "%." + (-e) + "f", Math.pow(10, e));
            g2.drawString(etiqueta, izq - 8 - fm.stringWidth(etiqueta), y + 4);
        }

        dibujarCurva(g2, msSin, COLOR_SIN, expMin, expMax, izq, ancho, arriba, alto);
        dibujarCurva(g2, msCon, COLOR_CON, expMin, expMax, izq, ancho, arriba, alto);

        // Referencias
        int xLeyenda = izq + 12;
        g2.setColor(COLOR_SIN);
        g2.fillRect(xLeyenda, arriba + 8, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawString("Sin Greedy", xLeyenda + 18, arriba + 19);
        g2.setColor(COLOR_CON);
        g2.fillRect(xLeyenda, arriba + 28, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawString("Con Greedy", xLeyenda + 18, arriba + 39);
    }

    private void dibujarCurva(Graphics2D g2, List<Double> valores, Color color, int expMin, int expMax,
                              int izq, int ancho, int arriba, int alto) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f));
        int xAnt = 0;
        int yAnt = 0;
        for (int i = 0; i < ns.size(); i++) {
            int x = xDe(ns.get(i), izq, ancho);
            int y = yDe(Math.max(valores.get(i), 1e-3), expMin, expMax, arriba, alto);
            if (i > 0) {
                g2.drawLine(xAnt, yAnt, x, y);
            }
            g2.fillOval(x - 3, y - 3, 6, 6);
            xAnt = x;
            yAnt = y;
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private int xDe(int n, int izq, int ancho) {
        return izq + (int) ((double) (n - nMin) / (nMax - nMin) * ancho);
    }

    private int yDe(double ms, int expMin, int expMax, int arriba, int alto) {
        double fraccion = (Math.log10(ms) - expMin) / (expMax - expMin);
        return arriba + alto - (int) (fraccion * alto);
    }
}
