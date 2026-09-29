package com.headshothelper.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.*;
import android.widget.*;

public class FloatingPanelService extends Service {
    private View panel;
    private WindowManager wm;

    @Override public IBinder onBind(Intent i) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.parseColor("#CC0A0A0A"));
        ll.setPadding(20, 20, 20, 20);

        TextView t = new TextView(this);
        t.setText("🎯");
        t.setTextSize(28);
        ll.addView(t);

        Button onOff = new Button(this);
        onOff.setText("ON/OFF");
        onOff.setOnClickListener(v ->
            startService(new Intent(this, OverlayService.class)));
        ll.addView(onOff);

        panel = ll;

        panel.setOnTouchListener(new View.OnTouchListener() {
            float ix, iy, px, py;
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ix = e.getRawX();
                        iy = e.getRawY();
                        WindowManager.LayoutParams lp =
                            (WindowManager.LayoutParams) v.getLayoutParams();
                        px = lp.x;
                        py = lp.y;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        WindowManager.LayoutParams lp =
                            (WindowManager.LayoutParams) v.getLayoutParams();
                        lp.x = (int)(px + (e.getRawX() - ix));
                        lp.y = (int)(py + (e.getRawY() - iy));
                        wm.updateViewLayout(v, lp);
                        return true;
                }
                return false;
            }
        });

        int type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.x = 50;
        lp.y = 200;
        wm.addView(panel, lp);
    }

    @Override
    public void onDestroy() {
        if (panel != null) wm.removeView(panel);
        super.onDestroy();
    }
}
