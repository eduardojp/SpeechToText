package com.darkprograms.speech.synthesiser;

import java.util.LinkedList;

/**
 *
 * @author eduardo
 */
public class AudioSegmentationResult {
    private static final int BEGIN = 0;
    private static final int END = 1;
    private static final int MIN_SEGMENT_MILLIS = 1000;
    private static final int MAX_SEGMENT_MILLIS = 10000;
    
    public LinkedList<AudioSegment> freezeSegmentList;
    public LinkedList<AudioSegment> audioSegmentList;
    public int amplitudeThreshold;
    public int freezeDurationThreshold;
    
    public AudioSegmentationResult(int amplitudeThreshold, int freezeDurationThreshold) {
        this.amplitudeThreshold = amplitudeThreshold;
        this.freezeDurationThreshold = freezeDurationThreshold;
        this.freezeSegmentList = new LinkedList<>();
        this.audioSegmentList = new LinkedList<>();
    }
    
    public void addFreezeSegments(AudioSegment freezeSegment) {
        this.freezeSegmentList.add(freezeSegment);
    }
    
    public void createAudioSegments(int videoDuration) {
        int audioSegmentBEGIN = 0;
        int audioSegmentEND = 0;
//        System.out.printf("BEGIN Segment: %s\n", Utils.toStringTime(audioSegment[BEGIN]));

        for(AudioSegment freeze : freezeSegmentList) {
            audioSegmentEND = freeze.timeBeginMillis;
//            System.out.printf("END Segment: %s\n", Utils.toStringTime(audioSegment[END]));

            if(audioSegmentEND - audioSegmentBEGIN <= MAX_SEGMENT_MILLIS) {
                audioSegmentList.add(new AudioSegment(audioSegmentBEGIN, audioSegmentEND, null));
                if(audioSegmentList.getLast().getDuration() > MAX_SEGMENT_MILLIS) {
                    System.out.println("MOPA 1: " + audioSegmentList.getLast().getDuration());
                    System.exit(0);
                }
            }

//            System.out.printf("---------------------\n");

            audioSegmentBEGIN = freeze.timeEndMillis;
//            System.out.printf("BEGIN Segment: %s\n", Utils.toStringTime(audioSegment[BEGIN]));
        }

        if(!freezeSegmentList.isEmpty()) {
            audioSegmentEND = videoDuration;
//            System.out.printf("END Segment: %s\n", Utils.toStringTime(audioSegment[END]));

            if(audioSegmentEND - audioSegmentBEGIN <= MAX_SEGMENT_MILLIS) {
                audioSegmentList.add(new AudioSegment(audioSegmentBEGIN, audioSegmentEND, null));
                if(audioSegmentList.getLast().getDuration() > MAX_SEGMENT_MILLIS) {
                    System.out.println("MOPA 2: " + audioSegmentList.getLast().getDuration());
                    System.exit(0);
                }
            }
//            System.out.printf("---------------------\n");
        }
        
        //mergeAudioSegmentsOld(audioSegmentList);
    }
    
    //FIXME
    public void addAudioSegmentsTo(LinkedList<AudioSegment> finalAudioSegmentList) {
        System.out.println("AUDIO SEGMENT LIST SIZE: " + finalAudioSegmentList.size());

        for(AudioSegment audioSegment : audioSegmentList) {
            if(/*audioSegment.getDuration() >= MIN_SEGMENT_MILLIS && */audioSegment.getDuration() <= MAX_SEGMENT_MILLIS) {
                boolean toAdd = true;

                for(AudioSegment as : finalAudioSegmentList) {
                    if(audioSegment.isOverlapping(as)) {
                        toAdd = false;
                        System.out.println(
                            "CONFLICT: "+Utils.toStringTime(as.timeBeginMillis)+"-"+Utils.toStringTime(as.timeEndMillis)
                        );
                        break;
                    }
                }
                //add = true;

                if(toAdd) {
                    finalAudioSegmentList.add(audioSegment);
                    System.out.println("ADDED");
                }
            }
        }
        
        //mergeAudioSegments(finalAudioSegmentList);
        //mergeAudioSegmentsOld(finalAudioSegmentList);
        
        System.out.println("AUDIO SEGMENT LIST SIZE: " + finalAudioSegmentList.size());
    }
    
    @Deprecated
    private void mergeAudioSegmentsOld(LinkedList<int[]> finalAudioSegmentList) {
        boolean merged = true;
        
        while(merged) {
            merged = false;
            
            int minDistance = 999999;
            int[] asMerge1 = null;
            int[] asMerge2 = null;

            int[] prev = null;
            for(int[] as : finalAudioSegmentList) {
                if(prev != null) {
                    if(as[BEGIN] - prev[END] < minDistance && as[END] - prev[BEGIN] < MAX_SEGMENT_MILLIS) {
                        minDistance = as[BEGIN] - prev[END];
                        asMerge1 = prev;
                        asMerge2 = as;
                    }
                }
                
                prev = as;
            }

            if(asMerge1 != null && asMerge2 != null) {
                System.out.print("MERGED: " + asMerge1[BEGIN] + "->" + asMerge1[END] + " / " + asMerge2[BEGIN] + "->" + asMerge2[END]);
                
                if(asMerge1[BEGIN] < asMerge2[BEGIN]) {
                    asMerge1[END] = asMerge2[END];
                }
                else {
                    asMerge1[BEGIN] = asMerge2[BEGIN];
                }

                finalAudioSegmentList.remove(asMerge2);
                merged= true;
                
                System.out.println(" : " + asMerge1[BEGIN] + "->" + asMerge1[END]);
            }
        }
    }
}