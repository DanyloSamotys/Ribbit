package samotys.danylo.ribbit.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import samotys.danylo.ribbit.R;


public class ViewMessageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        TextView textView = (TextView) findViewById(R.id.textView);

        String message = getIntent().getStringExtra("message");

        textView.setText(message);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        },10*1000);
    }
}
