/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.core;

import lousadigital.camera.processors.CursorCalibrator;
import lousadigital.camera.processors.CameraCursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import lousadigital.camera.CameraFeed;
import lousadigital.camera.processors.CameraMirror;
import lousadigital.interfaces.ICameraFeedFrameCompletedListener;
import org.opencv.core.Scalar;

/**
 *
 * @author gabri
 */
public class DigitalBoard implements ICameraFeedFrameCompletedListener
{
    private final CameraFeed feed;
    private final CameraCursor cursor;
    private final ArrayList<DigitalBoardOverlay> overlays;

    private final CursorCalibrator cursorCalibrator;

    private Scalar cursorMaskMin;
    private Scalar cursorMaskMax;
    private int cursorClickSize;

    private boolean calibrationMode;

    private Image finalFrame;
    private Graphics finalGraphics;
    private Graphics destGraphics;
    private int destWidth;
    private int destHeight;

    public DigitalBoard()
    {
        feed = new CameraFeed();
        feed.addFrameCompletedListener(this);

        cursor = new CameraCursor();
        overlays = new ArrayList<>();

        cursorMaskMin = new Scalar(0, 0, 0);
        cursorMaskMax = new Scalar(0, 0, 0);

        cursorCalibrator = new CursorCalibrator();
    }

    private void initialize(Graphics destGraphics, int width, int height, int desiredFPS)
    {
        cursor.setBoardWidth(width);
        cursor.setBoardHeight(height);
        cursor.setCursorMaskMin(cursorMaskMin);
        cursor.setCursorMaskMax(cursorMaskMax);
        cursor.setCursorClickedSize(cursorClickSize);

        destWidth = width;
        destHeight = height;

        addPreProcessors();
        addPostProcessors();

        finalFrame = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        finalGraphics = finalFrame.getGraphics();
        this.destGraphics = destGraphics;

        if (!calibrationMode)
            addOverlays(width, height);
    }

    public void start(Graphics destGraphics, int width, int height, int desiredFPS)
    {
        if (feed.isRunning())
        {
            return;
        }

        initialize(destGraphics, width, height, desiredFPS);

        feed.start(finalGraphics, width, height, desiredFPS);
    }

    public void start(Graphics destRawGraphics, Graphics destGraphics, int rawWidth, int rawHeight, int width, int height, int desiredFPS)
    {
        if (feed.isRunning())
        {
            return;
        }

        initialize(destGraphics, rawWidth, rawHeight, desiredFPS);

        feed.start(destRawGraphics, destGraphics, rawWidth, rawHeight, width, height, desiredFPS);
    }

    public void stop()
    {
        if (!feed.isRunning())
        {
            return;
        }

        feed.stop();

        if (destGraphics != null)
        {
            destGraphics.clearRect(0, 0, destWidth, destHeight);
        }
        destGraphics = null;
        finalGraphics = null;
    }

    public void enterCalibrationMode()
    {
        stop();

        calibrationMode = true;
        overlays.clear();
        addPreProcessors();
        addPostProcessors();
    }

    public void exitCalibrationMode()
    {
        stop();

        cursorMaskMin = new Scalar(cursorCalibrator.getHMin(), cursorCalibrator.getSMin(), cursorCalibrator.getVMin());
        cursorMaskMax = new Scalar(cursorCalibrator.getHMax(), cursorCalibrator.getSMax(), cursorCalibrator.getVMax());
        cursorClickSize = cursorCalibrator.getCursorClickedSize();

        calibrationMode = false;
    }

    public CursorCalibrator getCursorCalibrator()
    {
        return cursorCalibrator;
    }

    private void addPreProcessors()
    {
        feed.clearPreProcessors();
        feed.addPreProcessor(new CameraMirror());
    }

    private void addPostProcessors()
    {
        feed.clearPostProcessors();

        if (calibrationMode)
        {
            feed.addPostProcessor(cursorCalibrator);
        }
        else
        {
            feed.addPostProcessor(cursor);
        }
    }

    private void addOverlays(int width, int height)
    {
        overlays.clear();
        int colorPickerHeight = 450;//200;

        overlays.add(new DrawingArea(new Rectangle(50, 0, width - 50, height)));
        overlays.add(new ColorPicker(new Rectangle(0, (int)((height / 2f) - (colorPickerHeight / 2f)), 50, colorPickerHeight)));
        //overlays.add(new ColorPicker(new Rectangle(0, 80, 50, colorPickerHeight - 160)));

    }

    @Override
    public void frameCompleted()
    {
        if (destGraphics == null)
        {
            return;
        }

        Graphics2D g2d = (Graphics2D) finalGraphics;
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        overlays.forEach(o ->
        {
            o.drawOverlay((Graphics2D) finalGraphics, cursor);
        });

        destGraphics.drawImage(finalFrame, 0, 0, finalFrame.getWidth(null), finalFrame.getHeight(null), null);
    }
}
