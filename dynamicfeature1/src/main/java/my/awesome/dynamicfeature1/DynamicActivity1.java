package my.awesome.dynamicfeature1;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.splitcompat.SplitCompat;

public class DynamicActivity1 extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Emulates installation of on demand modules using SplitCompat.
        SplitCompat.installActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic1);
    }

}