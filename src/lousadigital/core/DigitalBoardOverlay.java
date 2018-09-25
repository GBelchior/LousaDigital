/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.core;

import lousadigital.camera.processors.CameraCursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author gabri
 */
public abstract class DigitalBoardOverlay
{
//    protected int width;
//    protected int height;

    protected Rectangle rect;

    public DigitalBoardOverlay(Rectangle rect)
    {
        this.rect = rect;
    }

    public abstract void drawOverlay(Graphics2D destGraphics, CameraCursor cursor);
//    
//    public int getWidth()
//    {
//        return width;
//    }
//
//    public void setWidth(int width)
//    {
//        this.width = width;
//    }
//
//    public int getHeight()
//    {
//        return height;
//    }
//
//    public void setHeight(int height)
//    {
//        this.height = height;
//    }

    public Rectangle getRect()
    {
        return rect;
    }

    public void setRect(Rectangle rect)
    {
        this.rect = rect;
    }
}
