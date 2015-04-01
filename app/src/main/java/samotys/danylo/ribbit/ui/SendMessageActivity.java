package samotys.danylo.ribbit.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import samotys.danylo.ribbit.utils.ParseConstants;
import samotys.danylo.ribbit.R;

public class SendMessageActivity extends ActionBarActivity {

    @InjectView(R.id.messageField) EditText mEditText;
    @InjectView(R.id.chooseButton) Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        ButterKnife.inject(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText.getText() != null){
                    Intent intent = new Intent(SendMessageActivity.this, RecipientsActivity.class);
                    intent.putExtra("message", mEditText.getText().toString());
                    intent.putExtra(ParseConstants.KEY_FILE_TYPE, ParseConstants.TEXT_TYPE);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(SendMessageActivity.this, "Please enter a message!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
