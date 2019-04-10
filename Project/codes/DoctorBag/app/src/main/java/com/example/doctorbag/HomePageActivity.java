package com.example.doctorbag;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends AppCompatActivity{

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;

    private TextView NameText, EmailText;
    private CircleImageView ProfileImage;

    String name,email,imageUrl;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;

    private Handler mHandler;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private String current_uid;

    public static int navItemIndex = 0;

    private static final String TAG_HOME = "home";
    private static final String TAG_MEDICINE = "medicine";
    private static final String TAG_NEARBY = "nearby";
    private static final String TAG_DETAILS = "details";
    private static final String TAG_ABOUT = "about";
    private static final String TAG_POLICY = "policy";
    public static String CURRENT_TAG = TAG_HOME;

    private boolean shouldLoadHomeFragOnBackPress = true;

    private FrameLayout frameLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        toolbar = (Toolbar) findViewById(R.id.account_toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        email=mAuth.getCurrentUser().getEmail();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        NameText = (TextView) navHeader.findViewById(R.id.username);
        EmailText = (TextView) navHeader.findViewById(R.id.email);
        ProfileImage = (CircleImageView) navHeader.findViewById(R.id.profile_image);

        frameLayout = (FrameLayout) findViewById(R.id.containerfragment);
        EmailText.setText(email);

        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                name = dataSnapshot.child("name").getValue().toString();
                imageUrl = dataSnapshot.child("image").getValue().toString();

                NameText.setText(name);

                if (!imageUrl.equals("default")) {
                    Picasso.get().load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(ProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(imageUrl).placeholder(R.drawable.profile).into(ProfileImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImage();
            }
        });
    }


    private void updateImage()
    {
        Intent account = new Intent(HomePageActivity.this,UpdateActivity.class);
        account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(account);
    }


    private void loadHomeFragment() {
        selectNavMenu();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                fragmentTransaction.replace(frameLayout.getId(), fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();

            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;

            case 1:
                MedicineFragment medicineFragment = new MedicineFragment();
                return medicineFragment;
            case 2:
                NearbyFragment nearbyFragment = new NearbyFragment();
                return nearbyFragment;
            case 3:
                DetailsFragment detailsFragment  = new DetailsFragment();
                return detailsFragment;
            case 4:
                AboutFragment aboutFragment = new AboutFragment();
                return aboutFragment;
            case 5:
                PolicyFragment policyFragment = new PolicyFragment();
                return policyFragment;
            case 6:


            default:
                return new HomeFragment();
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;

                    case R.id.nav_medicine:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_MEDICINE;
                        break;
                    case R.id.nav_nearby:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_NEARBY;
                        break;

                    case R.id.nav_details:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_DETAILS;
                        break;

                    case R.id.nav_about:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_ABOUT;
                        break;
                    case R.id.nav_policy:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_POLICY;
                        break;

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                setTitle(menuItem.getTitle());

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.opendrawer,R.string.closedrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                super.onConfigurationChanged(newConfig);
                drawerToggle.onConfigurationChanged(newConfig);
            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.account, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
        }
        else if (id == R.id.action_settings)
        {
            Intent settings=new Intent(HomePageActivity.this,UpdateActivity.class);
            startActivity(settings);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout()
    {
        mAuth.signOut();
        updateUI();
    }

    private void updateUI()
    {
        Intent account = new Intent(HomePageActivity.this,LoginActivity.class);
        account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(account);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (shouldLoadHomeFragOnBackPress)
        {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }
}
