package e.aman.lockdemo.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import e.aman.lockdemo.Views.PatternLockScreenActivity;

public class LockScreenReceiver extends BroadcastReceiver          //Main broadcast receiver that checks for boot completed
                                                                     //or on/off display.
{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //If the screen was just turned on or it just booted up, start your Lock Activity
        if(action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent i = new Intent(context, PatternLockScreenActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
