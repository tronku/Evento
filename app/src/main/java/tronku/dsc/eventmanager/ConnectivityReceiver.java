package tronku.dsc.eventmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;


public class ConnectivityReceiver extends BroadcastReceiver {

    private boolean noConnectivity;
    private Snackbar snackbar;

    public void setSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (noConnectivity)
                snackbar.show();
            else
                snackbar.dismiss();
        }
    }

    public boolean isConnected() {
        return !noConnectivity;
    }

}
