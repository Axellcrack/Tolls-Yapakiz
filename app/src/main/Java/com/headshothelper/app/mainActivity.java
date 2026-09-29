package com.headshothelper.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import android.graphics.Color;
import android.view.Gravity;

public class MainActivity extends Activity {

    public static float SENS = 1.0f;
    public static float SMOOTH = 0.6f;
    public static int HEAD_OFFSET = -80;
    public static int FOV_RADIUS = 150;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        ScrollView sc = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0A0A0A"));
        root.setPadding(50, 80, 50, 50);
        sc.addView(root);

        TextView title = new TextView(this);
        title.setText("🎯 HEADSHOT HELPER");
        title.setTextColor(Color.parseColor("#FF3B3B"));
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Non-Root • Accessibility + Overlay");
        sub.setTextColor(Color.parseColor("#888"));
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 10, 0, 30);
        root.addView(sub);

        TextView sensL = new TextView(this);
        sensL.setText("Sensitivity: 1.0x");
        sensL.setTextColor(Color.WHITE);
        root.addView(sensL);
        SeekBar sensBar = new SeekBar(this);
        sensBar.setMax(30);
        sensBar.setProgress(10);
        sensBar.setOnSeekBarChangeListener(new SL(() -> {
            SENS = sensBar.getProgress() / 10f;
            sensL.setText("Sensitivity: " + SENS + "x");
        }));
        root.addView(sensBar);

        TextView smL = new TextView(this);
        smL.setText("Smooth: 60%");
        smL.setTextColor(Color.WHITE);
        root.addView(smL);
        SeekBar smoothBar = new SeekBar(this);
        smoothBar.setMax(100);
        smoothBar.setProgress(60);
        smoothBar.setOnSeekBarChangeListener(new SL(() -> {
            SMOOTH = smoothBar.getProgress() / 100f;
            smL.setText("Smooth: " + smoothBar.getProgress() + "%");
        }));
        root.addView(smoothBar);

        TextView offL = new TextView(this);
        offL.setText("Head Offset: -80px");
        offL.setTextColor(Color.WHITE);
        root.addView(offL);
        SeekBar offBar = new SeekBar(this);
        offBar.setMax(200);
        offBar.setProgress(80);
        offBar.setOnSeekBarChangeListener(new SL(() -> {
            HEAD_OFFSET = -offBar.getProgress();
            offL.setText("Head Offset: " + HEAD_OFFSET + "px");
        }));
        root.addView(offBar);

        TextView fovL = new TextView(this);
        fovL.setText("FOV Radius: 150px");
        fovL.setTextColor(Color.WHITE);
        root.addView(fovL);
        SeekBar fovBar = new SeekBar(this);
        fovBar.setMax(300);
        fovBar.setProgress(150);
        fovBar.setOnSeekBarChangeListener(new SL(() -> {
            FOV_RADIUS = fovBar.getProgress();
            fovL.setText("FOV Radius: " + FOV_RADIUS + "px");
        }));
        root.addView(fovBar);

        Button perm = btn("1. IZIN OVERLAY", "#444");
        perm.setOnClickListener(v ->
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)));
        root.addView(perm);

        Button acc = btn("2. AKTIFKAN AKSESIBILITAS", "#0066CC");
        acc.setOnClickListener(v ->
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(acc);

        Button start = btn("3. MULAI HEADSHOT HELPER", "#FF3B3B");
        start.setOnClickListener(v -> {
            startForegroundService(new Intent(this, OverlayService.class));
            startService(new Intent(this, FloatingPanelService.class));
            Toast.makeText(this, "AKTIF ✅", Toast.LENGTH_SHORT).show();
            moveTaskToBack(true);
        });
        root.addView(start);

        Button stop = btn("MATIKAN", "#222");
        stop.setOnClickListener(v -> {
            stopService(new Intent(this, OverlayService.class));
            stopService(new Intent(this, FloatingPanelService.class));
        });
        root.addView(stop);

        TextView info = new TextView(this);
        info.setText("\nCara pakai:\n" +
            "1. IZIN OVERLAY → allow\n" +
            "2. AKSESIBILITAS → cari 'Headshot Helper' → ON\n" +
            "3. MULAI → buka Free Fire\n" +
            "4. Crosshair muncul otomatis\n" +
            "5. Drag aim → gerakan halus");
        info.setTextColor(Color.parseColor("#888"));
        info.setTextSize(13);
        root.addView(info);

        setContentView(sc);
    }

    private Button btn(String t, String c) {
        Button b = new Button(this);
        b.setText(t);
        b.setBackgroundColor(Color.parseColor(c));
        b.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 15, 0, 0);
        b.setLayoutParams(lp);
        return b;
    }

    static class SL implements SeekBar.OnSeekBarChangeListener {
        private final Runnable r;
        SL(Runnable r) { this.r = r; }
        public void onProgressChanged(SeekBar s, int p, boolean f) { r.run(); }
        public void onStartTrackingTouch(SeekBar s) {}
        public void onStopTrackingTouch(SeekBar s) {}
    }
}
