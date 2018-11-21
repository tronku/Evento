package tronku.dsc.eventmanager;

import android.animation.Animator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    @BindView(R.id.eventLogo)ImageView eventoLogo;
    @BindView(R.id.eventoText)ImageView eventoText;
    @BindView(R.id.madeBy)LinearLayout madeBy;

    private Animation imgAnim, textAnim, madeAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        ButterKnife.bind(this);

        imgAnim = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade);
        textAnim = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade);
        madeAnim = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade);
        eventoLogo.startAnimation(imgAnim);
        madeBy.startAnimation(madeAnim);
        eventoText.startAnimation(textAnim);

        final Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try{
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };

        thread.start();
    }

}
