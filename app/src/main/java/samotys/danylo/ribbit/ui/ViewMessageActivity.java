package samotys.danylo.ribbit.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import samotys.danylo.ribbit.R;


public class ViewMessageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        TextView textView = (TextView) findViewById(R.id.textView);

        String message = getIntent().getStringExtra("message");

        textView.setText(message);
    }
}
