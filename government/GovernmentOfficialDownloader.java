package com.wisaterhunep.government;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/*
This class is a thread called by the MainActivity class. It will do an HTTP GET call to a Google URL.
I have signed up and gotten an API Key listed below. The thread will initially take the user's requested
location from the alert dialog and attach the location to the end of the concatenated URL.
If we get back an HTTP Response of 200, we will parse the incoming JSON object fields
 */
public class GovernmentOfficialDownloader implements Runnable {

    private static final String TAG = "GOVERNMENT OFFICIAL DOWNLOADER";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private String location;
    private String city;
    private String state;
    private String zipCode;
    private static final String GOOGLE_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private static final String API_KEY = "AIzaSyD6MlE8ZTxp6D8513LpUobnsh2J7ZirKq4";

    public GovernmentOfficialDownloader(MainActivity mainActivity, String location) {
        this.mainActivity = mainActivity;
        this.location = location;
    }

    @Override
    public void run() {
        //full link should be: https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyD6MlE8ZTxp6D8513LpUobnsh2J7ZirKq4&address=60601
        String dataURL = GOOGLE_URL + API_KEY + "&address="+location;
        Uri.Builder uriBuilder =
                Uri.parse(dataURL).buildUpon();
        String useThisURL = uriBuilder.toString();

        Log.d(TAG, "data loading: " + useThisURL);
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(useThisURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Log.d(TAG, "run: connection response code" + connection.getResponseCode());
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + connection.getResponseCode());
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.showDownloaderError(); //call showDownloaderError method because HTTP status is 4xx
                    }
                });
                return;
            }

            InputStream input = connection.getInputStream(); //Assuming GET request gives HTTP status of 200
            BufferedReader reader = new BufferedReader((new InputStreamReader(input)));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n'); //Reading in the response fields from the GET request
            }
            Log.d(TAG, "Information received from URL: " + stringBuilder.toString()); //for debugging purposes

        } catch (Exception error) {
            Log.d(TAG, "Unable to retrieve information from URL: ", error);
            error.printStackTrace();
            error.getCause();
            error.getMessage();
            return;
        }

        parseJsonInformation(stringBuilder.toString()); //call this method to parse the incoming JSON fields
    }

    private void parseJsonInformation(String s){
        Log.d( TAG, "parseJsonInformation: get data of government officials");
        ArrayList<GovernmentOfficial> jsonList = new ArrayList<>();
        ArrayList<Object> parsedList = new ArrayList<>(); //contains list of all the parsed fields we need

        try{
            //We get the normalizedInput location information with city, state, and zip details
            JSONObject jsonData = new JSONObject(s);
            JSONObject locationObject = jsonData.getJSONObject("normalizedInput");
            city = locationObject.getString("city");
            state = locationObject.getString("state");
            zipCode = locationObject.getString("zip");
            String location = String.format("%s, %s %s",city,state,zipCode);
            parsedList.add(0,location);

            //Parsing the office titles and its indices
            JSONArray offices = jsonData.getJSONArray("offices");
            for(int x = 0;x < offices.length(); x++){
                JSONObject officeObject = offices.getJSONObject(x);
                String title = officeObject.getString("name"); //ie President of US
                if (title == null) {
                    title = "No title";
                }
                String officialIndices = officeObject.getString("officialIndices");
                String[] index = officialIndices.substring(1, officialIndices.length()-1).split(","); //some object has more than 1 index
                int[] indexArray = new int[index.length];
                int counter = 0;
                while(counter < index.length){
                    indexArray[counter] = Integer.parseInt(index[counter]);
                    counter ++;
                }

                //Parsing the official names and profile details
                JSONArray officials = jsonData.getJSONArray("officials");
                for(int i = 0; i < index.length; i++ ){
                    JSONObject jsonObject = officials.getJSONObject(indexArray[i]); //0 will get President
                    String name = jsonObject.getString("name");
                    if (name == null) name = "No name information";
                    String address = ""; //address should be concatenated into big long string
                    String party = ""; //political party
                    String phone = ""; //officer's phone number
                    String url = ""; //officer's website
                    String email = ""; //contact email
                    String photo = ""; //url of photo
                    String facebook = ""; //url of facebook account
                    String twitter = ""; //url of twitter
                    String youtube = ""; //url of youtube

                    //Parse address of a government official...we keep concatenate into paragraph format
                    if(jsonObject.has("address")){
                        JSONObject jsonAddress = jsonObject.getJSONArray("address").getJSONObject(0); //get the first address
                        if (jsonAddress.has("line1")) {
                            address = address.concat(jsonAddress.getString("line1")) +"\n";
                        }
                        if (jsonAddress.has("line2")) {
                            address = address.concat(jsonAddress.getString("line2")) + "\n";
                        }
                        if (jsonAddress.has("line3") && !jsonAddress.getString("line3").equals("")) {
                            address = address.concat(jsonAddress.getString("line3")) + "\n";
                        }
                        if (jsonAddress.has("city")) {
                            address = address.concat(jsonAddress.getString("city")) + " ";
                        }
                        if (jsonAddress.has("state")) {
                            address = address.concat(jsonAddress.getString("state")) + ", ";
                        }
                        if (jsonAddress.has("zip")) {
                            address = address.concat(jsonAddress.getString("zip"));
                        }
                    }
                    else {
                        address = "No address found";
                    }

                    //Parsing government official's party affiliation
                    if (jsonObject.has("party")) {
                        party = jsonObject.getString("party");
                    } else party = "Unknown"; //set party to unknown where there is no party

                    //Parsing officer's contact info object
                    if (jsonObject.has("phones")) {
                        JSONArray phoneJson = jsonObject.getJSONArray("phones");
                        phone = phoneJson.get(0).toString();
                    } else phone = "No phone provided";

                    //Getting website
                    if (jsonObject.has("urls")) {
                        JSONArray URLJson = jsonObject.getJSONArray("urls");
                        url = URLJson.get(0).toString();
                    } else url = "No website provided";

                    //Parsing email information
                    if (jsonObject.has("emails")) {
                        JSONArray emailJson = jsonObject.getJSONArray("emails");
                        email = emailJson.get(0).toString();
                    } else email = "No email provided";

                    //Photo Url
                    if (jsonObject.has("photoUrl")) {
                        photo = jsonObject.getString("photoUrl");
                    } else photo = "No photo provided"; //TODO put a place holder photo

                    //Parsing social media channels
                    if (jsonObject.has("channels")){
                        JSONArray channels = jsonObject.getJSONArray("channels");
                        int site = 0;
                        while (site < channels.length()){
                            String socialMedia = channels.getJSONObject(site).getString("type");

                            if (socialMedia.equals("Facebook")) {
                                facebook = channels.getJSONObject(site).getString("id");
                            }
                            if (socialMedia.equals("Twitter")) {
                                twitter = channels.getJSONObject(site).getString("id");
                            }
                            if (socialMedia.equals("YouTube")) {
                                youtube = channels.getJSONObject(site).getString("id");
                            }
                            site ++;
                        }
                    }
                    else {
                        facebook = "No Facebook Account Found";
                        twitter = "No Twitter Account Found";
                        youtube = "No Youtube Account Found";
                    }

                    GovernmentOfficial myGovernmentOfficial = new GovernmentOfficial(title, name, party, address, phone, url, email, photo, facebook, twitter, youtube);
                    jsonList.add(myGovernmentOfficial);
                }
            }
            parsedList.add(1, jsonList); //parsedDataList.add(0,location) has location information

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.updateData(parsedList);
                } //sends the updated parsed list to the main activity class
            });

        } catch (JSONException e) {
            Log.d(TAG, "parseJsonInformation: Unable to parse GOOGLE URL");
            e.printStackTrace(); 
        }
    }
}
