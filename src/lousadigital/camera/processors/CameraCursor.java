/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.camera.processors;

import java.awt.Color;
import java.awt.Rectangle;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import lousadigital.interfaces.ICameraFeedPostProcessor;

/**
 *
 * @author gabri
 */
public class CameraCursor implements ICameraFeedPostProcessor
{
    private Scalar cursorMaskMin;
    private Scalar cursorMaskMax;
    private final MatOfPoint cursorPoints;
    private final Mat cursorMask;

    private int x;
    private int y;
    private int width;
    private int height;

    private int boardWidth;
    private int boardHeight;
    private int cursorClickedSize;

    private boolean inScreen;
    private boolean clicked;
    
    private Color color;

    public CameraCursor()
    {
        cursorPoints = new MatOfPoint();
        cursorMask = new Mat();
        
        color = Color.BLACK;
    }

    @Override
    public void Process(Mat mat)
    {
        Imgproc.cvtColor(mat, cursorMask, Imgproc.COLOR_BGR2HSV);
        Core.inRange(cursorMask, cursorMaskMin, cursorMaskMax, cursorMask);
        Core.findNonZero(cursorMask, cursorPoints);

        if (cursorPoints.empty())
        {
            inScreen = false;
            return;
        }

        inScreen = true;

        Rect cursorPosition = Imgproc.boundingRect(cursorPoints);

        int partialX = (int) (cursorPosition.x + (cursorPosition.width / 2f));
        int partialY = (int) (cursorPosition.y + (cursorPosition.height / 2f));

        double perX = (double) partialX / mat.width();
        double perY = (double) partialY / mat.height();

        double perWidth = (double) cursorPosition.width / mat.width();
        double perHeight = (double) cursorPosition.height / mat.height();

        x = (int) (boardWidth * perX);
        y = (int) (boardHeight * perY);

        width = (int) (boardWidth * perWidth);
        height = (int) (boardHeight * perHeight);

        clicked = cursorPosition.width > cursorClickedSize || cursorPosition.height > cursorClickedSize;
    }

    public Scalar getCursorMaskMin()
    {
        return cursorMaskMin;
    }

    public void setCursorMaskMin(Scalar cursorMaskMin)
    {
        this.cursorMaskMin = cursorMaskMin;
    }

    public Scalar getCursorMaskMax()
    {
        return cursorMaskMax;
    }

    public void setCursorMaskMax(Scalar cursorMaskMax)
    {
        this.cursorMaskMax = cursorMaskMax;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getRelativeX(Rectangle rect)
    {
        return getX() - rect.x;
    }

    public int getRelativeY(Rectangle rect)
    {
        return getY() - rect.y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getBoardWidth()
    {
        return boardWidth;
    }

    public void setBoardWidth(int boardWidth)
    {
        this.boardWidth = boardWidth;
    }

    public int getBoardHeight()
    {
        return boardHeight;
    }

    public void setBoardHeight(int boardHeight)
    {
        this.boardHeight = boardHeight;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }
    
    public int getCursorClickedSize()
    {
        return cursorClickedSize;
    }

    public void setCursorClickedSize(int cursorClickedSize)
    {
        this.cursorClickedSize = cursorClickedSize;
    }

    public boolean isInScreen()
    {
        return inScreen;
    }

    public boolean isClicked()
    {
        return clicked;
    }

    public boolean isBoundedBy(Rectangle rect)
    {
        return getX() >= rect.x && getX() <= (rect.x + rect.width)
                && getY() >= rect.y && getY() <= (rect.y + rect.height);
    }
}
