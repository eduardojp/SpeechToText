package com.darkprograms.speech.synthesiser;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author eduardo
 */
public class Main {
    public static void main(String[] args) {
        //String filePath = "/home/eduardo/Downloads/audio-file.flac";
//        String filePath = "/home/eduardo/NetBeansProjects/freeze-detector/test.ts";
//        String filePath = "/home/eduardo/Desktop/freeze-detector-17-08-08/test_seg5.flac";
        String filePath = "/home/eduardo/Downloads/mission_succ.flac";
//        String filePath = "/home/eduardo/NetBeansProjects/freeze-detector/test_seg2.flac";
        
        try {
//            HashMap<Integer, AudioSegmentationResult> audioSegmentationResultMap = AudioProcessor.getFreezeIntervals(filePath);
//            LinkedList<AudioSegment> audioSegmentList = AudioProcessor.getAudioSegments(audioSegmentationResultMap);
//            AudioProcessor.saveAudioSegments(filePath, audioSegmentList);
            
//            GoogleAudioRecognizer googleAudioRecognizer = new GoogleAudioRecognizer(filePath);
//            googleAudioRecognizer.call();
//            IBMAudioRecognizer ibmAudioRecognizer = new IBMAudioRecognizer(filePath);
//            ibmAudioRecognizer.call();
            SpeechmaticAudioRecognizer speechmaticAudioRecognizer = new SpeechmaticAudioRecognizer(filePath);
            speechmaticAudioRecognizer.call();
        }
        catch(Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}