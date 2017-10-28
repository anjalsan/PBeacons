package poleb.awesome.login.litrallybeacon;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import java.util.Timer;

import static poleb.awesome.login.litrallybeacon.MyApplication.proximityManager;


public class MainActivity extends AppCompatActivity {

    private static String TAG="Pole";
    public static final int REQUEST_CODE_PERMISSION = 1;
    private Context mContext;
    private Timer T=new Timer();
    private int count = 0;
    private TextView textView1, textView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);

        onCreateBeacons();

//        MyApplication.onCreateBeacons();
    }


    public void onCreateBeacons(){
        KontaktSDK.initialize("SqsMXrtfSNncgUTHgiAzRSbLlnNruZxN");

        proximityManager = ProximityManagerFactory.create(mContext);
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setEddystoneListener(createEddystoneListener());
    }

    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                createNotification(true, ibeacon.getUniqueId());
            }

            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
                createNotification(false, iBeacon.getUniqueId());
            }
        };
    }

    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                createNotification(true, eddystone.getUniqueId());
            }

            @Override
            public void onEddystoneLost(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                createNotification(false, eddystone.getUniqueId());
            }
        };
    }

    private void createNotification(boolean enter, String venue) {

        if (enter){
            textView1.setText("Entered region: " + venue);
            count = 0;

//            T.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    runOnUiThread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            textView2.setText("count="+count);
//                            count++;
//                        }
//                    });
//                }
//            }, 1000, 1000);
        } else {
            textView1.setText("Last Entered region: " + venue);
//            T.cancel();
        }

    }

    public static void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }

    public static void stopScanning() {
        proximityManager.stopScanning();
    }

    public static void destroyScanning() {
        proximityManager.disconnect();
        proximityManager = null;
    }



    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
        startScanning();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScanning();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyScanning();
        Log.d(TAG, "onDestroy called");
    }
}
