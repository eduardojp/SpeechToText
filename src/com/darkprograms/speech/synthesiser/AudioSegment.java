package com.darkprograms.speech.synthesiser;

/**
 *
 * @author eduardo
 */
public class AudioSegment implements Comparable<AudioSegment> {
    public int timeBeginMillis;
    public int timeEndMillis;
    public String recognizedText;
    
    public AudioSegment(int timeBeginMillis, int timeEndMillis, String recognizedText) {
        this.timeBeginMillis = timeBeginMillis;
        this.timeEndMillis = timeEndMillis;
        this.recognizedText = recognizedText;
        
        //ERROR
        if(timeBeginMillis > timeEndMillis) {
            System.out.println("ERROR timeBeginMillis > timeEndMillis: " + timeBeginMillis + " " + timeEndMillis);
            System.exit(0);
        }
    }
    
    public boolean isOverlapping(AudioSegment as) {
        return this.timeBeginMillis >= as.timeBeginMillis && this.timeBeginMillis <= as.timeEndMillis || //Compartilha início
            this.timeEndMillis >= as.timeBeginMillis && this.timeEndMillis <= as.timeEndMillis; //Compartilha fim
    }
    
    public int getDuration() {
        return timeEndMillis - timeBeginMillis;
    }
    
    public int getDistanceTo(AudioSegment as) {
        if(isOverlapping(as)) {
            return 0;
        }
        
        if(this.timeBeginMillis < as.timeBeginMillis) {
            return this.timeEndMillis - as.timeBeginMillis;
        }
        else {
            return this.timeBeginMillis - as.timeEndMillis;
        }
    }

    @Override
    public int compareTo(AudioSegment as) {
        return this.timeBeginMillis - as.timeBeginMillis;
    }
}
