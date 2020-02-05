package e.aman.lockdemo.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import e.aman.lockdemo.Views.PatternLock.PatternLockScreenActivity;
import e.aman.lockdemo.Views.PinLock.PinLockScreenActivity;

public class LockScreenReceiver extends BroadcastReceiver          //Main broadcast receiver that checks for boot completed
                                                                     //or on/off display.
{
    String status ;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        status = pref.getString("status", null);
        if(status.equals("lock_pin"))
        {
            //If the screen was just turned on or it just booted up, start your Lock Activity
            if(action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_BOOT_COMPLETED))
            {
                Intent i = new Intent(context, PinLockScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
        else if(status.equals("lock_pattern"))
        {

            //If the screen was just turned on or it just booted up, start your Lock Activity
            if(action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_BOOT_COMPLETED))
            {
                Intent i = new Intent(context, PatternLockScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }


    }
}
