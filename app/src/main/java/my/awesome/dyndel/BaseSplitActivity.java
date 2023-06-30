package my.awesome.dyndel;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.play.core.splitcompat.SplitCompat;

public class BaseSplitActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Emulates installation of on demand modules using SplitCompat.
        SplitCompat.installActivity(this);
    }
}
