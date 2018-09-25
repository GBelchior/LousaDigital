/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.camera.processors;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import lousadigital.interfaces.ICameraFeedPreProcessor;

/**
 *
 * @author gabri
 */
public class CameraMirror implements ICameraFeedPreProcessor
{
    @Override
    public void Process(Mat mat)
    {
        Mat flipped = new Mat();
        mat.copyTo(flipped);

        Core.flip(mat, flipped, 1);

        flipped.copyTo(mat);
    }
}
