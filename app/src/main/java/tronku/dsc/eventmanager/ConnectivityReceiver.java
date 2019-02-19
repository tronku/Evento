package tronku.dsc.eventmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.view.View;

public class ConnectivityReceiver extends BroadcastReceiver {

    private View view;
    private boolean noConnectivity;

    public ConnectivityReceiver(View view) {
        super();
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            Snackbar snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(context.getResources().getColor(R.color.red));

            if (noConnectivity)
                snackbar.show();
        }
    }

    public boolean isConnected() {
        return !noConnectivity;
    }

}
