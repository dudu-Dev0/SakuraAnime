package my.project.sakuraproject.util;

import android.Manifest;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.palette.graphics.Palette;
import jp.wasabeef.glide.transformations.BlurTransformation;
import my.project.sakuraproject.BuildConfig;
import my.project.sakuraproject.R;
import my.project.sakuraproject.bean.UpdateImgBean;
import my.project.sakuraproject.custom.CustomToast;
import my.project.sakuraproject.main.base.BaseModel;
import my.project.sakuraproject.main.my.MyActivity;
import my.project.sakuraproject.sniffing.SniffingVideo;

public class Utils {
    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    public static void createFile() {
        File dataDir = new File(Environment.getExternalStorageDirectory() + "/SakuraAnime/Database");
        if (!dataDir.exists())
            dataDir.mkdirs();
        File downloadDir = new File(Environment.getExternalStorageDirectory() + "/SakuraAnime/Downloads");
        if (!downloadDir.exists())
            downloadDir.mkdirs();
    }

    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 加载框
     *
     * @return
     */
    public static AlertDialog getProDialog(Activity activity, @StringRes int id) {
        AlertDialog alertDialog;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogStyle);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_proress, null);
        RelativeLayout root = view.findViewById(R.id.root);
        TextView msg = view.findViewById(R.id.msg);
        root.setBackgroundColor(activity.getResources().getColor(R.color.window_bg));
        msg.setTextColor(activity.getResources().getColor(R.color.text_color_primary));
        msg.setText(getString(id));
        builder.setCancelable(false);
        alertDialog = builder.setView(view).create();
        alertDialog.show();
        return alertDialog;
    }

    /**
     * 关闭对话框
     * @param alertDialog
     */
    public static void cancelDialog(AlertDialog alertDialog) {
        if (alertDialog != null)
            alertDialog.dismiss();
    }

    /**
     * 选择视频播放器
     *
     * @param url
     */
    public static void selectVideoPlayer(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse(url), "video/*");
        try {
            context.startActivity(Intent.createChooser(intent, "请选择视频播放器"));
        } catch (ActivityNotFoundException e) {
//            Sakura.getInstance().showToastMsg("没有找到匹配的程序");
            CustomToast.showToast(getContext(), "没有找到匹配的程序", CustomToast.WARNING);
        }
    }

    /**
     * 通过浏览器打开
     *
     * @param url
     */
    public static void viewInBrowser(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "请通过浏览器打开"));
        } else {
//            Sakura.getInstance().showToastMsg("没有找到匹配的程序");
            CustomToast.showToast(getContext(), "没有找到匹配的程序", CustomToast.WARNING);
        }
    }

    /**
     * 通过浏览器打开
     * @param context
     * @param url
     */
    public static void viewInChrome(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        //Sets the toolbar color.
        builder.setToolbarColor(context.getResources().getColor(R.color.night));
        Bitmap closeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.baseline_close_white_48dp);
        builder.setCloseButtonIcon(closeBitmap);// 关闭按钮
        builder.setShowTitle(true); //显示网页标题
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    /**
     * 获取string.xml文本
     * @param id
     * @return
     */
    public static String getString(@StringRes int id) {
        return getContext().getResources().getString(id);
    }

    public static String[] getArray(@ArrayRes int id) {
        return getContext().getResources().getStringArray(id);
    }

    /**
     * 获取当前日期是星期几
     *
     * @param dt
     * @return + 1 当前日期是星期几
     */
    public static int getWeekOfDate(Date dt) {
        int[] weekDays = {6, 0, 1, 2, 3, 4, 5};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 首次进入主页弹窗
     */
    public static void showX5Info(Context context) {
        AlertDialog alertDialog;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.DialogStyle);
        builder.setPositiveButton(getString(R.string.x5_info_positive), null);
        builder.setMessage(getString(R.string.x5_info));
        builder.setTitle(getString(R.string.x5_info_title));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            SharedPreferencesUtils.setParam(getContext(), "show_x5_info", false);
            alertDialog.dismiss();
        });
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static ObjectAnimator tada(View view) {
        return tada(view, 2f);
    }

    public static ObjectAnimator tada(View view, float shakeFactor) {

        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -3f * shakeFactor),
                Keyframe.ofFloat(.2f, -3f * shakeFactor),
                Keyframe.ofFloat(.3f, 3f * shakeFactor),
                Keyframe.ofFloat(.4f, -3f * shakeFactor),
                Keyframe.ofFloat(.5f, 3f * shakeFactor),
                Keyframe.ofFloat(.6f, -3f * shakeFactor),
                Keyframe.ofFloat(.7f, 3f * shakeFactor),
                Keyframe.ofFloat(.8f, -3f * shakeFactor),
                Keyframe.ofFloat(.9f, 3f * shakeFactor),
                Keyframe.ofFloat(1f, 0)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY, pvhRotate).
                setDuration(1000);
    }

    public static ObjectAnimator nope(View view) {
        int delta = view.getResources().getDimensionPixelOffset(R.dimen.spacing_medium);
        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(0f, 0)
        );
        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(500);
    }

    /**
     * 隐藏动画&显示动画
     * type 0 隐藏  1 显示
     *
     * @return
     */
    public static ScaleAnimation animationOut(int type) {
        ScaleAnimation scaleAnimation = null;
        switch (type) {
            case 0:
                scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(200);
                break;
            case 1:
                scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(200);
                break;
        }
        return scaleAnimation;
    }

    /**
     * 复制到剪切板
     */
    public static void putTextIntoClip(String string) {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("string", string);
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * 滑动返回配置
     *
     * @return
     */
    /*public static SlidrConfig defaultInit() {
        SlidrConfig.Builder mBuilder;
        mBuilder = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .scrimStartAlpha(0.8f)
                .scrimEndAlpha(0f)
                .velocityThreshold(5f)
                .distanceThreshold(.35f)
                .edge((Boolean) SharedPreferencesUtils.getParam(getContext(), "slidr_config", false))
                .edgeSize(0.18f);// The % of the screen that counts as the edge, default 18%;
        return mBuilder.build();
    }*/

    public static boolean getSlidrConfig() {
        return (Boolean) SharedPreferencesUtils.getParam(getContext(), "slidr_config", false);
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad() {
        return (getContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 设置默认图片
     * @param context
     * @param img
     * @param imageView
     * @param setPalette
     * @param cardView
     * @param textView
     */
    public static void setDefaultImage(Context context, String img, String htmlUrl, ImageView imageView, boolean setPalette, CardView cardView, TextView textView) {
        imageView.setImageDrawable(getTheme() ? context.getDrawable(R.drawable.loading_night) : context.getDrawable(R.drawable.loading_light));
        DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
        GlideUrl imgUrl;
        if (img.contains("yhdmtu"))
            imgUrl = new GlideUrl(getImgUrl(img, false));
        else
            imgUrl = new GlideUrl(getImgUrl(img, true));
        Glide.with(context)
                .asBitmap()
                .transition(BitmapTransitionOptions.withCrossFade())
                .load(imgUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        imageView.setImageDrawable(context.getDrawable(R.drawable.error));
                        EventBus.getDefault().post(new UpdateImgBean(img, htmlUrl));
                    }
                });
        if (!getTheme() && setPalette)
            // 设置Palette
            Glide.with(context).asBitmap().load(imgUrl).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Palette.from(resource).generate(palette -> {
                        Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                        if (swatch != null) {
                            cardView.setCardBackgroundColor(swatch.getRgb());
                            textView.setTextColor(swatch.getBodyTextColor());
                        }
                    });
                }

                @Override
                public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                    imageView.setImageDrawable(context.getDrawable(R.drawable.error));
                    EventBus.getDefault().post(new UpdateImgBean(img, htmlUrl));
                }
            });
    }

    public static void setCardDefaultBg(Context context, CardView cardView, TextView textView) {
        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.window_bg));
        textView.setTextColor(context.getResources().getColor(R.color.text_color_primary));
    }

    public static void setImgViewBg(Context context, int source, String img, String descUrl, ImageView imageView) {
        imageView.setImageDrawable(getTheme() ? context.getDrawable(R.drawable.loading_night) : context.getDrawable(R.drawable.loading_light));
        GlideUrl imgUrl;
        if (source == 1)
            imgUrl = new GlideUrl(getImgUrl(img, true));
        else
            imgUrl = new GlideUrl(getImgUrl(img, false), new LazyHeaders.Builder()
                    .addHeader("Referer", BaseModel.getDomain(false) + "/")
                    .build());
        Glide.with(context)
                .asBitmap()
                .transition(BitmapTransitionOptions.withCrossFade())
                .load(imgUrl)
                .apply(RequestOptions.bitmapTransform( new BlurTransformation(15, 5)))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                        EventBus.getDefault().post(new UpdateImgBean(img, descUrl));
                    }
                });
    }

    public static void loadVideoScreenshot(final Context context, String uri, String defaultImg, ImageView imageView, long frameTimeMicros) {
        /*RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        requestOptions.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }
            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((context.getPackageName() + "RotateTransform").getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);*/
        RequestOptions options = new RequestOptions().frame(frameTimeMicros);
        Glide.with(context).asBitmap().load(uri)
                .apply(options)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (resource.getAllocationByteCount() > 100)
                            imageView.setImageBitmap(resource);
                        else
                            onLoadFailed(null);
                    }

                    @Override
                    public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                        Glide.with(context).load(defaultImg).apply(RequestOptions.bitmapTransform( new BlurTransformation(15, 5))).into(imageView);
                    }
                });
    }

    public static String getASVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * 删除文件
     *
     * @param root
     */
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    /**
     * 安装应用
     *
     * @param activity
     */
    public static void startInstall(Activity activity) {
        //权限不存在，申请权限，并跳到当前包
        if (!isGranted(activity, Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            Uri packageURI = Uri.parse("package:" + activity.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            activity.startActivityForResult(intent, 10001);
        } else {
            install(activity);
        }
    }

    /**
     * 安装应用
     *
     * @param activity
     */
    private static void install(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //android 7.0权限问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider", new File(Environment.getExternalStorageDirectory(), "base.apk"));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "base.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(intent);
    }

    /**
     * 判断是否为Android O+
     *
     * @param activity
     * @param permission
     * @return
     */
    private static boolean isGranted(Activity activity, String permission) {
        // 8.0 权限 安装apk 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return activity.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 发现新版本弹窗
     * @param context
     * @param version
     * @param body
     * @param posListener
     * @param negListener
     */
    public static void findNewVersion(Context context,
                                      String version,
                                      String body,
                                      DialogInterface.OnClickListener posListener,
                                      DialogInterface.OnClickListener negListener) {
        AlertDialog alertDialog;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.DialogStyle);
        builder.setMessage(body);
        builder.setTitle(getString(R.string.find_new_version) + version);
        builder.setPositiveButton(getString(R.string.update_now), posListener);
        builder.setNegativeButton(getString(R.string.update_after), negListener);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private static final String NAVIGATION= "navigationBarBackground";

    /**
     * 判断是否有NavigationBar
     *
     * @param activity
     * @return
     */
    public static boolean checkHasNavigationBar(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 获得NavigationBar的高度 +15
     */
    public static int getNavigationBarHeight(Activity activity) {
        int result = 0;
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && checkHasNavigationBar(activity)) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        Log.e("getNavigationBarHeight", result + "");
        return result + 15;
    }

    /**
     * dp转px
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + 0.5f);
    }

    /**
     * 获取状态栏高度
     * @return
     */
    public static int getStatusBarHeight() {
        int statusBarHeight = 20;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 获取ActionBar 高度
     * @return
     */
    public static int getActionBarHeight(){
        TypedValue tv = new TypedValue();
        if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize,tv,true)){
            return  TypedValue.complexToDimensionPixelSize(tv.data,
                    getContext().getResources().getDisplayMetrics());
        }
        return 0;
    }

    /**
     * X5内核加载状态
     * @return
     */
    /*public static boolean getX5State() {
        return (boolean) SharedPreferencesUtils.getParam(getContext(), "X5State", false);
    }*/

    /**
     * 是否启用x5内核
     * @return
     */
    /*public static boolean loadX5() {
        return (boolean) SharedPreferencesUtils.getParam(getContext(), "loadX5", false);
    }*/

    public static int getPixelHeight(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;
        Log.i("Pixel", "widthPixels = " + widthPixels + ",heightPixels = " + heightPixels);
        return heightPixels;
    }

    /**
     * 嗅探视频地址集合
     * @param list
     * @return
     */
    public static List ridRepeat(List<SniffingVideo> list) {
        List<String> urls = new ArrayList();
        for (SniffingVideo sniffingVideo : list) {
            if (!urls.contains(sniffingVideo.getUrl()) && !sniffingVideo.getUrl().startsWith("mp4"))
                urls.add(sniffingVideo.getUrl());
        }
        return urls;
    }

    /**
     * 判断当前选择的网站源是否是imomoe
     * @return
     */
    public static boolean isImomoe() {
        return (boolean) SharedPreferencesUtils.getParam(Utils.getContext(), "isImomoe", false);
    }

    public static String getImgUrl(String url, boolean isImomoe) {
        return url.contains("http") ? url : BaseModel.getDomain(isImomoe) + url;
    }

    public static String isYesterday (Date updateTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
        String todayStr = format.format(new Date());
        Date today = format.parse(todayStr);
        if((today.getTime()-updateTime.getTime())>0 && (today.getTime()-updateTime.getTime())<=86400000) {
            return "昨天 " + format2.format(updateTime);
        }
        else if((today.getTime()-updateTime.getTime())<=0){
            return "今天 " + format2.format(updateTime);
        }
        else{
            return format1.format(updateTime);
        }
    }

    private static String stringForHoursAndMinutes(long timeMs) {
        if (timeMs > 0L && timeMs < 86400000L) {
            long totalSeconds = timeMs / 1000L;
            int minutes = (int)(totalSeconds / 60L % 60L);
            int hours = (int)(totalSeconds / 3600L);
            StringBuilder stringBuilder = new StringBuilder();
            Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
            return hours > 0 ? mFormatter.format("%d:%02d", hours, minutes).toString() : mFormatter.format("%02d", minutes).toString();
        } else {
            return "00:00";
        }
    }

    public static boolean videoHasComplete(long playPosition, long videoDuration) {
        return stringForHoursAndMinutes(playPosition).equals(stringForHoursAndMinutes(videoDuration));
    }

    public static String getNetFileSizeDescription(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        }
        else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        }
        else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        }
        else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
            }
            else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }


    /**
     * 判断服务是否正在运行
     *
     * @param serviceName 服务类的全路径名称
     * @param context 上下文对象
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        //活动管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100); //获取运行的服务,参数表示最多返回的数量

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(serviceName)) {
                return true; //判断服务是否运行
            }
        }
        return false;
    }

    public static boolean isTopActivity(Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(MyActivity.class.getName());
    }

    /**
     * 检查网络是否是wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager =(ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null &&activeNetworkInfo.getType() == connectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }
    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return "http://"+int2ip(i)+":8080";
        } catch (Exception ex) {
            return null;
        }
        // return null;
    }

    /**
     * 获取当前主题
     * @return true 黑夜主题 false 白天主题
     */
    public static boolean getTheme() {
        return (boolean) SharedPreferencesUtils.getParam(getContext(), "darkTheme", false);
    }

    public static void fadeIn( View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        Animation animation = new AlphaAnimation(0F, 1F);
        animation.setDuration(800);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setEnabled(true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    public static void fadeOut(final View view) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        view.setEnabled(false);
        Animation animation = new AlphaAnimation(1F, 0F);
        animation.setDuration(800);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(animation);
    }
}
