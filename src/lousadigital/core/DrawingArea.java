/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.core;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import lousadigital.camera.processors.CameraCursor;

/**
 *
 * @author gabri
 */
public class DrawingArea extends DigitalBoardOverlay
{
    private final Image drawing;
    private final Graphics2D drawingGraphics;

    public DrawingArea(Rectangle rect)
    {
        super(rect);

        drawing = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_4BYTE_ABGR);
        drawingGraphics = (Graphics2D) drawing.getGraphics();
        drawingGraphics.setComposite(AlphaComposite.Src);

        drawingGraphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        
        //drawingGraphics.setColor(new Color(0, 0, 0, 0));
        //drawingGraphics.fillRect(0, 0, rect.width, rect.height);
    }

    @Override
    public void drawOverlay(Graphics2D destGraphics, CameraCursor cursor)
    {
        paintCursor(destGraphics, cursor);
    }

    private void paintCursor(Graphics2D destGraphics, CameraCursor cursor)
    {
        Color currentColor = cursor.getColor();

        int ovalX = (int) (cursor.getX() - cursor.getWidth() / 2f);
        int ovalY = (int) (cursor.getY() - cursor.getHeight() / 2f);

        int ovalXRel = (int) ((cursor.getX() - rect.x) - cursor.getWidth() / 2f);
        int ovalYRel = (int) ((cursor.getY() - rect.y) - cursor.getHeight() / 2f);

        int ovalWidth = cursor.getWidth();
        int ovalHeight = cursor.getHeight();

        if (cursor.isClicked())
        {
            drawingGraphics.setColor(currentColor);
            drawingGraphics.fillOval(ovalXRel, ovalYRel, ovalWidth, ovalHeight);
        }

        destGraphics.drawImage(drawing, rect.x, rect.y, rect.width, rect.height, null);

        if (cursor.isInScreen() && cursor.isBoundedBy(rect))
        {
            destGraphics.setColor(currentColor);
            destGraphics.fillOval(ovalX, ovalY, ovalWidth, ovalHeight);
        }
    }
}
