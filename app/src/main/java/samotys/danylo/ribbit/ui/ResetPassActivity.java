package samotys.danylo.ribbit.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import samotys.danylo.ribbit.R;


public class ResetPassActivity extends ActionBarActivity {

    @InjectView(R.id.emailResetField) EditText mEmailField;
    @InjectView(R.id.resetPassButton)Button mResetButton;
    @InjectView(R.id.cancelButton) Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ButterKnife.inject(this);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = mEmailField.getText().toString();
                ParseUser.requestPasswordResetInBackground(mEmail,
                        new RequestPasswordResetCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassActivity.this)
                                            .setMessage(e.getMessage())
                                            .setTitle(R.string.error_dialod_title)
                                            .setPositiveButton(android.R.string.ok, null);

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        });
            }
        });

    }
}
