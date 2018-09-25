/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.camera;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import lousadigital.interfaces.ICameraFeedFrameCompletedListener;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import lousadigital.interfaces.ICameraFeedPostProcessor;
import lousadigital.interfaces.ICameraFeedPreProcessor;

/**
 *
 * @author gabri
 */
public class CameraFeed
{
    private final ArrayList<ICameraFeedFrameCompletedListener> listeners;
    private final ArrayList<ICameraFeedPreProcessor> preProcessors;
    private final ArrayList<ICameraFeedPostProcessor> postProcessors;

    private final VideoCapture videoCapture;
    private Thread drawerThread;
    private final Runnable drawerMethod;
    private boolean running;

    private final Mat currentFrame;

    private Graphics destPreProcessedGraphics;
    private Graphics destPostProcessedGraphics;
    private int desiredFPS;

    private int rawWidth;
    private int rawHeight;
    private int width;
    private int height;

    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public CameraFeed()
    {
        listeners = new ArrayList<>();
        preProcessors = new ArrayList<>();
        postProcessors = new ArrayList<>();
        videoCapture = new VideoCapture(0);

        currentFrame = new Mat();

        drawerMethod = () ->
        {
            Instant frameStartTime;

            while (running)
            {
                frameStartTime = Instant.now();

                readNextFrame();

                this.listeners.forEach(l -> l.frameCompleted());

                try
                {
                    long frameTime = Duration.between(Instant.now(), frameStartTime).toMillis();
                    if (frameTime < this.desiredFPS)
                    {
                        Thread.sleep((1000 / this.desiredFPS) - frameTime);
                    }
                }
                catch (InterruptedException ex)
                {
                }
            }
        };
    }

    public void start(Graphics destGraphics, int width, int height, int desiredFPS)
    {
        start(null, destGraphics, 0, 0, width, height, desiredFPS);
    }

    public void start(Graphics destPreProcessedGraphics, Graphics destPostProcessedGraphics, int rawWidth, int rawHeight, int width, int height, int desiredFPS)
    {
        this.destPreProcessedGraphics = destPreProcessedGraphics;
        this.destPostProcessedGraphics = destPostProcessedGraphics;
        this.desiredFPS = desiredFPS;
        this.rawWidth = rawWidth;
        this.rawHeight = rawHeight;
        this.width = width;
        this.height = height;

        running = true;

        drawerThread = new Thread(drawerMethod);
        drawerThread.start();
    }

    public void stop()
    {
        running = false;

        if (!drawerThread.isInterrupted())
        {
            try
            {
                drawerThread.join();
                drawerThread.interrupt();
                drawerThread = null;
            }
            catch (InterruptedException ex)
            {
            }
        }

        destPostProcessedGraphics.clearRect(0, 0, width, height);
    }

    public void addPreProcessor(ICameraFeedPreProcessor preProcessor)
    {
        preProcessors.add(preProcessor);
    }

    public void clearPreProcessors()
    {
        preProcessors.clear();
    }

    public void addPostProcessor(ICameraFeedPostProcessor postProcessor)
    {
        postProcessors.add(postProcessor);
    }

    public void clearPostProcessors()
    {
        postProcessors.clear();
    }

    public void addFrameCompletedListener(ICameraFeedFrameCompletedListener listener)
    {
        listeners.add(listener);
    }

    public boolean isRunning()
    {
        return running;
    }

    private void readNextFrame()
    {
        videoCapture.read(currentFrame);

        preProcessors.forEach(p -> p.Process(currentFrame));

        if (destPreProcessedGraphics != null)
        {
            destPreProcessedGraphics.drawImage(matToBufferedImage(currentFrame), 0, 0, rawWidth, rawHeight, null);
        }

        postProcessors.forEach(p -> p.Process(currentFrame));

        if (destPostProcessedGraphics != null)
        {
            destPostProcessedGraphics.drawImage(matToBufferedImage(currentFrame), 0, 0, width, height, null);
        }
    }

    private BufferedImage matToBufferedImage(Mat mat)
    {
        BufferedImage bi = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);

        byte[] biBytes = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, biBytes);

        return bi;
    }
}
