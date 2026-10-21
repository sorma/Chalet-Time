package com.francesco.chalettime.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import com.francesco.chalettime.R;
import com.francesco.chalettime.ui.home.viewmodel.MainViewModel;
import com.francesco.chalettime.ui.welcome.WelcomeActivity;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        MainViewModel model = new ViewModelProvider(this).get(MainViewModel.class);
        if (!model.hasSession()) {
            startActivity(new Intent(this, WelcomeActivity.class)); finish(); return;
        }
        setContentView(R.layout.activity_main);
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        model.getRole().observe(this, role -> {
            // A restored controller already owns its graph and back stack.
            if (host.getNavController().getCurrentDestination() != null) return;
            NavGraph graph = host.getNavController().getNavInflater().inflate(R.navigation.home_nav_graph);
            graph.setStartDestination("admin".equals(role) ? R.id.adminFragment : R.id.homeFragment);
            host.getNavController().setGraph(graph);
        });
        model.getLoading().observe(this, loading -> findViewById(R.id.progress).setVisibility(loading ? View.VISIBLE : View.GONE));
        model.getMessages().observe(this, event -> {
            String message = event.consume();
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, WelcomeActivity.class)); finish();
            }
        });
        model.loadRole();
    }
}
