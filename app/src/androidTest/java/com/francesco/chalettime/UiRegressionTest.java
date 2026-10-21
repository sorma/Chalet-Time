package com.francesco.chalettime;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.francesco.chalettime.ui.welcome.WelcomeActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.io.FileOutputStream;
import androidx.test.platform.app.InstrumentationRegistry;
import static org.junit.Assert.*;

/** Navigation and native layout checks; no account or Firestore write is performed. */
@RunWith(AndroidJUnit4.class)
public class UiRegressionTest {
    @Test public void welcomeNavigationAndRecreationPreserveTheScreen() {
        try (ActivityScenario<WelcomeActivity> scenario = ActivityScenario.launch(WelcomeActivity.class)) {
            scenario.onActivity(activity -> {
                assertNotNull(activity.findViewById(R.id.login_button));
                activity.findViewById(R.id.textButton).performClick();
            });
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> assertNotNull(activity.findViewById(R.id.register_button)));
            scenario.recreate();
            scenario.onActivity(activity -> {
                assertNotNull(activity.findViewById(R.id.register_button));
                activity.getOnBackPressedDispatcher().onBackPressed();
            });
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> {
                assertNotNull(activity.findViewById(R.id.login_button));
                activity.findViewById(R.id.login_button).performClick();
                assertEquals("Inserisci l’e-mail", ((TextView)activity.findViewById(R.id.e_mail)).getError().toString());
            });
        }
    }

    @Test public void allScreensInflateInLightAndDarkOnSmallAndStandardPhones() {
        int[] layouts = {R.layout.fragment_login, R.layout.fragment_register, R.layout.fragment_home,
                R.layout.fragment_admin, R.layout.fragment_user_details, R.layout.dialog_hours_body};
        String[] names = {"login", "register", "home", "admin", "details", "hours"};
        try (ActivityScenario<WelcomeActivity> scenario = ActivityScenario.launch(WelcomeActivity.class)) {
            for (boolean dark : new boolean[]{false, true}) for (int widthDp : new int[]{320, 411}) {
                for (int index = 0; index < layouts.length; index++) {
                    final int layout = layouts[index];
                    final String name = names[index];
                    final View[] rendered = new View[1];
                    scenario.onActivity(activity -> {
                        Configuration config = new Configuration(activity.getResources().getConfiguration());
                        config.uiMode = (config.uiMode & ~Configuration.UI_MODE_NIGHT_MASK)
                                | (dark ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO);
                        ContextThemeWrapper context = new ContextThemeWrapper(activity.createConfigurationContext(config), R.style.Base_Theme_ChaletTime);
                        float density = context.getResources().getDisplayMetrics().density;
                        View root = LayoutInflater.from(activity).cloneInContext(context).inflate(layout, null);
                        rendered[0] = root;
                        // Attach to a real window: Material fields reserve icon spacing on a later frame.
                        activity.setContentView(root, new ViewGroup.LayoutParams(Math.round(widthDp * density), Math.round(800 * density)));
                        if (layout == R.layout.fragment_user_details) {
                            ((TextView)root.findViewById(R.id.name)).setText("Ada Rossi");
                            ((TextView)root.findViewById(R.id.birthday)).setText("12/05/1990");
                            ((TextView)root.findViewById(R.id.monthLabel)).setText("Ottobre");
                        }
                    });
                    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
                    scenario.onActivity(activity -> {
                        View root = rendered[0];
                        float density = root.getResources().getDisplayMetrics().density;
                        int width = Math.round(widthDp * density), height = Math.round(800 * density);
                        root.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
                        root.layout(0, 0, width, height);
                        assertEquals(width, root.getMeasuredWidth());
                        if (layout == R.layout.fragment_login) {
                            TextView email = root.findViewById(R.id.e_mail);
                            assertTrue("Email text must clear the start icon", email.getCompoundPaddingStart() >= 40 * density);
                        }
                        if (layout == R.layout.fragment_admin || layout == R.layout.fragment_user_details)
                            assertTrue(root.findViewById(R.id.recyclerView).getMeasuredHeight() > 0);
                        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        root.draw(new Canvas(image));
                        File output = new File(activity.getCacheDir(), "preview-" + name + "-" + widthDp + (dark ? "-dark" : "-light") + ".png");
                        try (FileOutputStream stream = new FileOutputStream(output)) { image.compress(Bitmap.CompressFormat.PNG, 100, stream); }
                        catch (java.io.IOException error) { throw new AssertionError(error); }
                        image.recycle();
                    });
                }
            }
        }
    }
}
