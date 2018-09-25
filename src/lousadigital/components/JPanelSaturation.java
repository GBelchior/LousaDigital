/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author gabri
 */
public class JPanelSaturation extends JPanel
{
    private int hue;

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gradient = new GradientPaint(0, getHeight() / 2f, Color.WHITE, getWidth(), getHeight() / 2f, Color.getHSBColor(hue / 360f, 1, 1));
        g2d.setPaint(gradient);
        
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public int getHue()
    {
        return hue;
    }

    public void setHue(int hue)
    {
        this.hue = hue;
        repaint();
    }
}
