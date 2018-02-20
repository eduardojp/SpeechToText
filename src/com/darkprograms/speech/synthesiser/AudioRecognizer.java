package com.darkprograms.speech.synthesiser;

/**
 *
 * @author eduardo
 */
public abstract class AudioRecognizer {
    protected String audioSegmentPath;
    
    public AudioRecognizer(String audioSegmentPath) {
        this.audioSegmentPath = audioSegmentPath;
    }
    
    public String call() {
        return null;
    }
}