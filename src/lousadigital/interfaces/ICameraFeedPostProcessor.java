/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.interfaces;

import org.opencv.core.Mat;

/**
 *
 * @author gabri
 */
public interface ICameraFeedPostProcessor
{
    public void Process(Mat mat);
}
