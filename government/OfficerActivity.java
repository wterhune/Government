package com.wisaterhunep.government;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;


public class OfficerActivity extends AppCompatActivity {

    TextView location;
    TextView name;
    TextView title;
    TextView politicalParty;

    TextView addressHeader;
    TextView phoneNumberHeader;
    TextView emailAddressHeader;
    TextView websiteHeader;

    TextView addressField;
    TextView phoneNumberField;
    TextView emailAddressField;
    TextView websiteField;

    ImageView photo;
    ImageView YouTube;
    ImageView Twitter;
    ImageView Facebook;
    ImageView logo;

    GovernmentOfficial governmentOfficial;

    private static final String TAG = "OFFICER ACTIVITY";
    private static final String DEMOCRATIC_PARTY = "Democratic Party";
    private static final String REPUBLICAN_PARTY = "Republican Party";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer);

        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        location = findViewById(R.id.locationPhotoActivity);
        name = findViewById(R.id.OfficerName);
        title = findViewById(R.id.governmentOfficialTitle);
        politicalParty = findViewById(R.id.party);

        addressHeader = findViewById(R.id.putAddress);
        addressHeader.setText("Address: ");
        addressField = findViewById(R.id.address);

        phoneNumberHeader = findViewById(R.id.putPhoneNumber);
        phoneNumberHeader.setText(("Phone: "));
        phoneNumberField = findViewById(R.id.phoneNumber);

        emailAddressHeader = findViewById(R.id.putEmailAddress);
        emailAddressHeader.setText("Email: ");
        emailAddressField = findViewById(R.id.emailAddress);

        websiteHeader = findViewById(R.id.putWebsite);
        websiteHeader.setText("Website: ");
        websiteField = findViewById(R.id.website);

        photo = findViewById(R.id.photoOutputActivity);
        logo = findViewById( R.id.partyLogo);
        Facebook = findViewById(R.id.facebook);
        Twitter = findViewById(R.id.twitter);
        YouTube = findViewById(R.id.youtube);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        governmentOfficial = (GovernmentOfficial) bundle.getSerializable("officer");
        location.setText(intent.getStringExtra("location"));

        Log.d(TAG, "setOfficerHeader: setting headers");
        if (governmentOfficial.getPoliticalParty().equals("Unknown")) {
            politicalParty.setVisibility(View.GONE);
        } else {
            politicalParty.setText(String.format("(%s)", governmentOfficial.getPoliticalParty()));
            setBackground();
        }
        if (governmentOfficial.getRole().contains("No title")) {
            title.setVisibility(View.GONE);
        } else { title.setText(governmentOfficial.getRole());}

        if (governmentOfficial.getName().contains("No name"))  {
            name.setVisibility(View.GONE);
        } else { name.setText(governmentOfficial.getName()); }

        if(doNetCheck() == true) {
            setPhoto();
        } else {
            Toast.makeText(OfficerActivity.this, "No Network Connection", Toast.LENGTH_LONG).show();
            photo.setImageResource(R.drawable.brokenimage);
        }

        setGovernmentOfficialInfo();

        Log.d(TAG, "setSocialMediaAccounts: set social media information");
        Twitter.setImageResource(R.drawable.twitter);
        YouTube.setImageResource(R.drawable.youtube);
        Facebook.setImageResource(R.drawable.facebook);

        if(governmentOfficial.getTwitter().contains("No Twitter Account Found") || governmentOfficial.getTwitter().equals(""))
            Twitter.setVisibility(View.GONE);

        if(governmentOfficial.getYoutube().contains("No Youtube Account Found") || governmentOfficial.getYoutube().equals(""))
            YouTube.setVisibility(View.GONE);

        if(governmentOfficial.getFacebook().contains("No Facebook Account Found") || governmentOfficial.getFacebook().equals(""))
            Facebook.setVisibility(View.GONE);
        addLinks();
    }

    //Will look through each field of governmentofficial and present each field on the screen
    private void setGovernmentOfficialInfo(){
        Log.d(TAG, "setOfficerInfo: setting officer information");

        if (!governmentOfficial.getAddress().contains("No address")) {
            addressField.setText(governmentOfficial.getAddress());
            addressField.setTextColor(Color.WHITE);
            addressHeader.setTextColor(Color.WHITE);
        } else {
            addressField.setVisibility(View.GONE);
            addressHeader.setVisibility(View.GONE);
        }

        if (!governmentOfficial.getPhone().contains("No phone")){
            phoneNumberField.setText(governmentOfficial.getPhone());
            phoneNumberField.setTextColor(Color.WHITE);
            phoneNumberHeader.setTextColor(Color.WHITE);
        } else {
            phoneNumberField.setVisibility(View.GONE);
            phoneNumberHeader.setVisibility(View.GONE);
        }

        if (!governmentOfficial.getEmail().contains("No email")) {
            emailAddressField.setText(governmentOfficial.getEmail());
            emailAddressField.setTextColor(Color.WHITE);
            emailAddressHeader.setTextColor(Color.WHITE);
        } else{
            emailAddressField.setVisibility(View.GONE);
            emailAddressHeader.setVisibility(View.GONE);
        }

        if (!governmentOfficial.getWebsiteURL().contains("No website")) {
            websiteField.setText(governmentOfficial.getWebsiteURL());
            websiteField.setTextColor(Color.WHITE);
            websiteHeader.setTextColor(Color.WHITE);
        } else{
            websiteField.setVisibility(View.GONE);
            websiteHeader.setVisibility(View.GONE);
        }
    }

    //do a network check
    private boolean doNetCheck() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else { return false; }
    }
    //this method will call picasso and set the picture
    private void setPhoto(){
        Log.d(TAG, "setPhoto: set photo from picasso");
        photo.setImageResource(R.drawable.placeholder);
        if (governmentOfficial.getPhoto().equals("No photoUrl provided") || governmentOfficial.getPhoto().equals("") )
            photo.setImageResource(R.drawable.missing);
        else {
            final String photoURL = governmentOfficial.getPhoto();
            Picasso picasso = new Picasso.Builder(this).listener( new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String urlToUse = photoURL.replace("http:", "https:");
                    picasso.load(urlToUse)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(photo);
                }
            }).build();

            picasso.setLoggingEnabled(true);
            picasso.load(photoURL) //should be .jpg
                    .error(R.drawable.brokenimage) //show broken image if unable to load Picasso image
                    .placeholder(R.drawable.placeholder) //show placeholder image if loading image
                    .into(photo); //else if 200 status, show the image
        }
    }

    //This will set the log and background color according to the party's affiliation
    private void setBackground(){
        Log.d(TAG, "setPartyLogoBackground: to set party logo and background");
        if(governmentOfficial.getPoliticalParty().equals("Republican") || governmentOfficial.getPoliticalParty().equals(REPUBLICAN_PARTY)){
            logo.setImageResource(R.drawable.rep_logo);
            getWindow().getDecorView().setBackgroundColor(Color.RED);
        }
        else if(governmentOfficial.getPoliticalParty().contains("Democrat") || governmentOfficial.getPoliticalParty().equals(DEMOCRATIC_PARTY)){
            logo.setImageResource(R.drawable.dem_logo);
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        }
        else {
            logo.setVisibility(View.GONE);
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }
    }

    //This will set the links for address, phone, email, and website
    private void addLinks(){
        Linkify.addLinks(addressField, Linkify.ALL);
        Linkify.addLinks(phoneNumberField, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(emailAddressField, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(websiteField, Linkify.WEB_URLS);
    }

    //This will go to the main party page using ACTION_VIEW intent
    public void logoClicked(View v){
        Log.d(TAG, "logoClicked: link to party's website");
        String democraticURL = "https://democrats.org";
        String republicanURL = "https://www.gop.com";
        String party = governmentOfficial.getPoliticalParty();
        Intent intent = new Intent(Intent.ACTION_VIEW); //ACTION_VIEW to click on a link and be redirected to a website

        if (party.contains("Democrat"))
            intent.setData(Uri.parse(democraticURL));
        else if (party.contains("Republican"))
            intent.setData(Uri.parse(republicanURL));
        startActivity(intent);
    }

    //When facebook icon is clicked, will redirect to account
    public void facebookClicked(View v){
        Log.d(TAG, "facebookClicked: link to Facebook");

        String facebookURL = "https://www.facebook.com/" + governmentOfficial.getFacebook();
        String url;
        PackageManager packageManager = getPackageManager();
        try {
            //if package is present, return a version code
            int version = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (version >= 3002850) { //if newer version of Facebook
                url = "fb://facewebmodal/f?href=" + facebookURL;
            } else { //use the older version of Facebook
                url = "fb://page/" + governmentOfficial.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            url = facebookURL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(url));
        startActivity(facebookIntent);
    }

    //When twitter icon is clicked, will redirect to account
    public void twitterClicked(View v){
        Log.d(TAG, "twitterClicked: to link to twitter");

        Intent intent;
        String twitterID = governmentOfficial.getTwitter();
        try {
            getPackageManager().getPackageInfo("com.twitter.android",0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitterID));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }catch (Exception e){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/" + twitterID));
        }
        startActivity(intent);
    }

    //When youtube icon is clicked, will redirect to account
    public void youtubeClicked(View v){
        Log.d(TAG, "youtubeClicked: redirecting to Youtube");
        String name = governmentOfficial.getYoutube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    //If the image gets clicked, will go to the photoactivity class and expands picture
    public void photoActivityLink(View v){
        Log.d(TAG, "photoActivityLink: to direct to PhotoActivity ");
        if(governmentOfficial.getPhoto().equals("") || governmentOfficial.getPhoto().contains("No photo")){
            return;
        }
        else {
            Intent intent = new Intent(OfficerActivity.this, PhotoActivity.class);
            intent.putExtra("location", location.getText().toString());
            intent.putExtra("name", governmentOfficial.getName());
            intent.putExtra("role", governmentOfficial.getRole());
            intent.putExtra("urlPhoto", governmentOfficial.getPhoto());

            if (governmentOfficial.getPoliticalParty().contains("Democrat"))
                intent.putExtra("party", "Democrat");
            else if (governmentOfficial.getPoliticalParty().contains("Republican"))
                intent.putExtra("party", "Republican");
            else {
                intent.putExtra("party", "Unknown");
            }
            startActivity(intent);
        }
    }
}
