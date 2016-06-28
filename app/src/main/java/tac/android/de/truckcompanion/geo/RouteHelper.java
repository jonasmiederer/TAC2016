package tac.android.de.truckcompanion.geo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitSettings;
import com.skobbler.ngx.SKPrepareMapTextureListener;
import com.skobbler.ngx.SKPrepareMapTextureThread;
import com.skobbler.ngx.map.SKMapViewStyle;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.versioning.SKMapUpdateListener;
import com.skobbler.ngx.versioning.SKVersioningManager;
import tac.android.de.truckcompanion.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jonas Miederer.
 * Date: 28.06.2016
 * Time: 17:18
 * Project: MAD
 * We're even wrong about which mistakes we're making. // Carl Winfield
 */
public class RouteHelper implements SKPrepareMapTextureListener, SKMapUpdateListener {

    public static final String TAG = "TAC";
    private static RouteHelper instance;
    private Context context;

    // Skobbler-related stuff
    private String mapResourcesDirPath;

    private static final String SKMAPS_DIR = "/SKMaps/";

    private RouteHelper(Context context) {
        this.context = context;
    }

    public static RouteHelper getInstance(Context context) {
        if (instance == null) {
            return new RouteHelper(context);
        }
        return instance;
    }


    public void init() {
        File externalDir = context.getExternalFilesDir(null);

        // determine path where map resources should be copied on the device
        mapResourcesDirPath = (externalDir != null ? externalDir : context.getFilesDir()) + SKMAPS_DIR;
        final SKPrepareMapTextureThread prepThread = new SKPrepareMapTextureThread(context, mapResourcesDirPath, "SKMaps.zip", this);
        prepThread.start();

//        if (!new File(mapResourcesDirPath).exists()) {
//            // if map resources are not already present copy them to
//            // mapResourcesDirPath in the following thread
//            new SKPrepareMapTextureThread(context, mapResourcesDirPath, "SKMaps.zip", this).start();
//            // copy some other resource needed
//            copyOtherResources();
//            prepareMapCreatorFile();
//            copyMarkImage(this);
//        } else {
//            // map resources have already been copied - start the map activity
//            prepareMapCreatorFile();
//            copyMarkImage();
//            initializeLibrary();
//            Executors.newScheduledThreadPool(1).schedule(new Runnable() {
//                @Override
//                public void run() {
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            finish();
//                            startActivity(new Intent(SplashActivity.this, MapActivity.class));
//                        }
//                    });
//                }
//            }, 1, TimeUnit.SECONDS);
//        }
    }

//    /**
//     * Copies the map creator file from assets to a storage.
//     */
//    private void prepareMapCreatorFile() {
//
//        final Thread prepareGPXFileThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
//
//                    final String mapCreatorFolderPath = mapResourcesDirPath + "MapCreator";
//                    final File mapCreatorFolder = new File(mapCreatorFolderPath);
//                    // create the folder where you want to copy the json file
//                    if (!mapCreatorFolder.exists()) {
//                        boolean mkdirsOk = mapCreatorFolder.mkdirs();
//                        if (!mkdirsOk)
//                            Log.d(TAG, "Error creating mapCreator folder");
//                    }
//                    setMapCreatorFilePath(mapCreatorFolderPath + "/mapcreatorFile.json");
//                    DemoUtils.copyAsset(getAssets(), "MapCreator", mapCreatorFolderPath, "mapcreatorFile.json");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        prepareGPXFileThread.start();
//    }
//
//    /**
//     * Copy some additional resources from assets
//     */
//    private void copyOtherResources() {
//        new Thread() {
//            public void run() {
//                try {
//                    String tracksPath = mapResourcesDirPath + "GPXTracks";
//                    File tracksDir = new File(tracksPath);
//                    if (!tracksDir.exists()) {
//                        boolean mkdirsOk = tracksDir.mkdirs();
//                        if (!mkdirsOk)
//                            Log.d(TAG, "Error making dirs");
//                    }
//                    DemoUtils.copyAssetsToFolder(getAssets(), "GPXTracks", mapResourcesDirPath + "GPXTracks");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
//
//    /**
//     * This method restores the image used for marking route from app resources when it is
//     * unavailable due to former extraction of SKMap resources by an app with the same package name
//     */
//    public void copyMarkImage() {
//        File markImage = new File(mapResourcesDirPath + MARK_PNG);
//
//        if (!markImage.exists()) {
//            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
//            bm = Bitmap.createScaledBitmap(bm, MARK_PNG_SIZE, MARK_PNG_SIZE, false);
//            try {
//                FileOutputStream outStream = new FileOutputStream(markImage);
//                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//                outStream.flush();
//                outStream.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * Initializes the SKMaps framework
     */
    private void initializeLibrary() {
        // get object holding map initialization settings
        SKMapsInitSettings initMapSettings = new SKMapsInitSettings();
        // set path to map resources and initial map style
        initMapSettings.setMapResourcesPaths(mapResourcesDirPath,
                new SKMapViewStyle(mapResourcesDirPath + "daystyle/", "daystyle.json"));

        final SKAdvisorSettings advisorSettings = initMapSettings.getAdvisorSettings();
        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.forString("de"));
        advisorSettings.setAdvisorVoice("de");
//        advisorSettings.setPlayInitialAdvice(true);
//        advisorSettings.setPlayAfterTurnInformalAdvice(true);
//        advisorSettings.setPlayInitialVoiceNoRouteAdvice(true);
        initMapSettings.setAdvisorSettings(advisorSettings);

        // EXAMPLE OF ADDING PREINSTALLED MAPS
        // initMapSettings.setPreinstalledMapsPath(app.getMapResourcesDirPath()
        // + "/PreinstalledMaps");
        // initMapSettings.setConnectivityMode(SKMaps.CONNECTIVITY_MODE_OFFLINE);

        // Example of setting light maps
        // initMapSettings.setMapDetailLevel(SKMapsInitSettings.SK_MAP_DETAIL_LIGHT);
        // initialize map using the settings object
        SKVersioningManager.getInstance().setMapUpdateListener(this);
        SKMaps.getInstance().initializeSKMaps(context, initMapSettings);
    }

    @Override
    public void onNewVersionDetected(int i) {

    }

    @Override
    public void onMapVersionSet(int i) {

    }

    @Override
    public void onVersionFileDownloadTimeout() {

    }

    @Override
    public void onNoNewVersionDetected() {

    }

    @Override
    public void onMapTexturesPrepared(boolean b) {
        initializeLibrary();
    }
}
