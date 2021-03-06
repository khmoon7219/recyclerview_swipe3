package com.khmoon.recyclerview_swipe3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.khmoon.recyclerview_swipe3.DB.AppDatabase;
import com.khmoon.recyclerview_swipe3.DB.DB_Call;
import com.khmoon.recyclerview_swipe3.DB.DB_Device;
import com.khmoon.recyclerview_swipe3.DB.DB_Order;
import com.khmoon.recyclerview_swipe3.DB.DB_checkserver;
import com.khmoon.recyclerview_swipe3.RECYCLERVIEW.AdapterRC;
import com.khmoon.recyclerview_swipe3.RECYCLERVIEW.Data_OrderCall;
import com.khmoon.recyclerview_swipe3.RECYCLERVIEW.Item_order;
import com.khmoon.recyclerview_swipe3.RECYCLERVIEW.OnSwipeTouchListener;
import com.khmoon.recyclerview_swipe3.RETROFIT.ApiService;
import com.khmoon.recyclerview_swipe3.UTILS.NetworkStatus;
import com.mikepenz.itemanimators.SlideInOutBottomAnimator;
import com.mikepenz.itemanimators.SlideInOutLeftAnimator;
import com.mikepenz.itemanimators.SlideInOutRightAnimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    /****** Static ?????? ?????? ******/
    public static final int DIALOG_SINGLE = 1;
    public static final int DIALOG_DOUBLE = 2;
    public static final int CONTENT_ORDER = 1;
    public static final int CONTENT_CALL = 2;

    // ???????????? ?????? ????????? ????????? -- ????????? ??? ????????? ??????..
    private static final int REQUEST_CODE = 1234;
    final int PERMISSION = 1;

    //???????????? ??? ??????
    private SpeechRecognizer speech;
    private Intent recognizerIntent;

    /****** ???????????? ******/
    LinearLayout keypad;
    LinearLayout setup;
    LinearLayout main1;
    LinearLayout main2;
    ConstraintLayout layout_transparent;
    ConstraintLayout layout_networkstatus;
    TextView tv_networkerror;
    ImageButton btn_keypad;
    ImageButton btn_setup;
    Animation translateLeft, translateRight;
    Animation enter_left, exit_left;

    TextView btn_order;
    boolean isPageOpen = false;
    boolean isSetupOpen = false;
    Button btn00, btn01, btn02, btn03, btn04, btn05, btn06, btn07;
    Button btn08, btn09, btn11, btn12, btn13, btn14, btn15;
    TextView tvNUM, tvORDER;
    TextView tvOrderNo, tvCallNo;
    TextView tv_storename, tvDate, tvBattery;
    TextView lb_order, lb_call;
    ImageView imgBattery;


    // Recycler View ?????????
    RecyclerView view_order;
    RecyclerView view_call;
    private ArrayList<Item_order> item_orders = new ArrayList<>();
    private AdapterRC adapterOrder;
    //    private ArrayList<Item_order> item_orders = new ArrayList<>();
    private ArrayList<Item_order> item_calls = new ArrayList<>();
    private AdapterRC adapterCall;
    // ???????????? ?????????
    private int clickCountOrder = 0;
    private int clickCountCall = 0;
    private final long CLICK_DELAY = 300;

//    int isClickMode = 0;
//    boolean isWidthMode = true;

    MyApplication myApp;
    AppDatabase db;

//    List<Data_OrderCall> orderlist = new ArrayList<>();
//    List<Data_OrderCall> calllist = new ArrayList<>();

    Timer networkTimer;

//    Button btn_setdata;
//    Button btn_voice;

    //Setup ?????? ????????? ?????? ??????
    SeekBar seekBar_volume;
    TextView tv_volume;
    EditText ed_serverip;
    EditText ed_storename;
    TextView tv_myip;
    Button btn_manager;
    Button btn_serverip_confirm;
    Button btn_setup_confirm;
    Button btn_setup_cancel;
    boolean isChkServer = false;
    boolean isNetWork = false;
    boolean isVolumeTest = false;
    boolean isWidthMode = true;
    boolean isDeviceDB = false;
    boolean isDialogMsg = false;;
    Button btn_server_change;
    Ringtone rt;

    RadioGroup rg; // ?????????, ?????? ?????? ??????

    // ????????? ????????????
    int iW, iH;
    int dpi;
    float density, xdpi, ydpi;

    // Retrofit ??? ????????? ?????? ??????
    final static String sPath = "/DID/json/";

    // DB ????????? ?????? ??????
    List<DB_Order> db_order = new ArrayList<>();
    List<DB_Call> db_call = new ArrayList<>();
    int dialg_msg_buttonclick = 0; // dialog_msg ??? ?????? ????????? ???????????? ??????.
    boolean isSetupEmpty = true;

//    // ???????????? ?????????
//    Intent speech_intent;
//    SpeechRecognizer mRecognizer;
//    Handler handler;
//    Runnable runn;
//
//    boolean isSpeech = false;

    /******************************************* => ???????????? ????????????  ***/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
/****** "????????????, ?????????(?????????) ????????? ?????????" ******/
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //????????? ?????????
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        hideNavigationBar();
        /*********************--> ???????????? "????????????, ?????????(?????????) ????????? ?????????" ***********/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //???????????? ???????????? ??????
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //????????????
            isWidthMode = true;
            setWidthMod();
            Log.e("___onConfigureChanged", "????????????");
        } else {
            //????????????
            isWidthMode = false;
            setHeightMod();
            Log.e("___onConfigureChanged", "????????????");
        }

        // ?????? ??????????????? ??? ??????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**********************  ?????? ?????? ?????? ***************/

/****** ?????? ?????? ******/
        myApp = (MyApplication) getApplication();
        db = AppDatabase.getInstance(getApplicationContext());

/******  MyApplication ??? ??????  ******/
        String model = Build.MODEL;
        myApp.setModelName(model);
        String sn_android = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String serial = model + "_" + sn_android + "_S";
        myApp.setsSN(serial);
        String ipAddress = getIPAddress();
        myApp.setMyIP(ipAddress);
        TextView myIP = findViewById(R.id.setup_myip);
        myIP.setText(myApp.getMyIP());
        myApp.setSsHallMode("N");
        myApp.setnStore(0);
        myApp.setsStoreName("-");
        myApp.setClickMode(1);

        /******* ????????? ?????? ******/
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        iW = size.x;
        iH = size.y;
        Log.e("___Main_?????????","iWidth(??????) => " + iW +", iHeight(??????) => " + iH );
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
//        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Log.e("___Main_?????????","dpi: " +  metrics.densityDpi);
        Log.e("___Main_?????????","density: " +  metrics.density);
        Log.e("___Main_?????????","xdpi: " +  metrics.xdpi);
        Log.e("___Main_?????????","ydpi: " +  metrics.ydpi);

        myApp.setnWidth(iW);
        myApp.setnHeight(iH);

        /***------------------------------------------------------ => ????????????: ????????? ?????? -----***/

    /****************************** ???????????? : MyApplication ??? ?????? ***/

/********** findViewByID() ??? ?????? **********/

        main1 = findViewById(R.id.layout_main1);
        main2 = findViewById(R.id.layout_main2);

        layout_transparent = findViewById(R.id.layout_transparent);
        layout_networkstatus = findViewById(R.id.layout_networkState);
        tv_networkerror = findViewById(R.id.tv_networkerror);
        tvOrderNo = findViewById(R.id.tv_ordernum);
        tvCallNo = findViewById(R.id.tv_callnum);

        tv_storename = findViewById(R.id.tv_storename);
        tvDate = findViewById(R.id.tv_date);
        tvBattery = findViewById(R.id.tv_battery);
        imgBattery = findViewById(R.id.img_battery_charging);
        lb_order = findViewById(R.id.label_order);
        lb_call = findViewById(R.id.label_call);

        tvNUM = findViewById(R.id.tv_number);
        tvORDER = findViewById(R.id.tv_order);
        view_order = findViewById(R.id.view_order);
        view_call = findViewById(R.id.view_call);
        keypad = findViewById(R.id.keypad);
        setup = findViewById(R.id.setup);
        btn_keypad = findViewById(R.id.btn_keypad);
        btn_setup = findViewById(R.id.btn_setup);

        seekBar_volume = findViewById(R.id.setup_volume);
        tv_volume = findViewById(R.id.setup_volumevalue);
        ed_serverip = findViewById(R.id.setup_serverip);
        tv_myip = findViewById(R.id.setup_myip);
        ed_storename = findViewById(R.id.ed_storename);
        btn_manager = findViewById(R.id.btn_manager);

        btn_setup_confirm = findViewById(R.id.btn_setupok);
        btn_setup_cancel = findViewById(R.id.btn_setupcancle);
        btn_serverip_confirm = findViewById(R.id.setup_serverip_confirm);
        btn_server_change = findViewById(R.id.btn_server_change);
        rg = findViewById(R.id.setup_radiogroup);

        initKeyPad();  // keypad ?????????

        /******************************????????????: findViewByID() ??? ?????? ***/

/******  Keypad Animation ?????? ******/
        // animation ????????? ???????????? ???. Left
        translateLeft = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRight = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        enter_left = AnimationUtils.loadAnimation(this, R.anim.horizon_left_enter);
        exit_left = AnimationUtils.loadAnimation(this, R.anim.horizon_left_exit);

        // ??????????????? ????????? ???????????? ?????? ??????.
        SlidingAnimationListener listener = new SlidingAnimationListener();
        translateRight.setAnimationListener(listener);
        translateLeft.setAnimationListener(listener);

        SlidingSetupListener listenerSetup = new SlidingSetupListener();
        enter_left.setAnimationListener(listenerSetup);
        exit_left.setAnimationListener(listenerSetup);

        layout_transparent.setVisibility(View.VISIBLE);
        Log.i("____layout_transparent", "layout_transparent (onCreat): ??????");


        // button Keypad ??????
        btn_keypad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myApp.getSsHallMode().equals("N")) {
                    tvNUM.setText("");
                    tvORDER.setText("");

                    if (isPageOpen) {
                        keypad.startAnimation(translateRight);
                        layout_transparent.setVisibility(View.GONE);
                    } else {
                        keypad.setVisibility(View.VISIBLE);
                        keypad.startAnimation(translateLeft);
                        layout_transparent.setVisibility(View.VISIBLE);
                        Log.i("____layout_transparent", "layout_transparent (btn_keypad): ??????");
                        //????????? ??????????????? ?????????????????? ?????? ??????????????? ?????? ???????????? ?????? ???????????????
                        // ????????? ?????? ????????? ??????
                    }
                }
            }
        });

        btn_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSetupOpen) {
                    setup.startAnimation(exit_left);
                    setup.setVisibility(View.GONE);
                    layout_transparent.setVisibility(View.INVISIBLE);
                } else {
                    setup.setVisibility(View.VISIBLE);
                    setup.startAnimation(enter_left);
                    layout_transparent.setVisibility(View.VISIBLE);
                    Log.i("____layout_transparent", "layout_transparent (btn_setup): ??????");
                    //????????? ??????????????? ?????????????????? ?????? ??????????????? ?????? ???????????? ?????? ???????????????
                    // ????????? ?????? ????????? ??????
                }

            }
        });



        // Setup ?????? ??????

        btn_setup_confirm.setOnClickListener(setupListener);
        btn_setup_cancel.setOnClickListener(setupListener);

        ed_serverip.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER) {
                    ed_serverip.setNextFocusDownId(R.id.setup_volume);
                }
                return false;
            }
        });

        btn_serverip_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ?????? ?????? ??? ????????? ?????????
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(ed_serverip.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // ?????? ??????
                String svrip = ed_serverip.getText().toString();
                myApp.setServerIP(svrip);
                findViewById(R.id.pgb_circle).setVisibility(View.VISIBLE);
                php_CheckServer(isChkServer, svrip);
                Log.i("___onCreate", "???????????? ??????");

                layout_transparent.setVisibility(View.VISIBLE);
                Log.i("____layout_transparent", "layout_transparent (btn_serverip_confirm): ??????");

            }
        });

        seekBar_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                myApp.setnVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setVolume(myApp.getnVolume());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Handler handler = new Handler();
                Runnable volumeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (isVolumeTest == false) {

                            setVolume(myApp.getnVolume());

                        }
                    }
                };

                tv_volume.setText("??????: " + myApp.getnVolume());
                handler.postDelayed(volumeRunnable, 100);
//                setVolume(progress);
                myApp.setnVolume(myApp.getnVolume());
            }

        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.setup_radio_1) {
                    myApp.setClickMode(1);
                } else if (checkedId == R.id.setup_radio_2) {
                    myApp.setClickMode(2);
                }
            }
        });

        btn_server_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout container = new FrameLayout(MainActivity.this);
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

                final EditText et = new EditText(MainActivity.this);
                et.setLayoutParams(params);
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                et.setTextColor(Color.RED);
//                et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                et.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_CLASS_TEXT);

                et.setSelectAllOnFocus(true);
                et.setFocusableInTouchMode(true);

                container.addView(et);
                final AlertDialog.Builder dlg_BD = new AlertDialog.Builder(MainActivity.this, R.style.InputTableDialogStyle);
                et.setText(myApp.getServerIP());
                dlg_BD.setTitle("???????????? ??????")
                        .setMessage("??????????????? ???????????????.")
                        .setCancelable(false)
                        .setView(container)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s_svr;

                                if (et.getText().toString().equals("") || et.getText().toString() == null) {
                                    if (!isDialogMsg) {
                                        isDialogMsg = true;
                                        dialog_msg(MainActivity.this, DIALOG_SINGLE, "???????????? ?????? ??????", "?????? ????????? ??????????????????.");
                                    }
                                } else {
                                    s_svr = et.getText().toString();

                                    if (s_svr.contains("http") || s_svr.contains("/")) {
                                        if (!isDialogMsg) {
                                            isDialogMsg = true;
                                            dialog_msg(MainActivity.this, DIALOG_SINGLE, "???????????? ?????? ??????", "'http' ?????? '/' ??? ???????????????");
                                        }
                                    } else {
                                        ed_serverip.setText(s_svr);
                                        php_CheckServer(isChkServer, s_svr);
                                    }

                                }

                            }
                        });
                AlertDialog dlg = dlg_BD.create();
                dlg.show();
            }
        });

        btn_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
                intent.putExtra("_URL", "http://" + myApp.getServerIP() + "/DID/");
                startActivityForResult(intent, 1);
            }
        });


    /******************************* => ????????????: Keypad Animation ?????? ******/

//        isWidthMode = true;
//        isClickMode = 2;

        view_order = findViewById(R.id.view_order);
        view_order.setItemAnimator(new SlideInOutRightAnimator(view_order));

        view_call = findViewById(R.id.view_call);
        view_call.setItemAnimator(new SlideInOutBottomAnimator(view_call));

//        btn_setdata = findViewById(R.id.btn_setdata);
//        btn_voice = findViewById(R.id.btn_voice);

//        btn_setdata.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<Data_OrderCall> data_oc = new ArrayList<>();
//                for (int i = 1; i < 15; i++) {
//                    Data_OrderCall doc = new Data_OrderCall(i, 0, i+10);
//                    data_oc.add(doc);
//                    orderlist.add(doc);
//                    inputOrderList(CONTENT_ORDER, adapterOrder, data_oc, i-1);
//
//                }
////                inputOrderList(CONTENT_ORDER, adapterOrder, data_oc, position);
//
//            }
//        });

/****  ???????????? ?????? ?????? ?????? ******/
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

//        btn_voice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR");
//                recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
//                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
//                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
//                startActivityForResult(recognizerIntent, REQUEST_CODE);
//            }
//        });


/****  Order ????????? ?????? ?????? ?????? ******/
        GridLayoutManager grid_order = new GridLayoutManager(this, 3);
        view_order.setLayoutManager(grid_order);
        adapterOrder = new AdapterRC(MainActivity.this, item_orders, CONTENT_CALL);
        view_order.setAdapter(adapterOrder);

        view_order.addOnItemTouchListener(new OnSwipeTouchListener(this, view_order, new OnSwipeTouchListener.OnTouchActionListener() {
            @Override
            public void onLeftSwipe(View view, int position) {
                Toast.makeText(MainActivity.this, "Order Swipe: Left / position- " + position, Toast.LENGTH_LONG).show();
                adapterOrder.OrderRightToLeft(view, position);
                view_order.scrollToPosition(0);
            }

            @Override
            public void onRightSwipe(View view, int position) {
                Toast.makeText(MainActivity.this, "Order Swipe: RIGHT / position- " + position, Toast.LENGTH_LONG).show();
                adapterOrder.OrderLeftToRight(view, position);
                OrderToCall(position);
                view_order.scrollToPosition(0);
            }

            @Override
            public void onClick(View view, int position) {
                if(myApp.getClickMode() == 1) {
                    Toast.makeText(MainActivity.this, "Order Click: position- " + position, Toast.LENGTH_LONG).show();
                    OrderToCall(position);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "Order LongClick: position- " + position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDoubleClick(View view, int position) {
                if(myApp.getClickMode() == 2) {
                    Toast.makeText(MainActivity.this, "Order DoubkeClick: position- " + position, Toast.LENGTH_LONG).show();
                    OrderToCall(position);
                }
            }
        }));

        /******************************************* => ????????????: Order ????????? ?????? ?????? ?????? ******/

/******  Call ????????? ?????? ?????? ?????? ******/
        LinearLayoutManager linear_call = new LinearLayoutManager(this);
        view_call.setLayoutManager(linear_call);
        adapterCall = new AdapterRC(MainActivity.this, item_calls, CONTENT_CALL);
        view_call.setAdapter(adapterCall);

        view_call.addOnItemTouchListener(new OnSwipeTouchListener(this, view_call, new OnSwipeTouchListener.OnTouchActionListener() {
            @Override
            public void onLeftSwipe(View view, int position) {
                Toast.makeText(MainActivity.this, "Call Swipe: Left / position- " + position, Toast.LENGTH_LONG).show();
                adapterOrder.CallRightToLeft(view, position);
                CallToOrder(position);
                view_order.scrollToPosition(0);
            }

            @Override
            public void onRightSwipe(View view, int position) {
                Toast.makeText(MainActivity.this, "Call Swipe: RIGHT / position- " + position, Toast.LENGTH_LONG).show();
                adapterOrder.CallLeftToRight(view, position);
                CallToEnd(position);
                view_order.scrollToPosition(0);
            }

            @Override
            public void onClick(View view, int position) {
                if(myApp.getClickMode() == 1) {
                    Toast.makeText(MainActivity.this, "Call Click: position- " + position, Toast.LENGTH_LONG).show();
                    CallToEnd(position);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "Call LongClick: position- " + position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDoubleClick(View view, int position) {
                if(myApp.getClickMode() == 2) {
                    Toast.makeText(MainActivity.this, "Call DoubkeClick: position- " + position, Toast.LENGTH_LONG).show();
                    CallToEnd(position);
                }
            }
        }));

        /******************************************* => ????????????: Call ????????? ?????? ?????? ?????? ******/

/******* DB_Device ??? ????????? ????????? ?????? : ????????? ????????? ?????? ?????? & ?????? ?????? ????????? ******/
//        db.daoOrder().deleteAll();
//        db.daoCall().deleteAll();

        int device_exist = db.daoDevice().isNotEmpty();

        if (device_exist == 0) {
            Log.e("___DB_device_??? ??????", "rowcount: 0");
            isSetupEmpty = true;
            isDeviceDB = false;

            if(myApp.getnStore() > 0) {
                php_insert_device(
                        myApp.getsSN(), myApp.getModelName() + "_" + myApp.getnStore(),
                        myApp.getServerIP(), myApp.getMyIP(), myApp.getnWidth(),
                        myApp.getnHeight(), 0
                );
            }
            myApp.setSsHallMode("N");
            myApp.setnVolume(0);
            setVolume(0);
            // ????????? ??????
            if (isSetupOpen) {
                setup.startAnimation(exit_left);
                setup.setVisibility(View.GONE);
                layout_transparent.setVisibility(View.INVISIBLE);
            } else {
                setup.setVisibility(View.VISIBLE);
                setup.startAnimation(enter_left);
                layout_transparent.setVisibility(View.VISIBLE);

                Log.i("____layout_transparent", "layout_transparent (device_exist_1): ??????");
                //????????? ??????????????? ?????????????????? ?????? ??????????????? ?????? ???????????? ?????? ???????????????
                // ????????? ?????? ????????? ??????
            }
            Toast.makeText(this, "???????????? ?????????", Toast.LENGTH_LONG).show();

        } else {
            int n_playerno = db.daoDevice().getPlayerNo();
            isSetupEmpty = false;
            isDeviceDB = true;

            if (n_playerno == 0) {
                if (isSetupOpen) {
                    setup.startAnimation(exit_left);
                    setup.setVisibility(View.GONE);
                    layout_transparent.setVisibility(View.INVISIBLE);
                } else {
                    setup.setVisibility(View.VISIBLE);
                    setup.startAnimation(enter_left);
                    layout_transparent.setVisibility(View.VISIBLE);
                    Log.i("____layout_transparent", "layout_transparent (device_exist_2): ??????");
                    //????????? ??????????????? ?????????????????? ?????? ??????????????? ?????? ???????????? ?????? ???????????????
                    // ????????? ?????? ????????? ??????
                }
                Toast.makeText(this, "???????????? ?????????", Toast.LENGTH_LONG).show();
            } else {

                int rowcnt = db.daoDevice().rowCount();
                Log.e("___DB_device_??? ??????", "rowcount: " + rowcnt);

                List<DB_Device> dev = db.daoDevice().getall();
                myApp.setServerIP(dev.get(0).getSsServerIP());
                myApp.setnVolume(dev.get(0).getNnVolume());
                myApp.setSsHallMode(dev.get(0).getSsHallMode());
                myApp.setnStore(dev.get(0).getNnStore());
                myApp.setsStoreName(dev.get(0).getSsStoreName());
                myApp.setnPlayerNo(dev.get(0).getNnPlayerNo());
                myApp.setClickMode(dev.get(0).getClickMode());

                try {
                    ed_serverip.setText(myApp.getServerIP());
                    seekBar_volume.setProgress(myApp.getnVolume());

                    ed_storename.setText(myApp.getsStoreName());
                    tv_storename.setText(myApp.getsStoreName());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                php_CheckServer(isChkServer, myApp.getServerIP());

//                Log.e("____DB_device_??? ??????", "Check Server ??????");

//                Toast.makeText(MainActivity.this, "?????? ???????????? ??????", Toast.LENGTH_SHORT).show();

                Log.e("____DB_device_??? ??????", myApp.toString());
                Toast.makeText( MainActivity.this, "??????????????? ???????????????.", Toast.LENGTH_LONG).show();
            }

        }
        /********************************* ????????????: DB_Device ??? ????????? ????????? ?????? ***/

        layout_transparent.setVisibility(View.GONE);


    }

    /********************************* ????????????: onCreate() ******************/

    @Override
    protected void onStart() {
        super.onStart();

        if (networkTimer == null ) {
            networkTimer = new Timer();
            networkTimer.schedule(new NetworkCheckTimer(), 100, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (networkTimer != null) {
            networkTimer.cancel();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            hideNavigationBar();
        }
    }



/**************  RecyclerView Touch Control ?????? ?????? *********/
    public void OrderToCall(int position) {
        Item_order itm = adapterOrder.getItem(position);

        Log.d("__onCreate_OrderToCall", "php_delete_order: " + position);
        php_delete_order(myApp.getnPlayerNo(), itm.getnNumber());
        adapterOrder.removeItem(position);
        Log.d("__onCreate_OrderToCall", "php_insert_call: " + position);
        php_insert_call(myApp.getnPlayerNo(), myApp.getnStore(), itm.getnTable(), itm.getnNumber());
        adapterCall.addItem(itm);

        Toast.makeText(MainActivity.this, itm.getnNumber() + "??? ????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
    }

    public void CallToOrder(int position) {
        Item_order itm = adapterCall.getItem(position);
        view_call.setItemAnimator(new SlideInOutLeftAnimator(view_call));
        Log.d("__onCreate_CallToOrder", "php_delete_call: " + position);

        php_delete_call(myApp.getnPlayerNo(), itm.getnNumber());
        adapterCall.removeItem(position);
        Log.d("__onCreate_CallToOrder", "php_insert_order: " + position);

        php_insert_order(myApp.getnPlayerNo(), myApp.getnStore(), itm.getnTable(), itm.getnNumber());
        adapterOrder.addItem(itm);
        view_call.setItemAnimator(new SlideInOutBottomAnimator(view_call));

        Toast.makeText(MainActivity.this, itm.getnNumber() + "??? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
    }

    private void CallToEnd(int position) {
        Item_order itm = adapterCall.getItem(position);
        view_call.setItemAnimator(new SlideInOutRightAnimator(view_call));
        Log.d("__onCreate_CallToEnd", "php_delete_call: " + position);

        php_delete_call(myApp.getnPlayerNo(), itm.getnNumber());
        adapterCall.removeItem(position);
        view_call.setItemAnimator(new SlideInOutLeftAnimator(view_call));

        Toast.makeText(MainActivity.this, itm.getnNumber() + "??? ????????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
    }

    public void inputOrderList(int OrderCall, AdapterRC adapter, List<Data_OrderCall> list, int position) {
        adapter.addItem(new Item_order(OrderCall, list.get(position).getiTable(), list.get(position).getiNumber()));
        adapter.notifyDataSetChanged();
    }

    public void inputOrderList(int OrderCall, AdapterRC adapter, int iTable, int iNumber) {
        adapter.addItem(new Item_order(OrderCall, iTable, iNumber));
        adapter.notifyDataSetChanged();
    }

    public void inputOrderList(int OrderCall, AdapterRC adapter, int iTable, int iNumber, int position) {
        adapter.addItem(new Item_order(OrderCall, iTable, iNumber), position);
        adapter.notifyDataSetChanged();
    }

    /********************************** ????????????: RecyclerView Touch Control ?????? ?????? ***/


/********* ???????????? ????????? ?????? ??? *********/
    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        String message;

        switch (error) {

            case SpeechRecognizer.ERROR_AUDIO:
                message = "????????? ??????";
                break;

            case SpeechRecognizer.ERROR_CLIENT:
                message = "??????????????? ??????";
                break;

            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "???????????????";
                break;

            case SpeechRecognizer.ERROR_NETWORK:
                message = "???????????? ??????";
                break;

            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "????????? ????????????";
                break;

            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "????????? ??????";;
                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "?????????";
                break;

            case SpeechRecognizer.ERROR_SERVER:
                message = "????????????";;
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "????????? ????????????";
                break;

            default:
                message = "????????????";
                break;
        }

        Log.e("___GoogleActivity", "SPEECH ERROR : " + message);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for (int i = 0; i < matches.size(); i++) {
            Log.e("___onResults_????????????", "onResults text: " + matches.get(i));
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String s = null;

                    for (int i=0; i < text.size(); i++) {
//                        Log.e("___googleActivity_????????????", "onActivityResult text: " + text.get(i));
                        s = s + text.get(i);
                    }
                    s = s.replaceAll(" ", "");
                    String num = s.replaceAll("[^0-9]", "");
//                    int pos2 = adapterOrder.getPosition(23);
//                    Item_order ii = adapterOrder.getItem(pos2);
//
//                    Log.e("___onActivityResult_?????????", "number: "+ ii.getnNumber() + " / positon: " + pos2 + " / num: " + num);

                    if (s.contains("????????????")) {
                        int pos = adapterCall.getPosition(Integer.valueOf(num));
                        Item_order itm = adapterCall.getItem(pos);
                        adapterCall.removeItem(pos);
                        Toast.makeText(MainActivity.this, itm.getnNumber() + "??? ????????? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                    } else if (s.contains("??????")) {

                        int pos = adapterOrder.getPosition(Integer.valueOf(num));
                        Item_order itm = adapterOrder.getItem(pos);
                        adapterOrder.removeItem(pos);

                        adapterCall.addItem(itm);

                        Toast.makeText(MainActivity.this, itm.getnNumber() + "??? ????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();

                    }
                }
                break;
        }
    }

    /************************************* ????????????: ???????????? ????????? ?????? ??? ***/


/************ Layout Animation ?????? **********/
    class SlidingAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isPageOpen) {
                keypad.setVisibility(View.INVISIBLE);
                btn_keypad.setImageResource(R.drawable.ic_keypad2_white);
                isPageOpen = false;
            } else {
                btn_keypad.setImageResource(R.drawable.ic_keypad1_white);
                isPageOpen = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    class SlidingSetupListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isSetupOpen) {
                setup.setVisibility(View.INVISIBLE);
                isSetupOpen = false;
            } else {
                isSetupOpen = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    /********************** ????????????: Layout Animation ?????? ***/

/********** PHP_ ?????? ??? **********/
public void php_CheckServer(boolean oldServerCheck, String svrip) {
    String serverIP = "http://" + svrip + sPath;
    Log.e("___php_server", "svrip = " + serverIP);

    final Retrofit.Builder rBuilder = new Retrofit.Builder()
            .baseUrl(serverIP)
            .addConverterFactory(GsonConverterFactory.create());
    Retrofit retrofit = rBuilder.build();
    ApiService apiService = retrofit.create(ApiService.class);
    Call<List<DB_checkserver>> call = apiService.php_check_Server();

    call.enqueue(new Callback<List<DB_checkserver>>() {
        @Override
        public void onResponse(Call<List<DB_checkserver>> call, Response<List<DB_checkserver>> response) {
            Log.d("___php_server", "php_checkserver ??????");
            Log.e("___php_server", "svrip = " + serverIP);
            if(response.isSuccessful()) {
                if(response.body() != null) {
                    Log.d("___php_server", "response: " + response.body().toString());
                    if (response.body().get(0).getCountry().toString().equals("didk")) {
                        Log.e("___php_server", "Check Server ??????");
//                            myApp.setServerIP(serverIP);
                        myApp.setServerIP(svrip);
                        isChkServer = true;
//                            php_get_store();
//                            findViewById(R.id.pgb_circle).setVisibility(View.INVISIBLE);
//                            dialog_msg(MainActivity.this, DIALOG_SINGLE, "???????????? ??????", "??????????????? ???????????????.");

                        if(layout_transparent.getVisibility() == View.VISIBLE) {
                            layout_transparent.setVisibility(View.GONE);
                        }
                    } else {
                        isChkServer = false;
                        Log.e("___php_server_1", "Check Server ??????1");
                        Toast.makeText(MainActivity.this, "????????? ??????????????? \n????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
//                            findViewById(R.id.pgb_circle).setVisibility(View.INVISIBLE);
//                            dialog_msg(MainActivity.this, DIALOG_SINGLE, "?????? ?????? ??????", "?????? ????????? ???????????????");
                    }
                } else {
                    isChkServer = false;
                    Log.e("___php_server_2", "Check Server ??????2");
                    Toast.makeText(MainActivity.this, "????????? ????????? ??? ????????????.\n?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
//                        findViewById(R.id.pgb_circle).setVisibility(View.INVISIBLE);
//                        dialog_msg(MainActivity.this, DIALOG_SINGLE, "?????? ?????? ??????", "?????? ????????? ???????????????");
                }
            } else {
                isChkServer = false;
                Log.e("___php_server_3", "Check Server ??????3");
                Toast.makeText(MainActivity.this, "????????? ????????? ??? ????????????.\n?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
//                    findViewById(R.id.pgb_circle).setVisibility(View.INVISIBLE);
//                    dialog_msg(MainActivity.this, DIALOG_SINGLE, "?????? ?????? ??????", "?????? ????????? ???????????????");
            }
            Log.e("___php_server_4", "?????????????");

            if (isChkServer) {
                Log.e("___php_server_6", "isCheckServer = true");
                if (oldServerCheck != isChkServer) {
                    Log.e("___php_server_6", "oldServerCheck != isCheckServer");
                    if (!isDialogMsg) {
                        isDialogMsg = true;
                        dialog_msg(MainActivity.this, DIALOG_SINGLE, "???????????? ??????", "??????????????? ???????????????.");
                    }
                } else {
                    Log.e("___php_server_6", "oldServerCheck == isCheckServer");
                    actionNetworkServerError(isNetWork, isChkServer);

                    php_get_order(8);
                    php_get_call(8);
                }
            } else {
                Log.e("___php_server_7", "isCheckServer = false");
                actionNetworkServerError(isNetWork, isChkServer);
            }

            if (networkTimer == null ) {
                networkTimer = new Timer();
                networkTimer.schedule(new NetworkCheckTimer(), 100, 1000);
            }
        }

        @Override
        public void onFailure(Call<List<DB_checkserver>> call, Throwable t) {

            isChkServer = false;
            Log.e("___php_server_fail", "isCheckServer = false");
            actionNetworkServerError(isNetWork, isChkServer);
        }
    });
}

    public void php_get_order(int div) {
        Log.d("___php_get_order", "serverip: " + myApp.getServerIP());
        String serverIP = "http://" + myApp.getServerIP() + sPath;

        int n_store = myApp.getnStore();
//        String dt_max = myApp.getsOrderDT();
//        String dt_max = db.daoOrder().getMaxDt();
//        int r_count = db.daoOrder().rowCount();

        final Retrofit.Builder rBuilder = new Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = rBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);
//        Call<List<DB_Order>> call = apiService.php_get_order(n_store, dt_max, myApp.getnOrderNo());
        Call<List<DB_Order>> call = apiService.php_get_order(n_store);

        call.enqueue(new Callback<List<DB_Order>>() {

            @Override
            public void onResponse(Call<List<DB_Order>> call, Response<List<DB_Order>> response) {
                List<DB_Order> newDB = new ArrayList<>();
                List<Data_OrderCall> data_oc = new ArrayList<>();
                boolean isDBChanged = false;

//                db.daoOrder().deleteAll();
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        newDB = response.body();

                        List<DB_Order> dbt = db.daoOrder().getAll();

                        if (dbt.size() != newDB.size()) {
                            isDBChanged = true;
                        } else {
                            loopout:
                            for (int i = 0; i < newDB.size(); i++) {
                                int tf = db.daoOrder().isCompareExist(newDB.get(i).getNnNumber(), newDB.get(i).getNnTable());
//                                String s = "table: " + newDB.get(i).getNnTable() +
//                                            " / number: " + newDB.get(i).getNnNumber() +
//                                            " / tf: " + tf;
//                                Log.d("____get_order", s);
                                if (tf == 0) {
                                    isDBChanged = true;
                                    break loopout;
                                }
                            }
                        }

                        if (isDBChanged){
                            db.daoOrder().deleteAll();
//                            orderlist.clear();
                            adapterOrder.removeAllItem();
                            for (int i = 0; i < newDB.size(); i++) {
                                DB_Order db_order = new DB_Order(newDB.get(i).getNnStore(),
                                        newDB.get(i).getNnTable(),
                                        newDB.get(i).getNnNumber(),
                                        newDB.get(i).getDtTime()
                                );

                                db.daoOrder().insert(db_order);

//                                Data_OrderCall doc = new Data_OrderCall(i,
//                                        newDB.get(i).getNnTable(), newDB.get(i).getNnNumber()
//                                );
//                                data_oc.add(doc);
//                                orderlist.add(doc);
                                inputOrderList(CONTENT_ORDER, adapterOrder,
                                        newDB.get(i).getNnTable(),
                                        newDB.get(i).getNnNumber());
//                                Log.e("___php_get_order", i +": number: " + doc.getiNumber());
                            }
//                            inputOrderList(CONTENT_ORDER, adapterOrder, data_oc);
                            adapterOrder.notifyItemRangeChanged(0, newDB.size());
                            Log.e("___php_get_order", "Order DB ?????????: no: " + newDB.size());
                        } else {
                            Log.e("___php_get_order", "DB ?????? ??????");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DB_Order>> call, Throwable t) {

            }
        });
    }


    public void php_get_call(int div) {

        String serverIP = "http://" + myApp.getServerIP() + sPath;
        int n_store = myApp.getnStore();
//        String dt_max = myApp.getsCallDT();
//        String dt_max = db.daoCall().getMaxDt();
//        int r_count = db.daoCall().rowCount();

        final Retrofit.Builder rBuilder = new Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = rBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<DB_Call>> call = apiService.php_get_call(n_store);

//        Log.d("___php_get_Call_" + div, "store: " + n_store + " / maxDT: " + dt_max);

        call.enqueue(new Callback<List<DB_Call>>() {

            @Override
            public void onResponse(Call<List<DB_Call>> call, Response<List<DB_Call>> response) {
                List<DB_Call> newDB = new ArrayList<>();
                List<Data_OrderCall> data_oc = new ArrayList<>();
                boolean isDBChanged = false;

//                db.daoCall().deleteAll();

                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        newDB = response.body();

                        List<DB_Call> dbt = db.daoCall().getAll();

                        if (dbt.size() != newDB.size()) {
                            isDBChanged = true;
                        } else {
                            loopout:
                            for (int i = 0; i < newDB.size(); i++) {
                                int tf = db.daoCall().isCompareExist(newDB.get(i).getNnNumber(), newDB.get(i).getNnTable());

                                if (tf == 0) {
                                    isDBChanged = true;
                                    break loopout;
                                }
                            }
                        }

                        if (isDBChanged){
                            db.daoCall().deleteAll();
                            adapterCall.removeAllItem();
//                            calllist.clear();
                            for (int i = 0; i < newDB.size() ; i++) {
                                DB_Call db_call = new DB_Call(newDB.get(i).getNnStore(),
                                        newDB.get(i).getNnTable(),
                                        newDB.get(i).getNnNumber(),
                                        newDB.get(i).getDtTime()
                                );

                                db.daoCall().insert(db_call);

//                                Data_OrderCall doc = new Data_OrderCall(i,
//                                        newDB.get(i).getNnTable(), newDB.get(i).getNnNumber()
//                                );
//                                data_oc.add(doc);
//                                calllist.add(doc);
                                inputOrderList(CONTENT_CALL, adapterCall,
                                        newDB.get(i).getNnTable(),
                                        newDB.get(i).getNnNumber());
                            }
//                            inputOrderList(CONTENT_CALL, adapterCall, data_oc);
                            adapterCall.notifyItemRangeChanged(0, newDB.size());

                            Log.e("___php_get_call", "Call DB ?????????: no: " + newDB.size());
                        } else {
                            Log.e("___php_get_call", "DB ?????? ??????");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DB_Call>> call, Throwable t) {

            }
        });
    }

    public void php_insert_order(int playerno, int store, int table, int number) {

        Log.d("___php_get_order", "serverip: " + myApp.getServerIP());
        String serverIP = "http://" + myApp.getServerIP() + sPath;

        final Retrofit.Builder rBuilder = new Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = rBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<DB_Order>> call = apiService.php_insert_order(playerno, store, table, number);

        call.enqueue(new Callback<List<DB_Order>>() {
            DB_Order dt;

            @Override
            public void onResponse(Call<List<DB_Order>> call, Response<List<DB_Order>> response) {
                List<Data_OrderCall> data_oc = new ArrayList<>();

                if(response.isSuccessful()) {
                    List<DB_Order> dbt = db.daoOrder().getAll();

//                    if (dbt != null) {
//                        dt = new DB_Order(store, table, number);
//                    } else {
//                        dt = new DB_Order(store, table, number);
//                    }
//                    db.daoOrder().insert(dt);
//
//                    List<DB_Order> dbt2 = db.daoOrder().getAll();
//
//                    for (int i = 0; i < dbt2.size(); i++) {
//                        Data_OrderCall doc = new Data_OrderCall(i,
//                                dbt2.get(i).getNnTable(), dbt2.get(i).getNnNumber()
//                        );
//                        data_oc.add(doc);
//                        orderlist.add(doc);
////                                Log.e("___php_get_order", i +": number: " + doc.getiNumber());
//                    }
//
//                    inputOrderList("order", adapterOrder, data_oc);

                }
            }

            @Override
            public void onFailure(Call<List<DB_Order>> call, Throwable t) {

            }
        });
    }

    public void php_insert_call(int playerno, int store, int table, int number) {

        Log.d("___php_get_order", "serverip: " + myApp.getServerIP());
        String serverIP = "http://" + myApp.getServerIP() + sPath;

        final Retrofit.Builder rBuilder = new Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = rBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<DB_Call>> call = apiService.php_insert_call(playerno, store, table, number);

        call.enqueue(new Callback<List<DB_Call>>() {
            DB_Call dt;

            @Override
            public void onResponse(Call<List<DB_Call>> call, Response<List<DB_Call>> response) {
                List<Data_OrderCall> data_oc = new ArrayList<>();

                if(response.isSuccessful()) {
//                    List<DB_Call> dbt = db.daoCall().getAll();
//
//                    if (dbt != null) {
//                        dt = new DB_Call(store, table, number);
//                    } else {
//                        dt = new DB_Call(store, table, number);
//                    }
//                    db.daoCall().insert(dt);
//
//                    List<DB_Order> dbt2 = db.daoOrder().getAll();
//
//                    for (int i = 0; i < dbt2.size(); i++) {
//                        Data_OrderCall doc = new Data_OrderCall(i,
//                                dbt2.get(i).getNnTable(), dbt2.get(i).getNnNumber()
//                        );
//                        data_oc.add(doc);
//                        calllist.add(doc);
////                                Log.e("___php_get_order", i +": number: " + doc.getiNumber());
//                    }
//
//                    inputOrderList("call", adapterCall, data_oc);
                }
            }

            @Override
            public void onFailure(Call<List<DB_Call>> call, Throwable t) {

            }
        });
    }

    public void php_update_order(int playerno, int store, int table, int number) {
        String serverIP = "http://" + myApp.getServerIP() + sPath;

        final Retrofit.Builder rBuilder = new Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = rBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<DB_Order>> call = apiService.php_update_order(playerno, store, table, number);

        call.enqueue(new Callback<List<DB_Order>>() {
            DB_Order dt;

            @Override
            public void onResponse(Call<List<DB_Order>> call, Response<List<DB_Order>> response) {
                List<Data_OrderCall> data_oc = new ArrayList<>();

                if(response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<List<DB_Order>> call, Throwable t) {

            }
        });
    }

    public void php_delete_order(int playerno, int number) {

        Log.d("___php_get_order", "serverip: " + myApp.getServerIP());
        String serverIP = "http://" + myApp.getServerIP() + sPath;

        final Retrofit.Builder rBuilder = new Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = rBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<DB_Order>> call = apiService.php_delete_order(playerno, number);

        call.enqueue(new Callback<List<DB_Order>>() {
            DB_Order dt;

            @Override
            public void onResponse(Call<List<DB_Order>> call, Response<List<DB_Order>> response) {
                List<Data_OrderCall> data_oc = new ArrayList<>();

                if(response.isSuccessful()) {

                    db.daoOrder().delete(number);

//                    List<DB_Order> dbt = db.daoOrder().getAll();
//
//                    if (dbt != null) {
//                        for (int i = 0; i < dbt.size(); i++) {
//                            Data_OrderCall doc = new Data_OrderCall(i,
//                                    dbt.get(i).getNnTable(), dbt.get(i).getNnNumber()
//                            );
//                            data_oc.add(doc);
//                            orderlist.add(doc);
//                            //                                Log.e("___php_get_order", i +": number: " + doc.getiNumber());
//                        }
//                    }
//
//                    inputOrderList(CONTENT_ORDER, adapterOrder, data_oc);
                }
            }

            @Override
            public void onFailure(Call<List<DB_Order>> call, Throwable t) {

            }
        });
    }

    public void php_delete_call(int playerno, int number) {

        Log.d("___php_get_order", "serverip: " + myApp.getServerIP());
        String serverIP = "http://" + myApp.getServerIP() + sPath;

        final Retrofit.Builder rBuilder = new Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = rBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<DB_Call>> call = apiService.php_delete_call(playerno, number);

        call.enqueue(new Callback<List<DB_Call>>() {
            DB_Call dt;

            @Override
            public void onResponse(Call<List<DB_Call>> call, Response<List<DB_Call>> response) {
                List<Data_OrderCall> data_oc = new ArrayList<>();

                if(response.isSuccessful()) {

                    db.daoOrder().delete(number);

//                    List<DB_Order> dbt = db.daoOrder().getAll();
//
//                    if (dbt != null) {
//                        for (int i = 0; i < dbt.size(); i++) {
//                            Data_OrderCall doc = new Data_OrderCall(i,
//                                    dbt.get(i).getNnTable(), dbt.get(i).getNnNumber()
//                            );
//                            data_oc.add(doc);
//                            calllist.add(doc);
//                            //                                Log.e("___php_get_order", i +": number: " + doc.getiNumber());
//                        }
//                    }
//
//                    inputOrderList(CONTENT_ORDER, adapterOrder, data_oc);
                }
            }

            @Override
            public void onFailure(Call<List<DB_Call>> call, Throwable t) {

            }
        });
    }

//    public void php_get_store() {
//        Log.e("___php_get_store", "svrip: " + myApp.getServerIP());
//        String serverIP = "http://" + myApp.getServerIP() + sPath;
//
//        final Retrofit.Builder rBuilder = new Retrofit.Builder()
//                .baseUrl(serverIP)
//                .addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = rBuilder.build();
//        ApiService apiService = retrofit.create(ApiService.class);
//        Call<List<DB_Store>> call = apiService.php_get_store();
//
//        call.enqueue(new Callback<List<DB_Store>>() {
//
//            @Override
//            public void onResponse(Call<List<DB_Store>> call, Response<List<DB_Store>> response) {
//                List<DB_Store> newDB = new ArrayList<>();
//                List<Data_OrderCall> data_oc = new ArrayList<>();
//                boolean isDBChanged = false;
//
////                db.daoOrder().deleteAll();
//                if(response.isSuccessful()) {
//                    if(response.body() != null) {
//                        newDB = response.body();
//
//                        if (newDB.size() > 0) {
//                            isDBChanged = true;
//                        }
//
//                        if (isDBChanged){
//                            db.daoStore().deleteAll();
//                            for (int i = 0; i < newDB.size(); i++) {
//                                DB_Store db_store = new DB_Store(newDB.get(i).getNnStore(),
//                                        newDB.get(i).getNnStore_list(),
//                                        newDB.get(i).getSsStoreName(),
//                                        newDB.get(i).getSsStore_pass(),
//                                        newDB.get(i).getSsStore_remark()
//                                );
//
//                                db.daoStore().insert(db_store);
//
//                            }
//
//                            set_dropdown_store();
//                            Log.e("___php_get_store", "Store DB ?????????: no: " + newDB.size());
//                        } else {
//                            Log.e("___php_get_store", "DB ?????? ??????");
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<DB_Store>> call, Throwable t) {
//
//            }
//        });
//    }

    public void php_insert_device(String sn, String storename,  String mIP, String hallMode, int width, int height, int volume) {

        Log.d("____php_insert_device", "serverip: " + myApp.getServerIP());
        String serverIP = "http://" + myApp.getServerIP() + sPath;

        final Retrofit.Builder rBuilder = new Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = rBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<DB_Device>> call = apiService.php_insert_device(
                sn, storename, mIP, hallMode, width, height, volume);

        call.enqueue(new Callback<List<DB_Device>>() {
            @Override
            public void onResponse(Call<List<DB_Device>> call, Response<List<DB_Device>> response) {
                List<DB_Device> newDB = new ArrayList<>();
                boolean isDBChanged = false;

                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        newDB = response.body();
                        Log.d("____php_insert_device", "Response ??? ?????????");

                        if (newDB.size() > 0) {
                            isDBChanged = true;
                        }

                        if (isDBChanged){
                            db.daoDevice().deleteAll();
                            for (int i = 0; i < newDB.size(); i++) {

                                DB_Device db_device = new DB_Device(
                                        newDB.get(i).getSsSN(),
                                        newDB.get(i).getNnPlayerNo(),
                                        myApp.getServerIP(),
                                        newDB.get(i).getSsMyIP(),
                                        myApp.getsStoreName(),
                                        myApp.getnStore(),
                                        newDB.get(i).getSsHallMode(),
                                        newDB.get(i).getNnVolume(),
                                        newDB.get(i).getNnWidth(),
                                        newDB.get(i).getNnHeight(),
                                        myApp.getModelName(),
                                        myApp.getClickMode()
                                );


                                db.daoDevice().insertAll(db_device);
                                Log.e("____php_insert_device", "DB_Device_1\n" + db.daoDevice().toString());
                            }

                            myApp.setnPlayerNo(newDB.get(0).getNnPlayerNo());
                            Log.e("____php_insert_device", "newDB_PlayerNo: " + newDB.get(0).getNnPlayerNo());
                            List<DB_Device> test = db.daoDevice().getall();
                            Log.e("____php_insert_device", "Device DB ?????????: no: " + newDB.size());
                            Log.e("____php_insert_device", "DB_Device_2:\n" + test.get(0).toString());
                            Log.e("____php_insert_device", "myApp: \n" + myApp.toString());
                        } else {
                            Log.e("___php_insert_device", "DB ?????? ??????");
                        }
                    }
//

                }
            }

            @Override
            public void onFailure(Call<List<DB_Device>> call, Throwable t) {

            }
        });
    }

    public void php_restore_call() {
        String serverIP = "http://" + myApp.getServerIP() + sPath;

        final Retrofit.Builder rBuilder = new Retrofit.Builder()
                .baseUrl(serverIP)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = rBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<DB_Call>> call = apiService.php_restore_call(myApp.getnPlayerNo(), myApp.getnStore());

        call.enqueue(new Callback<List<DB_Call>>() {

            @Override
            public void onResponse(Call<List<DB_Call>> call, Response<List<DB_Call>> response) {
                List<DB_Call> newDB = new ArrayList<>();
                List<Data_OrderCall> data_oc = new ArrayList<>();
                boolean isDBChanged = false;

                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        newDB = response.body();

                        if(newDB.get(0).getNnNumber() != 0) {
                            isDBChanged = true;
                        }

                        if (isDBChanged){
                            String sRestore = "????????????: " + newDB.get(0).getNnNumber() + "\n ??? ??????????????????.";
                            if (!isDialogMsg) {
                                isDialogMsg = true;
                                dialog_msg(MainActivity.this, 1, "?????? ??????", sRestore);
                            }
                            inputOrderList(CONTENT_CALL, adapterCall,
                                    newDB.get(0).getNnTable(),
                                    newDB.get(0).getNnNumber(),
                                    0);
//                            inputOrderList(CONTENT_CALL, adapterCall, data_oc);
                            Log.e("___php_get_call", "Call DB ?????????: no: " + newDB.size());
                        } else {
                            Log.e("___php_get_call", "DB ?????? ??????");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DB_Call>> call, Throwable t) {

            }
        });
    }

    /********************* ????????????: PHP_ ?????? ???  ***/

/********** NetworkCheckTimer **********/
class NetworkCheckTimer extends TimerTask {

    @Override
    public void run() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                check_NetworkStatus();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd(E)\na hh??? mm??? ss???", Locale.KOREAN);
                String currentDate = sdf.format(new Date());

                // ????????? ?????? ?????? ?????? ??????
                int bt = getBatteryRemain(getApplicationContext());

                String ch = "";
                if(isBatteryCharging(getApplicationContext())) {
                    imgBattery.setImageResource(R.drawable.ic_battery_charging);
                } else {
                    if (bt < 20 && bt >= 0) {
                        imgBattery.setImageResource(R.drawable.ic_battery_0);
                    } else if(bt < 50 && bt >= 20) {
                        imgBattery.setImageResource(R.drawable.ic_battery_1);
                    } else if(bt < 75 && bt >= 50) {
                        imgBattery.setImageResource(R.drawable.ic_battery_2);
                    } else if(bt <= 100 && bt >= 75) {
                        imgBattery.setImageResource(R.drawable.ic_battery_3);
                    }
                }
                tvDate.setText(currentDate);
                tvBattery.setText(bt + "%");


//                    if(isChkServer == true) {
//                        php_get_order(5);
//                        php_get_call(5);
//                    }

                php_CheckServer(isChkServer, myApp.getServerIP());
                Log.d("___NetworkTimer", "??????");

            }
        });
    }
}

    public void check_NetworkStatus() {
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());

        if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
            isNetWork = false;
        } else {
            isNetWork = true;
        }

        actionNetworkServerError(isNetWork, isChkServer);
    }

    public void actionNetworkServerError(boolean netState, boolean serverState) {

        if (isDeviceDB) {
            Log.i("___php_server_action", "isDeviceDB = true" );
            netState = true;
            if (netState) {
                Log.i("___php_server_action", "netState = isNetWork = true" );

                if (serverState) {
                    Log.i("___php_server_action", "serverState = isCheckServer = true" );
                    layout_networkstatus.setVisibility(View.GONE);
                } else {
                    Log.i("___php_server_action", "serverState = isCheckServer = true" );
                    tv_networkerror.setText("DID ????????? ????????? ??????????????????.\n???????????? ??????????????? ???????????????.");
                    layout_networkstatus.setVisibility(View.VISIBLE);
                }
            } else {
                Log.i("___php_server_action", "netState = false" );

                tv_networkerror.setText("NetWork??? ?????????????????? ????????????.\n???????????? ??????????????? ???????????????.");
                layout_networkstatus.setVisibility(View.VISIBLE);
            }
        } else {
            Log.i("___php_server_action", "isDeviceDB = false" );
        }
    }

    private String getIPAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return formatedIpAddress;
    }

    /********************************* ????????????: NetworkCheckTimer ***/


/********** ??????/?????? ?????? ?????? **********/
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //????????????
            Log.e("___onConfigureChanged", "????????????");
            isWidthMode = true;
            setWidthMod();

        } else {
            //????????????
            Log.e("___onConfigureChanged", "????????????");
            isWidthMode = false;
            setHeightMod();

        }

    }

    public void setWidthMod() {
        float dp = (xdpi > ydpi) ? ydpi : xdpi;

        LinearLayout layout_main1 = (LinearLayout) findViewById(R.id.layout_main1);
        LinearLayout.LayoutParams main1_params = (LinearLayout.LayoutParams) layout_main1.getLayoutParams();
        main1_params.weight = 100;
        layout_main1.setLayoutParams(main1_params);

        LinearLayout layout_main2 = (LinearLayout) findViewById(R.id.layout_main2);
        LinearLayout.LayoutParams main2_params = (LinearLayout.LayoutParams) layout_main2.getLayoutParams();
        main2_params.weight = 400;
        layout_main2.setLayoutParams(main2_params);

        LinearLayout layout_main3 = (LinearLayout) findViewById(R.id.layout_display);
        LinearLayout.LayoutParams main3_params = (LinearLayout.LayoutParams) layout_main3.getLayoutParams();
        main3_params.weight = 7;
        layout_main3.setLayoutParams(main3_params);


        TextView label_order = findViewById(R.id.label_order);
        TextView tv_ordernum = findViewById(R.id.tv_ordernum);
        TextView tv_date = findViewById(R.id.tv_date);
        TextView label_call = findViewById(R.id.label_call);
        TextView tv_callnum = findViewById(R.id.tv_callnum);
        TextView tv_battery = findViewById(R.id.tv_battery);
        ImageView img_battery = findViewById(R.id.img_battery_charging);

        LinearLayout.LayoutParams imgParam = (LinearLayout.LayoutParams) img_battery.getLayoutParams();
        imgParam.setMargins(0,10,0,10);
        img_battery.setLayoutParams(imgParam);

        label_order.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80 * 160 / dp);
        tv_ordernum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80 * 160 / dp);
        tv_date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 * 160 / dp);
        label_call.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80 * 160 / dp);
        tv_callnum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80 * 160 / dp);
        tv_battery.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 * 160 / dp);


    }

    public void setHeightMod() {
        float dp = (xdpi > ydpi) ? ydpi : xdpi;

        LinearLayout layout_main1 = (LinearLayout) findViewById(R.id.layout_main1);
        LinearLayout.LayoutParams main1_params = (LinearLayout.LayoutParams) layout_main1.getLayoutParams();
        main1_params.weight = 60;
        layout_main1.setLayoutParams(main1_params);

        LinearLayout layout_main2 = (LinearLayout) findViewById(R.id.layout_main2);
        LinearLayout.LayoutParams main2_params = (LinearLayout.LayoutParams) layout_main2.getLayoutParams();
        main2_params.weight = 440;
        layout_main2.setLayoutParams(main2_params);

        LinearLayout layout_main3 = (LinearLayout) findViewById(R.id.layout_display);
        LinearLayout.LayoutParams main3_params = (LinearLayout.LayoutParams) layout_main3.getLayoutParams();
        main3_params.weight = 10;
        layout_main3.setLayoutParams(main3_params);


        TextView label_order = findViewById(R.id.label_order);
        TextView tv_ordernum = findViewById(R.id.tv_ordernum);
        TextView tv_date = findViewById(R.id.tv_date);
        TextView label_call = findViewById(R.id.label_call);
        TextView tv_callnum = findViewById(R.id.tv_callnum);
        TextView tv_battery = findViewById(R.id.tv_battery);
        ImageView img_battery = findViewById(R.id.img_battery_charging);

        LinearLayout.LayoutParams imgParam = (LinearLayout.LayoutParams) img_battery.getLayoutParams();
        imgParam.setMargins(0,5,0,5);
        img_battery.setLayoutParams(imgParam);

        label_order.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50 * 160 / dp);
        tv_ordernum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50 * 160 / dp);
        tv_date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 * 160 / dp);
        label_call.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50 * 160 / dp);
        tv_callnum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50 * 160 / dp);
        tv_battery.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 * 160 / dp);

    }

    /******************************* ????????????: ??????/?????? ?????? ?????? ***/


/********* ?????? ?????? ??? ********/

public void initKeyPad() {

    btn00 = findViewById(R.id.btn_00);
    btn01 = findViewById(R.id.btn_01);
    btn02 = findViewById(R.id.btn_02);
    btn03 = findViewById(R.id.btn_03);
    btn04 = findViewById(R.id.btn_04);
    btn05 = findViewById(R.id.btn_05);
    btn06 = findViewById(R.id.btn_06);
    btn07 = findViewById(R.id.btn_07);
    btn08 = findViewById(R.id.btn_08);
    btn09 = findViewById(R.id.btn_09);
    btn11 = findViewById(R.id.btn_11);
    btn12 = findViewById(R.id.btn_12);
    btn13 = findViewById(R.id.btn_13);
    btn14 = findViewById(R.id.btn_14);
    btn15 = findViewById(R.id.btn_15);

    btn00.setOnClickListener(keypadClickListener);
    btn01.setOnClickListener(keypadClickListener);
    btn02.setOnClickListener(keypadClickListener);
    btn03.setOnClickListener(keypadClickListener);
    btn04.setOnClickListener(keypadClickListener);
    btn05.setOnClickListener(keypadClickListener);
    btn06.setOnClickListener(keypadClickListener);
    btn07.setOnClickListener(keypadClickListener);
    btn08.setOnClickListener(keypadClickListener);
    btn09.setOnClickListener(keypadClickListener);
    btn11.setOnClickListener(keypadClickListener);
    btn12.setOnClickListener(keypadClickListener);
    btn13.setOnClickListener(keypadClickListener);
    btn14.setOnClickListener(keypadClickListener);
    btn15.setOnClickListener(keypadClickListener);
}

    View.OnClickListener keypadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int num = 0;
            String sNum = "";

            switch (v.getId()) {
                case R.id.btn_00:
                    tvNUM.append("0");
                    break;
                case R.id.btn_01:
                    tvNUM.append("1");
                    break;
                case R.id.btn_02:
                    tvNUM.append("2");
                    break;
                case R.id.btn_03:
                    tvNUM.append("3");
                    break;
                case R.id.btn_04:
                    tvNUM.append("4");
                    break;
                case R.id.btn_05:
                    tvNUM.append("5");
                    break;
                case R.id.btn_06:
                    tvNUM.append("6");
                    break;
                case R.id.btn_07:
                    tvNUM.append("7");
                    break;
                case R.id.btn_08:
                    tvNUM.append("8");
                    break;
                case R.id.btn_09:
                    tvNUM.append("9");
                    break;
                case R.id.btn_11:  /*** ?????? ??????   ***/
                    tvORDER.setText("??????");

//                    sNum = tvORDER.getText().toString();
                    sNum = tvNUM.getText().toString();
                    Log.e("___???????????? ??????", "sNum: " +  sNum);
                    if (sNum.isEmpty())   {
//                        Log.e("___???????????? ??????", "sNum = ''");
                        sNum = "0";
                    }
                    num = Integer.valueOf(sNum);

//                    Log.e("___???????????? ??????", "num: " + num);

                    if (num ==  0) {  //  ???????????? ????????? ?????????
                        php_restore_call();

                        if (isPageOpen) {
                            keypad.startAnimation(translateRight);
                            layout_transparent.setVisibility(View.GONE);
                        } else {
                            keypad.setVisibility(View.VISIBLE);
                            keypad.startAnimation(translateLeft);
                            layout_transparent.setVisibility(View.VISIBLE);
                            Log.i("____layout_transparent", "layout_transparent (keypadClickListener_1): ??????");
                        }

                    } else {  // ?????? ?????? ????????? ?????? ??? :  Call -> Order??? ?????? ??????
                        int rows = db.daoCall().isDataExist(num);
                        if(rows > 0) {  // Call ??? ?????? ?????? ?????? ???
                            DB_Call dc = db.daoCall().getCall(num);
                            php_delete_call(myApp.getnPlayerNo(), num);
                            Log.e("___keyPad_btn_click", "store: " + dc.getNnStore() + " / table: " + dc.getNnTable() + " / number: " + dc.getNnNumber());
                            php_insert_order(myApp.getnPlayerNo(),dc.getNnStore(), dc.getNnTable(), dc.getNnNumber());
                        } else {
                            if (!isDialogMsg) {
                                isDialogMsg = true;
                                dialog_msg(getApplicationContext(), DIALOG_DOUBLE, "?????? ??????", "???????????? ???????????? ????????? ????????? ????????????.\n?????????????????? ????????? ???????????? ????????????????");
                            }
                            Log.e("___keyPad_btn_click", "dialg_msg_buttonclick: " + dialg_msg_buttonclick);
                            if(dialg_msg_buttonclick == 2) {
                                php_restore_call();
                                dialg_msg_buttonclick = 0;
                            }
                        }

                        if (isPageOpen) {
                            keypad.startAnimation(translateRight);
                            layout_transparent.setVisibility(View.GONE);
                        } else {
                            keypad.setVisibility(View.VISIBLE);
                            keypad.startAnimation(translateLeft);
                            layout_transparent.setVisibility(View.VISIBLE);
                            Log.i("____layout_transparent", "layout_transparent (keypadClickListener_2): ??????");
                        }
                    }
                    break;

                case R.id.btn_12:  /*** ?????? ??????   ***/
                    tvORDER.setText("??????");
                    String sO = tvNUM.getText().toString();
                    if (sO.isEmpty()) {
                        num = 0;
                    } else {
                        num = Integer.valueOf(sO);
                    }
                    if (num != 0) {
                        int n_order = db.daoOrder().isDataExist(num);
                        int n_call = db.daoCall().isDataExist(num);

                        Log.i("____layout_transparent",  "num: " + num + " / n_order: " + n_order + " / n_call: " + n_call);
                        if (n_order == 0 && n_call == 0) {
                            DB_Order rct = db.daoOrder().getOrder(num);

                            php_insert_order(myApp.getnPlayerNo(), myApp.getnStore(), 0, num);
                            php_get_order(1);

                            if (isPageOpen) {
                                keypad.startAnimation(translateRight);
                                layout_transparent.setVisibility(View.GONE);
                            } else {
                                keypad.setVisibility(View.VISIBLE);
                                keypad.startAnimation(translateLeft);
                                layout_transparent.setVisibility(View.VISIBLE);
                                Log.i("____layout_transparent", "layout_transparent (keypadClickListener_3): ??????");
                            }
                        } else if(n_order > 0 && n_call == 0){
//                            dialog_msg(MainActivity.this, DIALOG_SINGLE, "???????????? ?????? ??????", "?????? ???????????? ???????????????.");
                            Toast.makeText(MainActivity.this, "???????????? ?????? ??????\n?????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            tvORDER.setText("");
                            tvNUM.setText("");
                        } else if(n_order == 0 && n_call > 0) {
                            Toast.makeText(MainActivity.this, "??????????????? ??????\n?????? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            tvORDER.setText("");
                            tvNUM.setText("");
                        }
                    } else {
                        if (!isDialogMsg) {
                            isDialogMsg = true;
                            dialog_msg(MainActivity.this, DIALOG_SINGLE, "???????????? ?????????", "??????????????? ????????? ?????????.");
                        }
                        tvORDER.setText("");
                        tvNUM.setText("");
                    }
                    break;

                case R.id.btn_13:  /*** ?????? ??????   ***/
                    tvORDER.setText("??????");
                    String dummy = tvNUM.getText().toString();
                    if (dummy.isEmpty()) {
                        dummy = "0";
                    }
                    num = Integer.valueOf(dummy);
                    if (num != 0) {
                        if(db.daoOrder().isDataExist(num) == 1) {
                            DB_Order rct = db.daoOrder().getOrder(num);

                            php_delete_order(myApp.getnPlayerNo(), num);
                            php_insert_call(myApp.getnPlayerNo() ,myApp.getnStore(), rct.getNnTable(), num);
                            php_get_order(2);
                            php_get_call(2);

                            Handler volHandler = new Handler();
                            Runnable volumeRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (isVolumeTest == false) {
                                        setVolume(myApp.getnVolume());
                                    }
                                }
                            };

                            volHandler.postDelayed(volumeRunnable, 1300);


                            if (isPageOpen) {
                                keypad.startAnimation(translateRight);
                                layout_transparent.setVisibility(View.GONE);
                            } else {
                                keypad.setVisibility(View.VISIBLE);
                                keypad.startAnimation(translateLeft);
                                layout_transparent.setVisibility(View.VISIBLE);
                                Log.i("____layout_transparent", "layout_transparent (keypadClickListener_4): ??????");
                            }

                            Toast.makeText(MainActivity.this, num + "??? ????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                        } else if(db.daoCall().isDataExist(num) == 1) {
                            DB_Call rct = db.daoCall().getCall(num);
                            php_delete_call(myApp.getnPlayerNo(), num);
                            php_get_call(9);

                            if (isPageOpen) {
                                keypad.startAnimation(translateRight);
                                layout_transparent.setVisibility(View.GONE);
                            } else {
                                keypad.setVisibility(View.VISIBLE);
                                keypad.startAnimation(translateLeft);
                                layout_transparent.setVisibility(View.VISIBLE);
                                Log.i("____layout_transparent", "layout_transparent (keypadClickListener_5): ??????");
                            }

                            Toast.makeText(MainActivity.this, "????????? " + num + "??? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                        } else {
                            if (!isDialogMsg) {
                                isDialogMsg = true;
                                dialog_msg(MainActivity.this, DIALOG_SINGLE, "???????????? ????????????", "???????????? ??????????????? ????????? ?????? ???????????????.");
                            }
                            tvORDER.setText("");
                            tvNUM.setText("");
                        }
                    } else {
                        if (!isDialogMsg) {
                            isDialogMsg = true;
                            dialog_msg(MainActivity.this, DIALOG_SINGLE, "???????????? ?????????", "??????????????? ????????? ?????????.");
                        }
                        tvORDER.setText("");
                        tvNUM.setText("");
                    }
                    break;

                case R.id.btn_14:  /*** ????????? ??????   ***/
                    tvNUM.setText("");
                    tvORDER.setText("?????????");
                    break;
                case R.id.btn_15:  /*** KeyPad ??????   ***/
                    tvNUM.setText("");
                    tvORDER.setText("?????????");

                    if (isPageOpen) {
                        keypad.startAnimation(translateRight);
                        layout_transparent.setVisibility(View.GONE);
                    } else {
                        keypad.setVisibility(View.VISIBLE);
                        keypad.startAnimation(translateLeft);
                        layout_transparent.setVisibility(View.VISIBLE);
                        Log.i("____layout_transparent", "layout_transparent (keypadClickListener_6): ??????");
                    }
                    break;
            }
        }
    };

    View.OnClickListener setupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_setupok:
                    Log.d("____btn_setup_confirm", "???????????? ??????");
                    isDeviceDB = true;

                    if (isSetupOpen) {
                        Log.d("____btn_setup_confirm", "isSetupOpen = true");
                        String svrIP = ed_serverip.getText().toString();
                        String myIP = tv_myip.getText().toString();
                        int volume = myApp.getnVolume();
                        int nStore = 0;
                        myApp.setnStore(nStore);
                        String sStoreName = ed_storename.getText().toString();
                        if(sStoreName.isEmpty()) {
                            sStoreName = "?????? ?????? ??????";
                        }

                        myApp.setsStoreName(sStoreName);
                        tv_storename.setText(sStoreName);

                        String s = "????????????: " + svrIP + "\n" + "??????????????? ??????: " + myIP  + "\n" +
                                "??????: " + volume;
                        Log.d("____btn_setup_confirm", s);

                        int rdevice = db.daoDevice().isNotEmpty();
                        Log.d("____btn_setup_confirm", "daoDevice().isNotEmpty(): " + rdevice);
                        if (rdevice == 1) {
                            db.daoDevice().updateAll(
                                    myApp.getsSN(),
                                    myApp.getnPlayerNo(),
                                    myApp.getServerIP(),
                                    myApp.getMyIP(),
                                    myApp.getsStoreName(),
                                    myApp.getnStore(),
                                    myApp.getSsHallMode(),
                                    myApp.getnVolume(),
                                    myApp.getnWidth(),
                                    myApp.getnHeight(),
                                    myApp.getModelName()
                            );

                            php_insert_device(
                                    myApp.getsSN(),
                                    myApp.getsStoreName(),
                                    myApp.getMyIP(),
                                    myApp.getSsHallMode(),
                                    myApp.getnWidth(),
                                    myApp.getnHeight(),
                                    myApp.getnVolume()
                            );
                            Log.d("____btn_setup_confirm", "??????:update\n" + myApp.toString());

                        } else {
                            php_insert_device(
                                    myApp.getsSN(),
                                    myApp.getsStoreName(),
                                    myApp.getMyIP(),
                                    myApp.getSsHallMode(),
                                    myApp.getnWidth(),
                                    myApp.getnHeight(),
                                    myApp.getnVolume()
                            );
                            Log.d("____btn_setup_confirm", "??????:Insert\n" + myApp.toString());

//                            DB_Device dc = new DB_Device(
//                                    myApp.getsSN(),
//                                    myApp.getnPlayerNo(),
//                                    myApp.getServerIP(),
//                                    myApp.getMyIP(),
//                                    myApp.getsStoreName(),
//                                    myApp.getnStore(),
//                                    myApp.getSsHallMode(),
//                                    myApp.getnVolume(),
//                                    myApp.getnWidth(),
//                                    myApp.getnHeight(),
//                                    myApp.getModelName()
//                            );
//
//                            db.daoDevice().insertAll(dc);
                        }
                        if (!isDialogMsg) {
                            isDialogMsg = true;
                            dialog_msg(v.getContext(), DIALOG_SINGLE, "?????????", s);
                        }

                        setup.startAnimation(exit_left);
                        layout_transparent.setVisibility(View.GONE);
                        actionNetworkServerError(isNetWork, isChkServer);
                    } else {
                        setup.setVisibility(View.VISIBLE);
                        setup.startAnimation(enter_left);
                        layout_transparent.setVisibility(View.VISIBLE);
                        Log.i("____layout_transparent", "layout_transparent (setup:istener_1): ??????");
                        Log.d("____btn_setup_confirm", "isSetupOpen = false");
                    }
                    break;

                case R.id.btn_setupcancle:  /*** ?????? ??????   ***/
                    Log.d("___btn_setup_confirm", "???????????? ??????");

                    if (isSetupOpen) {
                        setup.startAnimation(exit_left);
                        layout_transparent.setVisibility(View.GONE);
                    } else {
                        setup.setVisibility(View.VISIBLE);
                        setup.startAnimation(enter_left);
                        layout_transparent.setVisibility(View.VISIBLE);
                        Log.i("____layout_transparent", "layout_transparent (setup:istener_2): ??????");
                    }
                    break;
            }
        }
    };


    public void setVolume(int vol) {
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        int cur_vol = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);
//        Log.e("__TimeCheck_main", "vol = " + vol);

        int change_volume = Math.round(cur_vol * vol / 100);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, change_volume, audioManager.FLAG_PLAY_SOUND );

        if (isVolumeTest == false) {
            isVolumeTest = true;
            Handler handler = new Handler();

            MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.dingdong);

            Runnable soundplay = new Runnable() {
                @Override
                public void run() {
                    player.stop();
                    isVolumeTest = false;
                }
            };

            player.start();
            handler.postDelayed(soundplay, 1000);


            Log.e("_____setVolume", "rt.play is Action");
        }

//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        rt = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        rt.play();
//        Log.e("_____setVolume", "rt.play is Action");


        Log.e("_____setVolume", "rt.play is Action");
    }



    public void dialog_msg(Context context, int button_no, String title, String messages) {
        AlertDialog.Builder msg = new AlertDialog.Builder(context);
        if (button_no == DIALOG_SINGLE) {
            msg.setTitle(title)
                    .setMessage(messages)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialg_msg_buttonclick = 2;
                            isDialogMsg = false;
                        }
                    })
                    .setCancelable(false);
        } else if (button_no == DIALOG_DOUBLE){
            msg.setTitle(title)
                    .setMessage(messages)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialg_msg_buttonclick = 2;
                            isDialogMsg = false;
                        }
                    })
                    .setCancelable(false)
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialg_msg_buttonclick = 0;
                            isDialogMsg = false;
                        }
                    });
        }
        msg.show();

    }

    public static int getBatteryRemain(Context context) {
        Intent intentBattery = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intentBattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;

        return (int)(batteryPct * 100);
    }

    public static boolean isBatteryCharging(Context context) {
        Intent ib = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = ib.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        boolean isCharging = false;

        if(status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL) {
            isCharging = true;
        } else if(status == BatteryManager.BATTERY_STATUS_NOT_CHARGING || status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            isCharging = false;
        }

        return isCharging;
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /*********************************** ????????????: ?????? ?????? ??? ***/
}