package com.darkprograms.speech.synthesiser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author eduardo
 */
public class AudioProcessor {
    private static final String silenceDetectorPath = "/home/eduardo/NetBeansProjects/freeze-detector/freeze-detector";
    private static final Pattern pattern = Pattern.compile("FD\\[(\\d+)\\].*inicio\\[(\\d+):(\\d+):(\\d+).(\\d+)\\] duracao\\[(\\d+)\\]");
    
    private static int videoDurationMillis;
    
    public static HashMap<Integer, AudioSegmentationResult> getFreezeIntervals(String audioFileName) throws IOException {
        HashMap<Integer, AudioSegmentationResult> map = new HashMap<>();
        int[] thresholds = new int[] {5,10,15,20};

        StringBuilder builder = new StringBuilder(thresholds[0] + "");
        map.put(1, new AudioSegmentationResult(thresholds[0], 40));
        
        for(int id = 2; id <= thresholds.length; id++) {
            map.put(id, new AudioSegmentationResult(thresholds[id-1], 40));
            builder.append(",");
            builder.append(thresholds[id-1]);
        }
        String thresholdListString = builder.toString();
        
//            ProcessBuilder builder = new ProcessBuilder(
//                silenceDetectorPath,
//                "-i", audioFileName
//            );
//
//            //builder.redirectErrorStream(true);
//            Process process = builder.start();

        //String commandLine = silenceDetectorPath + " -I -i " + audioFileName  + " -t " + t + " -p " + t + " -r " + rangeThreshold;
        String commandLine = silenceDetectorPath + " -i " + audioFileName + " -r " + thresholdListString;

        System.out.println(commandLine);
        System.out.println("----------------");

        Process process = Runtime.getRuntime().exec(commandLine);

        //logger.info("Processo executado");
        //logger.info(commandLine);

        String inputLine;
        BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        float lastEnd = 0;

        System.out.println("Segmentando audio 0%");

        while((inputLine = processReader.readLine()) != null) {
            System.out.println(">> " + inputLine);

            if(inputLine.startsWith("FD")) {
                Matcher matcher = pattern.matcher(inputLine);
                matcher.find();
                
                int id = Integer.parseInt(matcher.group(1));
                int hours = Integer.parseInt(matcher.group(2));
                int minutes = Integer.parseInt(matcher.group(3));
                int seconds = Integer.parseInt(matcher.group(4));
                int milliseconds = Integer.parseInt(matcher.group(5));
                int freezeDuration = Integer.parseInt(matcher.group(6));
                
                //FIXME
//                if(id!= 1 && id != 2 && id != 3) {
//                    continue;
//                }

                int timeBeginMillis = (hours*3600 + minutes*60 + seconds) * 1000 + milliseconds;
                int timeEndMillis = timeBeginMillis + freezeDuration;
                
                AudioSegment freezeSegment = new AudioSegment(timeBeginMillis, timeEndMillis, null);

                System.out.println("BEGIN Freeze: " + Utils.toStringTime(freezeSegment.timeBeginMillis));
                System.out.println("END Freeze: " + Utils.toStringTime(freezeSegment.timeEndMillis));

                AudioSegmentationResult audioSegmentationResult = map.get(id);
                audioSegmentationResult.addFreezeSegments(freezeSegment);
            }
            else if(inputLine.startsWith("PROGRESS")) {
                String[] split = inputLine.split(" ");
                float progress = Float.parseFloat(split[1]);

                System.out.println("Segmentando áudio " + progress + "%");
            }
            else if(inputLine.startsWith("INFO")) {
                String[] split = inputLine.split(" ");
                videoDurationMillis = Integer.parseInt(split[1]);
            }
        }

        System.out.println("Segmentando áudio 100%");

        return map;
    }
    
    public static LinkedList<AudioSegment> getAudioSegments(HashMap<Integer, AudioSegmentationResult> audioSegmentationResultMap) {
        LinkedList<AudioSegment> audioSegmentList = new LinkedList<>();
        
        //Ordena as listas de silêncio da mais restritiva para a menos restritiva
        LinkedList<Integer> keyList = new LinkedList<>(audioSegmentationResultMap.keySet());
        Collections.sort(keyList);
        //Collections.reverse(keyList);
        
        //Percoore as listas de freeze em diferentes limiares
        for(Integer threshold : keyList) {
            AudioSegmentationResult audioSegmentationResult = audioSegmentationResultMap.get(threshold);
            
            if(audioSegmentationResult.freezeSegmentList.isEmpty()) {
                System.out.println("EMPTY FREEZE LIST: " + threshold);
                continue;
            }
            System.out.println("FREEZE LIST " + threshold + " SIZE: " + audioSegmentationResult.freezeSegmentList.size());
            
            audioSegmentationResult.createAudioSegments(videoDurationMillis);
            audioSegmentationResult.addAudioSegmentsTo(audioSegmentList);
        }
        
        //Collections.sort(audioSegmentList);
        
        mergeAudioSegments(audioSegmentList);
        
        int expansionMillis = 2000;
        
        //Expansão
        AudioSegment prev = null;
        for(AudioSegment as : audioSegmentList) {
            if(prev != null) {
                prev.timeEndMillis += expansionMillis;
                as.timeBeginMillis -= expansionMillis;
                
                if(prev.timeEndMillis > as.timeBeginMillis) {
//                    int mid = (as.timeBeginMillis + prev.timeEndMillis) / 2;
//                    prev.timeEndMillis = mid;
//                    as.timeBeginMillis = mid;
                    int temp = prev.timeEndMillis;
                    prev.timeEndMillis = as.timeBeginMillis + expansionMillis;
                    as.timeBeginMillis = temp - expansionMillis;
                }
            }
            prev = as;
        }
        
        //Expansão do primeiro e do último segmento
        if(!audioSegmentList.isEmpty()) {
            AudioSegment firstAudioSegment = audioSegmentList.peek();
            AudioSegment lastAudioSegment = prev;
            
            firstAudioSegment.timeBeginMillis -= expansionMillis;
            lastAudioSegment.timeEndMillis += expansionMillis;
            
            if(firstAudioSegment.timeBeginMillis < 0) firstAudioSegment.timeBeginMillis = 0;
            if(lastAudioSegment.timeEndMillis > videoDurationMillis) lastAudioSegment.timeEndMillis = videoDurationMillis;
        }
        
        return audioSegmentList;
    }
    
    private static void mergeAudioSegments(LinkedList<AudioSegment> audioSegmentList) {
        boolean merged = true;
        
        Collections.sort(audioSegmentList);
        
        while(merged) {
            merged = false;
            
            int minDuration = 999999;
            int minDistance = 999999;
            AudioSegment asMerge1 = null;
            AudioSegment asMerge2 = null;

            int MAX_SEGMENT_MILLIS = 10000;
            
            AudioSegment prev = null;
            for(AudioSegment as : audioSegmentList) {
                if(prev != null) {
                    
                    if(prev.isOverlapping(as)) {
                        System.out.println("OVERLAPPING!!!");
                        System.exit(0);
                    }
                    if(prev.timeBeginMillis >= as.timeBeginMillis || prev.timeEndMillis >= as.timeEndMillis) {
                        System.out.println("ORDER");
                        System.exit(0);
                    }
                    
                    if(prev.getDuration() <= minDuration || as.getDuration() <= minDuration) {
                        if(as.timeBeginMillis - prev.timeEndMillis < minDistance && as.timeEndMillis - prev.timeBeginMillis <= MAX_SEGMENT_MILLIS) {
                            asMerge1 = prev;
                            asMerge2 = as;
                            minDuration = Math.min(prev.getDuration(), as.getDuration());
                            minDistance = as.timeBeginMillis - prev.timeEndMillis;
                        }
                    }
                }
                
                prev = as;
            }

            if(asMerge1 != null && asMerge2 != null) {
                System.out.print("MERGED: " + asMerge1.timeBeginMillis + "->" + asMerge1.timeEndMillis + " / " + asMerge2.timeBeginMillis + "->" + asMerge2.timeEndMillis);
                
                if(asMerge1.timeBeginMillis < asMerge2.timeBeginMillis) {
                    asMerge1.timeEndMillis = asMerge2.timeEndMillis;
                }
                else {
                    asMerge1.timeBeginMillis = asMerge2.timeBeginMillis;
                }

                audioSegmentList.remove(asMerge2);
                merged= true;
                
                System.out.println(" : " + asMerge1.timeBeginMillis + "->" + asMerge1.timeEndMillis);
            }
        }
    }
    
    public static void saveAudioSegments(String audioFilePath, LinkedList<AudioSegment> audioSegments) throws IOException, InterruptedException {
        int index = 1;
        
        for(AudioSegment as : audioSegments) {
            System.out.println(as.timeBeginMillis + " --> " + as.timeEndMillis);

            //Nomeação do segmento de áudio
            int lastDotIndex = audioFilePath.lastIndexOf('.');
            String segmentFilePath = (lastDotIndex != -1) ?
                audioFilePath.substring(0, lastDotIndex) + "_seg"+index+".flac" :
                audioFilePath + "_seg"+index+".flac";

            ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg",
                "-i", audioFilePath,
                "-ss", Utils.toStringTime(as.timeBeginMillis),
                "-t", Utils.toStringTime(as.timeEndMillis - as.timeBeginMillis),
                "-ar", Config.SAMPLE_RATE+"",
                "-ac", "1",
                "-af", "volume=enable='between(t,"+as.timeBeginMillis+","+as.timeBeginMillis+")':volume=0,volume=enable='between(t,"+as.timeEndMillis+","+as.timeEndMillis+")':volume=0",
                "-sample_fmt", "s16",
                "-y", segmentFilePath
            );

            Process process = builder.start();
            process.waitFor();

            index++;
        }
    }
}
