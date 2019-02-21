package tronku.dsc.eventmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import tronku.dsc.eventmanager.Fragments.PastEventsFragment;
import tronku.dsc.eventmanager.Fragments.UpcomingEventsFragment;
import tronku.dsc.eventmanager.POJO.Society;

public class ConnectivityReceiverEvents extends BroadcastReceiver {

    private boolean noConnectivity;
    private UpcomingEventsFragment upcomingEventsFragment = null;
    private PastEventsFragment pastEventsFragment = null;
    private SocietyFilterActivity societyFilterActivity = null;
    private boolean upcoming;
    private boolean hasExtra;
    private boolean society = false;
    private Snackbar snackbar;

    public ConnectivityReceiverEvents(Fragment fragment, String fragName, boolean hasExtra, Snackbar snackbar) {
        if (fragName.equals("upcoming")) {
            upcomingEventsFragment = (UpcomingEventsFragment) fragment;
            upcoming = true;
        }
        else if (fragName.equals("past")) {
            pastEventsFragment = (PastEventsFragment) fragment;
            upcoming = false;
        }

        this.hasExtra = hasExtra;
        this.snackbar = snackbar;
    }

    public ConnectivityReceiverEvents (SocietyFilterActivity activity, Snackbar snackbar) {
        societyFilterActivity = activity;
        society = true;
        this.snackbar = snackbar;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (noConnectivity) {
                snackbar.show();
            }

            else {
                snackbar.dismiss();
                if (society)
                    societyFilterActivity.fillData();
                else {
                    if (upcoming)
                        upcomingEventsFragment.updateEvents(hasExtra);
                    else
                        pastEventsFragment.updateEvents(hasExtra);
                }
            }
        }
    }

    public boolean isConnected() {
        return !noConnectivity;
    }

}
