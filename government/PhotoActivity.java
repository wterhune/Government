package com.wisaterhunep.government;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;


/*This class is an activity that will show the expanded image and information of the
government official
 */
public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PHOTO ACTIVITY";

    String politicalParty;
    TextView location;
    TextView role;
    TextView name;
    ImageView photo;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        location = findViewById(R.id.locationPhotoActivity);
        role = findViewById(R.id.governmentOfficialTitle);
        name = findViewById(R.id.OfficerName);
        photo = findViewById(R.id.photoOutputActivity);
        logo = findViewById(R.id.logoPhotoView);

        Intent intent = this.getIntent();
        String location = intent.getStringExtra("location"); //find the location value
        this.location.setText(location); //display the location on top of the photo

        role.setText(intent.getStringExtra("role")); //find the role value
        name.setText(intent.getStringExtra("name")); //find the name value
        politicalParty = intent.getStringExtra("party"); //find the political party value

        //we will check for computer network before expanding the image
        if (hasNetwork() == true) {
            setPhoto(intent);
        } else {
            photo.setImageResource(R.drawable.brokenimage);
        }
        setBackgroundWithLogo();
    }

    //This method will check for the Internet network
    private boolean hasNetwork() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //This method will set the background color and logo image according to the party affiliation
    private void setBackgroundWithLogo() {
        Log.d(TAG, "setBackgroundWithLogo: ");

        //if the political party is Rep, then the background will be red with logo
        if (politicalParty.equalsIgnoreCase("Republican")) {
            logo.setImageResource(R.drawable.rep_logo);
            getWindow().getDecorView().setBackgroundColor(Color.RED);

            //if political party is Dem, then the background will be blue with logo
        } else if (politicalParty.equalsIgnoreCase("Democrat")) {
            logo.setImageResource(R.drawable.dem_logo);
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        } else {
            //if the person is not dem or rep, then show no logo and black background
            logo.setVisibility(View.GONE);
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }
    }

    public void logoClicked(View v) {
        Log.d(TAG, "LogoClicked: redirect to party website");

        Intent intent = new Intent(Intent.ACTION_VIEW); //create an intent for Internet web redirect
        if (politicalParty.contains("Republican")) { //if the political party is Republican, then go to the gop page
            intent.setData(Uri.parse("https://www.gop.com"));

        } else if (politicalParty.contains("Democrat")) { //if the political party is Democrat, then go to the democrats page
            intent.setData(Uri.parse("https://democrats.org"));
            
        } else {
            Toast.makeText(this, "No party website provided", Toast.LENGTH_LONG).show();
        }

        startActivity(intent); //redirect to website
    }

    //This method will call Picasso to get an image of the official
    private void setPhoto(Intent intent) {
        Log.d(TAG, "setPhoto: set phot from URL");

        final String urlPhoto = intent.getStringExtra("urlPhoto");
        Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                final String changedUrl = urlPhoto.replace("http:", "https:");
                picasso.load(changedUrl)
                        .error(R.drawable.brokenimage) //if there is an error on Picasso, then display the broken image
                        .placeholder(R.drawable.placeholder) //if we are still loading the picture from Picasso, display the placeholder image
                        .into(photo); //if we have received 200 for GET Picasso image, then display the photo
            }
        }).build();
        picasso.load(urlPhoto)
                .error(R.drawable.brokenimage) //if there is an error on Picasso, then display the broken image
                .placeholder(R.drawable.placeholder) //if we are still loading the picture from Picasso, display the placeholder image
                .into(photo); //if we have received 200 for GET Picasso image, then display the photo
    }
}
