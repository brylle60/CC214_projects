package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Simple3DRenderer extends JPanel {
    private final int width = 800;
    private final int height = 600;
    private double[][] cube;
    private double angle = 0;

    public Simple3DRenderer() {
        setPreferredSize(new Dimension(width, height));
        initCube();
    }

    private void initCube() {
        // Define cube vertices (x, y, z)
        cube = new double[][] {
                {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1},
                {-1, -1, 1}, {1, -1, 1}, {1, 1, 1}, {-1, 1, 1}
        };
    }

    private int[][] project(double[][] points3D) {
        int[][] points2D = new int[points3D.length][2];
        double distance = 4;

        for (int i = 0; i < points3D.length; i++) {
            double x = points3D[i][0];
            double y = points3D[i][1];
            double z = points3D[i][2];

            // Rotate around Y axis
            double rotatedX = x * Math.cos(angle) - z * Math.sin(angle);
            double rotatedZ = x * Math.sin(angle) + z * Math.cos(angle);

            // Project 3D to 2D
            double scale = distance / (distance + rotatedZ);
            points2D[i][0] = (int) (rotatedX * scale * 100) + width/2;
            points2D[i][1] = (int) (y * scale * 100) + height/2;
        }

        return points2D;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Draw cube
        g2d.setColor(Color.GREEN);
        int[][] projectedPoints = project(cube);

        // Draw edges
        int[][] edges = {
                {0,1}, {1,2}, {2,3}, {3,0},  // front face
                {4,5}, {5,6}, {6,7}, {7,4},  // back face
                {0,4}, {1,5}, {2,6}, {3,7}   // connecting edges
        };

        for (int[] edge : edges) {
            g2d.drawLine(
                    projectedPoints[edge[0]][0], projectedPoints[edge[0]][1],
                    projectedPoints[edge[1]][0], projectedPoints[edge[1]][1]
            );
        }

        // Rotate for next frame
        angle += 0.02;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Cube Renderer");
        Simple3DRenderer renderer = new Simple3DRenderer();
        frame.add(renderer);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Animation loop
        Timer timer = new Timer(16, e -> renderer.repaint());
        timer.start();
    }
}