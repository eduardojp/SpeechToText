package com.darkprograms.speech.synthesiser;

import com.darkprograms.speech.recognizer.GoogleResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author eduardo
 */
class SpeechmaticAudioRecognizer extends AudioRecognizer {
        
    /**
     * URL to POST audio data and retrieve results
     */
    private static final String RECOGNIZER_URL = "https://api.speechmatics.com/v1.0/user/9540/jobs/?";

    private LinkedList<String> properties;
    private boolean profanityFilter = true;
    private String language = null;
    private int userID = 9540;
    private String apikey = null;
   
    public SpeechmaticAudioRecognizer(String audioSegmentPath) {
        super(audioSegmentPath);
        
        this.properties = new LinkedList<>();
        properties.add("client=chromium");
        properties.add("output=json");
        
        this.language = Config.LANGUAGE;
        this.profanityFilter = false;
        this.userID = 9540;
        this.apikey = "ZTk3YzJiNmUtODhmNy00YzZkLTkwYWQtNTY3OTNiODg1ZjM1";
    }
    
    @Override
    public String call() {
        System.out.println("Enviando " + audioSegmentPath + "...");

        GoogleResponse googleResponse = null;

        try {
            googleResponse = getRecognizedDataForFlac(new File(audioSegmentPath), 3, Config.SAMPLE_RATE);
        }
        catch(IOException ex) {
            Logger.getLogger(GoogleAudioRecognizer.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<String> allList = googleResponse.getAllPossibleResponses();
        List<String> list = new LinkedList<>();
        
        for(String response : allList) {
            if(response != null) {
                list.add(response);
            }
        }

        //list = googleResponse.getOtherPossibleResponses();

        for(String response : list) {
            System.out.println("-> " + response);
        }
        
//Utils.update(audioSegmentPath, null, id);

        return (list.isEmpty()) ? null : list.get(0);
    }

    @Deprecated
    public enum Languages{
        AUTO_DETECT("auto"),//tells Google to auto-detect the language
        ARABIC_JORDAN("ar-JO"),
        ARABIC_LEBANON("ar-LB"),
        ARABIC_QATAR("ar-QA"),
        ARABIC_UAE("ar-AE"),
        ARABIC_MOROCCO("ar-MA"),
        ARABIC_IRAQ("ar-IQ"),
        ARABIC_ALGERIA("ar-DZ"),
        ARABIC_BAHRAIN("ar-BH"),
        ARABIC_LYBIA("ar-LY"),
        ARABIC_OMAN("ar-OM"),
        ARABIC_SAUDI_ARABIA("ar-SA"),
        ARABIC_TUNISIA("ar-TN"),
        ARABIC_YEMEN("ar-YE"),
        BASQUE("eu"),
        CATALAN("ca"),
        CZECH("cs"),
        DUTCH("nl-NL"),
        ENGLISH_AUSTRALIA("en-AU"),
        ENGLISH_CANADA("en-CA"),
        ENGLISH_INDIA("en-IN"),
        ENGLISH_NEW_ZEALAND("en-NZ"),
        ENGLISH_SOUTH_AFRICA("en-ZA"),
        ENGLISH_UK("en-GB"),
        ENGLISH_US("en-US"),
        FINNISH("fi"),
        FRENCH("fr-FR"),
        GALICIAN("gl"),
        GERMAN("de-DE"),
        HEBREW("he"),
        HUNGARIAN("hu"),
        ICELANDIC("is"),
        ITALIAN("it-IT"),
        INDONESIAN("id"),
        JAPANESE("ja"),
        KOREAN("ko"),
        LATIN("la"),
        CHINESE_SIMPLIFIED("zh-CN"),
        CHINESE_TRANDITIONAL("zh-TW"),
        CHINESE_HONGKONG("zh-HK"),
        CHINESE_CANTONESE("zh-yue"),
        MALAYSIAN("ms-MY"),
        NORWEGIAN("no-NO"),
        POLISH("pl"),
        PIG_LATIN("xx-piglatin"),
        PORTUGUESE("pt-PT"),
        PORTUGUESE_BRASIL("pt-BR"),
        ROMANIAN("ro-RO"),
        RUSSIAN("ru"),
        SERBIAN("sr-SP"),
        SLOVAK("sk"),
        SPANISH_ARGENTINA("es-AR"),
        SPANISH_BOLIVIA("es-BO"),
        SPANISH_CHILE("es-CL"),
        SPANISH_COLOMBIA("es-CO"),
        SPANISH_COSTA_RICA("es-CR"),
        SPANISH_DOMINICAN_REPUBLIC("es-DO"),
        SPANISH_ECUADOR("es-EC"),
        SPANISH_EL_SALVADOR("es-SV"),
        SPANISH_GUATEMALA("es-GT"),
        SPANISH_HONDURAS("es-HN"),
        SPANISH_MEXICO("es-MX"),
        SPANISH_NICARAGUA("es-NI"),
        SPANISH_PANAMA("es-PA"),
        SPANISH_PARAGUAY("es-PY"),
        SPANISH_PERU("es-PE"),
        SPANISH_PUERTO_RICO("es-PR"),
        SPANISH_SPAIN("es-ES"),
        SPANISH_US("es-US"),
        SPANISH_URUGUAY("es-UY"),
        SPANISH_VENEZUELA("es-VE"),
        SWEDISH("sv-SE"),
        TURKISH("tr"),
        ZULU("zu");

        //TODO Clean Up JavaDoc for Overloaded Methods using @link

        /**
         *Stores the LanguageCode
         */
        private final String languageCode;

        /**
         *Constructor
         */
        private Languages(final String languageCode){
            this.languageCode = languageCode;
        }

        public String toString(){
            return languageCode;
        }
    }
    
    /**
     * Language: Contains all supported languages for Google Speech to Text. 
     * Setting this to null will make Google use it's own language detection.
     * This value is null by default.
     * @param language
     */
    public void setLanguage(Languages language) {
        this.language = language.languageCode;
    }
    
    /**Language code.  This language code must match the language of the speech to be recognized. ex. en-US ru-RU
     * This value is null by default.
     * @param language The language code.
     */
     @Deprecated
    public void setLanguage(String language) {
    	this.language = language;
    }
    
    /**
     * Returns the state of profanityFilter
     * which enables/disables Google's profanity filter (on by default).
     * @return profanityFilter
     */
    public boolean getProfanityFilter(){
    	return profanityFilter;
    }
    
    /**
     * Language code.  This language code must match the language of the speech to be recognized. ex. en-US ru-RU
     * This value is null by default.
     * @return language the Google language
     */
    public String getLanguage(){
    	return language;
    }

    public String getApiKey() {
        return apikey;
    }

    public void setApiKey(String apikey) {
        this.apikey = apikey;
    }

    /**
     * Get recognized data from a FLAC file.
     *
     * @param flacFile FLAC file to recognize
     * @param maxResults the maximum number of results to return in the response
     * @param sampleRate The sampleRate of the file. Default is 8000.
     * @return GoogleResponse with the response and confidence score
     * @throws IOException if something goes wrong
     */
    public GoogleResponse getRecognizedDataForFlac(File flacFile, int maxResults, int sampleRate) throws IOException{
        String [] response = rawRequest(flacFile, maxResults, sampleRate);
        //GoogleResponse googleResponse = new GoogleResponse();
        GoogleResponse googleResponse = null;
        
        try {
            googleResponse = parseResponse(response);
        } catch (JSONException ex) {
            Logger.getLogger(GoogleAudioRecognizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return googleResponse;
    }

    /**
     * Performs the request to Google with a file <br>
     * Request is buffered
     *
     * @param inputFile Input files to recognize
     * @return Returns the raw, unparsed response from Google
     * @throws IOException Throws exception if something went wrong
     */
    private String[] rawRequest(File inputFile, int maxResults, int sampleRate) throws IOException {

        StringBuilder sb = new StringBuilder(RECOGNIZER_URL);

        if(apikey != null) {
            sb.append("&auth_token=");
            sb.append(apikey);
        }

        // URL of Remote Script.
        URL url = new URL(sb.toString());
        
        System.out.println("Recognizer.rawRequest(): url=" + url);

        // Open New URL connection channel.
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        //urlConn.setRequestProperty("Content-Type", "multipart/form-data; model=en-US");
        urlConn.setRequestMethod("POST");

        // Send POST output.
        OutputStream outputStream = urlConn.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(inputFile);

        int read = 0;
        byte[] buffer = new byte[256];

        while((read = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        fileInputStream.close();
        outputStream.close();
        
        System.out.println("WRITEN");
        
        //----------------------------------

        // Get response data.
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), Charset.forName("UTF-8")));

        List<String> completeResponse = new ArrayList<>();
        String response = br.readLine();
        System.out.println(response);
        
        while(response != null) {
            completeResponse.add(response);
            response = br.readLine();
            System.out.println(response);
        }

        br.close();
        
        System.out.println("READ");

        // System.out.println("Recognizer.rawRequest() -> completeResponse = " + completeResponse);
        return completeResponse.toArray(new String[completeResponse.size()]);
    }
    
    /**
     * Parses the raw response from Google
     *
     * @param rawResponse The raw, unparsed response from Google
     * @return Returns the parsed response in the form of a Google Response.
     */
    private GoogleResponse parseResponse(String[] rawResponse) throws JSONException {
        GoogleResponse googleResponse = new GoogleResponse();
        
        for(String s : rawResponse) {
            JSONObject jsonResponse = new JSONObject(s);
            JSONArray jsonResultArray = jsonResponse.getJSONArray("result");
            
            for(int i = 0; i < jsonResultArray.length(); i++) {
                JSONObject jsonAlternativeObject = jsonResultArray.getJSONObject(i);
                JSONArray jsonAlternativeArray = jsonAlternativeObject.getJSONArray("alternative");
                double prevConfidence = 0;
                
                for(int j = 0; j < jsonAlternativeArray.length(); j++) {
                    JSONObject jsonTranscriptObject = jsonAlternativeArray.getJSONObject(j);
                    String transcript = jsonTranscriptObject.optString("transcript", "");
                    double confidence = jsonTranscriptObject.optDouble("confidence", 0.0);
                    
                    if(confidence > prevConfidence) {
                        googleResponse.setResponse(transcript);
                        googleResponse.setConfidence(String.valueOf(confidence));
                        prevConfidence = confidence;
                    } else
                        googleResponse.getOtherPossibleResponses().add(transcript);
                }
            }
        }
        
        return googleResponse;
    }
}