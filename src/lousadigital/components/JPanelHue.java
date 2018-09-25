/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import javax.swing.JPanel;

/**
 *
 * @author gabri
 */
public class JPanelHue extends JPanel
{
    private final float[] fractions;
    private final Color[] colors;

    public JPanelHue()
    {
        fractions = new float[]
        {
            0,
            1f / 6f,
            2f / 6f,
            3f / 6f,
            4f / 6f,
            5f / 6f,
            1
        };

        colors = new Color[]
        {
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.MAGENTA,
            Color.RED
        };
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;

        LinearGradientPaint gradient = new LinearGradientPaint(0, getHeight() / 2f, getWidth(), getHeight() / 2f, fractions, colors);
        g2d.setPaint(gradient);

        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
