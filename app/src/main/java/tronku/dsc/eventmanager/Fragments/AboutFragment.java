package tronku.dsc.eventmanager.Fragments;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import tronku.dsc.eventmanager.BuildConfig;
import tronku.dsc.eventmanager.R;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    private TableRow facebook, web;
    private View view;
    private TextView versionName;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about, container, false);
        facebook = view.findViewById(R.id.facebook);
        web = view.findViewById(R.id.dscweb);
        versionName = view.findViewById(R.id.versionName);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fb = new Intent();
                fb.setAction(Intent.ACTION_VIEW);
                fb.setData(Uri.parse("http://www.facebook.com/dscjssnoida"));
                startActivity(fb);
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent web = new Intent();
                web.setAction(Intent.ACTION_VIEW);
                web.setData(Uri.parse("http://dscjss.in"));
                startActivity(web);
            }
        });

        String version = "v." + BuildConfig.VERSION_NAME;
        versionName.setText(version);

        return view;
    }

}
