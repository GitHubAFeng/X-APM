package github.tornaco.xposedmoduletest.ui.activity.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import github.tornaco.android.common.util.ColorUtil;
import github.tornaco.xposedmoduletest.R;
import github.tornaco.xposedmoduletest.cache.RunningServicesLoadingCache;
import github.tornaco.xposedmoduletest.loader.PaletteColorPicker;
import github.tornaco.xposedmoduletest.provider.XSettings;
import github.tornaco.xposedmoduletest.ui.activity.BaseActivity;

/**
 * Created by Tornaco on 2018/5/2 15:05.
 * God bless no bug!
 */
public class RunningServicesDetailsActivity extends BaseActivity {

    public static void start(Context context, Bundle args) {
        Intent starter = new Intent(context, RunningServicesDetailsActivity.class);
        starter.putExtra("args", args);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_with_appbar_template);

        setupToolbar();
        showHomeAsUp();

        if (RunningServicesLoadingCache.getInstance().getMergedItem() != null) {
            PackageManager pm = getPackageManager();
            RunningState.MergedItem mergedItem = RunningServicesLoadingCache.getInstance().getMergedItem();
            if (mergedItem.mPackageInfo == null) {
                // Items for background processes don't normally load
                // their labels for performance reasons.  Do it now.
                if (mergedItem.mProcess != null) {
                    mergedItem.mProcess.ensureLabel(pm);
                    mergedItem.mPackageInfo = mergedItem.mProcess.mPackageInfo;
                    mergedItem.mDisplayLabel = mergedItem.mProcess.mDisplayLabel;
                }
            }

            setTitle(String.valueOf(mergedItem.mDisplayLabel));

            // Apply theme color.
            if (false && mergedItem.mPackageInfo != null && mergedItem.mPackageInfo.packageName != null) {
                int color = ContextCompat.getColor(this, XSettings.getThemes(this).getThemeColor());
                PaletteColorPicker.pickPrimaryColor(this, new PaletteColorPicker.PickReceiver() {
                    @Override
                    public void onColorReady(int color) {
                        applyColor(color);
                    }
                }, mergedItem.mPackageInfo.packageName, color);
            }
        }

        Bundle args = getIntent().getBundleExtra("args");
        RunningServiceDetails details = new RunningServiceDetails();
        details.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.container, details)
                .commitAllowingStateLoss();
    }

    @SuppressWarnings("ConstantConditions")
    private void applyColor(int color) {
        AppBarLayout appBar = findViewById(R.id.appbar);
        if (appBar != null) appBar.setBackgroundColor(color);
        int dark = ColorUtil.colorBurn(color);
        getWindow().setStatusBarColor(dark);
        getWindow().setNavigationBarColor(dark);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(color);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}