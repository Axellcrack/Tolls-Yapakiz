package com.headshothelper.app;

import android.app.*;
import android.content.Intent;
import android.graphics.*;
import android.os.Build;
import android.os.IBinder;
import android.view.*;

public class OverlayService extends Service {

    private View overlay;
    private WindowManager wm;
    private float lastX, lastY;
    private boolean dragging = false;

    @Override public IBinder onBind(Intent i) { return null; }

    @Override
    public int onStartCommand(Intent i, int f, int s) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(
                "hs", "Headshot Helper",
                NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
            Notification n = new Notification.Builder(this, "hs")
                .setContentTitle("Headshot Helper Aktif")
                .setContentText("Non-root smooth touch ✅")
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .build();
            startForeground(1, n);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        overlay = new View(this) {
            private Paint p = new Paint();

            @Override
            protected void onDraw(Canvas c) {
                super.onDraw(c);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.parseColor("#00FFAA55"));
                p.setStrokeWidth(2);
                c.drawCircle(cx, cy, MainActivity.FOV_RADIUS, p);

                p.setColor(Color.parseColor("#FF3B3B"));
                p.setStrokeWidth(3);
                int gap = 8, len = 30;
                c.drawLine(cx, cy - gap - len, cx, cy - gap, p);
                c.drawLine(cx, cy + gap, cx, cy + gap + len, p);
                c.drawLine(cx - gap - len, cy, cx - gap, cy, p);
                c.drawLine(cx + gap, cy, cx + gap + len, cy, p);
                c.drawCircle(cx, cy, 3, p);

                p.setColor(Color.parseColor("#FFAA00"));
                p.setStyle(Paint.Style.STROKE);
                int hy = cy + MainActivity.HEAD_OFFSET;
                c.drawCircle(cx, hy, 25, p);
                p.setStyle(Paint.Style.FILL);
                p.setTextSize(24);
                c.drawText("HEAD", cx - 32, hy + 8, p);

                p.setColor(Color.parseColor("#00FFAA44"));
                p.setStrokeWidth(1);
                c.drawLine(cx, 0, cx, getHeight(), p);
                c.drawLine(0, cy, getWidth(), cy, p);
            }
        };

        overlay.setOnTouchListener((v, e) -> {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = e.getRawX();
                    lastY = e.getRawY();
                    dragging = true;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (!dragging) return false;
                    float rx = e.getRawX();
                    float ry = e.getRawY();
                    float dx = (rx - lastX) * MainActivity.SENS;
                    float dy = (ry - lastY) * MainActivity.SENS;

                    TouchAccessibilityService svc =
                        TouchAccessibilityService.getInstance();
                    if (svc != null) {
                        svc.smoothDrag(rx, ry, dx, dy,
                            MainActivity.SMOOTH, 16);
                    }

                    lastX = rx;
                    lastY = ry;
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    dragging = false;
                    return true;
            }
            return false;
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            -1, -1,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
              | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
              | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
              | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT);
        wm.addView(overlay, lp);
    }

    @Override
    public void onDestroy() {
        if (overlay != null) wm.removeView(overlay);
        super.onDestroy();
    }
}
