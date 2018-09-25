/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.camera.processors;

import lousadigital.interfaces.IPropertyChangedHandler;
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
public class CursorCalibrator implements ICameraFeedPostProcessor
{
    private int hMin;
    private int sMin;
    private int vMin;

    private int hMax;
    private int sMax;
    private int vMax;

    private int cursorClickedSize;
    private boolean cursorClicked;

    private final MatOfPoint cursorPoints;

    private IPropertyChangedHandler propertyChangedHandler;

    public CursorCalibrator()
    {
        cursorPoints = new MatOfPoint();
    }

    @Override
    public void Process(Mat mat)
    {
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV);
        Core.inRange(mat, new Scalar(hMin, sMin, vMin), new Scalar(hMax, sMax, vMax), mat);
        
        Core.findNonZero(mat, cursorPoints);
        
        Rect cursorPosition = Imgproc.boundingRect(cursorPoints);

        setCursorClicked(cursorPosition.width > cursorClickedSize || cursorPosition.height > cursorClickedSize);

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2BGR);
    }

    public int getHMin()
    {
        return hMin;
    }

    public void setHMin(int hMin)
    {
        this.hMin = hMin;
    }

    public int getSMin()
    {
        return sMin;
    }

    public void setSMin(int sMin)
    {
        this.sMin = sMin;
    }

    public int getVMin()
    {
        return vMin;
    }

    public void setVMin(int vMin)
    {
        this.vMin = vMin;
    }

    public int getHMax()
    {
        return hMax;
    }

    public void setHMax(int hMax)
    {
        this.hMax = hMax;
    }

    public int getSMax()
    {
        return sMax;
    }

    public void setSMax(int sMax)
    {
        this.sMax = sMax;
    }

    public int getVMax()
    {
        return vMax;
    }

    public void setVMax(int vMax)
    {
        this.vMax = vMax;
    }

    public int getCursorClickedSize()
    {
        return cursorClickedSize;
    }

    public void setCursorClickedSize(int cursorClickedSize)
    {
        this.cursorClickedSize = cursorClickedSize;
    }

    public boolean isCursorClicked()
    {
        return cursorClicked;
    }

    private void setCursorClicked(boolean cursorClicked)
    {
        this.cursorClicked = cursorClicked;

        if (propertyChangedHandler != null)
        {
            propertyChangedHandler.onPropertyChanged("cursorClicked", cursorClicked);
        }
    }

    public void setPropertyChangedHandler(IPropertyChangedHandler propertyChangedHandler)
    {
        this.propertyChangedHandler = propertyChangedHandler;
    }
}
