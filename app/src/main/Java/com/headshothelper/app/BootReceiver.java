package com.headshothelper.app;

import android.content.*;
import android.os.Bundle;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) {
            c.startService(new Intent(c, OverlayService.class));
        }
    }
}
