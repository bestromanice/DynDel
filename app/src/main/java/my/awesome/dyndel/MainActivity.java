package my.awesome.dyndel;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.Group;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends BaseSplitActivity {

    private SplitInstallManager splitInstallManager;

    private Group progressGroup;
    private Group buttonsGroup;
    private ProgressBar progressBar;
    private TextView progressTextView;
    private Button dynamicFeature1Button;
    private Button dynamicFeature2Button;
    private ListView installedModulesListView;

    private String dynamicFeature1;
    private String dynamicFeature2;
    private final String DYNAMIC_FEATURE_1_SAMPLE_CLASSNAME = "my.awesome.dynamicfeature1.DynamicActivity1";
    private final String DYNAMIC_FEATURE_2_SAMPLE_CLASSNAME = "my.awesome.dynamicfeature2.DynamicActivity2";
    private final static String FILE_NAME = "exceptionText.txt";
    public static String PACKAGE_NAME;

    private Context context;

    // Creates a listener for request status updates.
    SplitInstallStateUpdatedListener listener = state -> {
        List<String> modNames = state.moduleNames();
        String separatedModuleNames  = TextUtils.join(" - ", modNames);

        switch (state.status()) {
            case SplitInstallSessionStatus.DOWNLOADING:
                displayLoadingState(state, getString(R.string.downloading, separatedModuleNames));
                break;

            case SplitInstallSessionStatus.INSTALLED:
            case SplitInstallSessionStatus.DOWNLOADED:
                onSuccessfulLoad(separatedModuleNames);
                break;

            case SplitInstallSessionStatus.INSTALLING:
                displayLoadingState(state, getString(R.string.installing, separatedModuleNames));
                break;
            case SplitInstallSessionStatus.FAILED:
                toastAndLog(getString(R.string.error_for_module,
                        state.errorCode(),
                        state.moduleNames()));
                break;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);

        dynamicFeature1 = getString(R.string.title_dynamicfeature1);
        dynamicFeature2 = getString(R.string.title_dynamicfeature2);

        context = getApplicationContext();
        PACKAGE_NAME = getApplicationContext().getPackageName();

        // Creates an instance of SplitInstallManager.
        splitInstallManager = SplitInstallManagerFactory.create(context);
//        splitInstallManager.registerListener(listener);

        initializeViews();
        updateListOfInstalledModules();

        dynamicFeature1Button.setOnClickListener(v -> {
            loadAndLaunchModule(dynamicFeature1);
        });

        dynamicFeature2Button.setOnClickListener(v -> {
            loadAndLaunchModule(dynamicFeature2);
        });
    }

    @Override
    protected void onResume() {
        // Listener can be registered even without directly triggering a download.
        splitInstallManager.registerListener(listener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Make sure to dispose of the listener once it's no longer needed.
        splitInstallManager.unregisterListener(listener);
        super.onPause();
    }

    /**
     * Load a feature by module name
     * @param name The name of the feature module to load
     */
    private void loadAndLaunchModule(String name) {

        updateProgressMessage(getString(R.string.loading_module, name));

        // Skip loading if the module already is installed. Perform success action directly.
        if (splitInstallManager.getInstalledModules().contains(name)) {
            updateProgressMessage(getString(R.string.already_installed));
            onSuccessfulLoad(name);
            return;
        }

        // Create request to install a feature module by name
        SplitInstallRequest request = SplitInstallRequest.newBuilder()
                .addModule(name)
                .build();

        List<String> deferredNames = null;
        deferredNames.add(name);
        // Load and install the requested feature module.
//        splitInstallManager.startInstall(request);
        splitInstallManager.deferredInstall(deferredNames);

        updateProgressMessage(getString(R.string.starting_install_for, name));
        updateListOfInstalledModules();

        //
        // TODO success and failure listeners
        //

//        splitInstallManager.startInstall(request)
//                .addOnSuccessListener(sessionId -> {
//                    updateListOfInstalledModules();
//                    onSuccessfulLoad(name);
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(MainActivity.this,
//                            "Module download failed:\n" + e.getMessage(),
//                            Toast.LENGTH_LONG).show();
//                    FileOutputStream fos = null;
//                    try {
//                        String exceptionText = Objects.requireNonNull(e.getMessage());
//                        exceptionText += "\n";
//                        fos = openFileOutput(FILE_NAME, MODE_APPEND);
//                        fos.write(exceptionText.getBytes());
//                        Toast.makeText(MainActivity.this,
//                                "Log in exceptionText.txt",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                    catch (IOException ex) {
//                        Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                    finally {
//                        try {
//                            if (fos != null)
//                                fos.close();
//                        }
//                        catch (IOException ex) {
//                            Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
        }

    /**
     * Define what to do once a feature module is loaded successfully
     * @param moduleName The name of the successfully loaded module
     */
    private void onSuccessfulLoad(String moduleName) {

        if (Objects.equals(moduleName, dynamicFeature1)) {
            launchActivity(DYNAMIC_FEATURE_1_SAMPLE_CLASSNAME);
            displayButtons();
            return;
        }
        if (Objects.equals(moduleName, dynamicFeature2)) {
            launchActivity(DYNAMIC_FEATURE_2_SAMPLE_CLASSNAME);
            displayButtons();
            return;
        }
//        displayButtons();
    }

    /** Launch an activity by its class name */
    private void launchActivity(String className) {

        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, className);
        startActivity(intent);
        return;
    }

    /** Display a loading state to the user */
    private void displayLoadingState(SplitInstallSessionState state, String message) {

        displayProgress();

        progressBar.setMax(((int) state.totalBytesToDownload()));
        progressBar.setProgress(((int) state.bytesDownloaded()));

        updateProgressMessage(message);
    }

    /** Set up all view variables */
    private void initializeViews() {

        buttonsGroup = findViewById(R.id.buttons_group);
        progressGroup = findViewById(R.id.progress_group);
        progressBar = findViewById(R.id.progressbar);
        progressTextView = findViewById(R.id.progress_textview);
        installedModulesListView = findViewById(R.id.installed_modules_listview);
        dynamicFeature1Button = findViewById(R.id.dynamic_feature_1_button);
        dynamicFeature2Button = findViewById(R.id.dynamic_feature_2_button);
    }

    /** Set up all view variables */
    private void updateProgressMessage(String message) {

        if (progressBar.getVisibility() != View.VISIBLE) {
            displayProgress();
        }
        progressTextView.setText(message);
    }

    /** Display progress bar and text */
    private void displayProgress() {

        progressGroup.setVisibility(View.VISIBLE);
        installedModulesListView.setVisibility(View.GONE);
        buttonsGroup.setVisibility(View.GONE);
    }

    /** Display list of installed modules and buttons to accept user input */
    private void displayButtons() {
        progressGroup.setVisibility(View.GONE);
        installedModulesListView.setVisibility(View.VISIBLE);
        buttonsGroup.setVisibility(View.VISIBLE);
        updateListOfInstalledModules();
    }

    private void updateListOfInstalledModules() {

        // Takes all installed modules.
        Set<String> installedModules = splitInstallManager.getInstalledModules();

        ArrayAdapter<String> adapter = new ArrayAdapter(MainActivity.this,
                android.R.layout.simple_list_item_1, Arrays.asList(installedModules.toArray()));
        installedModulesListView.setAdapter(adapter);
    }

    public void toastAndLog(String text) {

        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
        Log.d(TAG, text);
    }
}