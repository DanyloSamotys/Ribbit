package samotys.danylo.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import samotys.danylo.ribbit.adapters.UserAdapter;
import samotys.danylo.ribbit.utils.FileHelper;
import samotys.danylo.ribbit.utils.ParseConstants;
import samotys.danylo.ribbit.R;


public class RecipientsActivity extends ActionBarActivity {

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;

    @InjectView(R.id.friendsGrid) GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);
        ButterKnife.inject(this);

        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView textView = (TextView)findViewById(android.R.id.empty);
        mGridView.setEmptyView(textView);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getStringExtra(ParseConstants.KEY_FILE_TYPE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }

                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this)
                            .setMessage(e.getMessage())
                            .setTitle(R.string.error_dialod_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            ParseObject message = createMessage();
            if (message == null){
                //error
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error").setMessage("Try again!").setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                sendMessage(message);
                finish();
                if (mFileType.equals(ParseConstants.TEXT_TYPE)){
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void sendMessage(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    //success
                    Toast.makeText(RecipientsActivity.this, "Message sent!", Toast.LENGTH_LONG).show();
                    sendPushNotifications();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setTitle("Error").setMessage("Try again!").setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void sendPushNotifications() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientIds());

        //send push notification
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message, ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();
    }

    protected ParseObject createMessage(){
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        if (mFileType.equals(ParseConstants.TEXT_TYPE)){
            message.put(ParseConstants.KEY_MESSAGE, getIntent().getStringExtra("message"));
            return message;
        }
        else {

            byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

            if (fileBytes == null) {
                return null;
            } else {
                if (mFileType.equals(ParseConstants.IMAGE_TYPE)) {
                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                }

                String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
                ParseFile file = new ParseFile(fileName, fileBytes);
                message.put(ParseConstants.KEY_FILE, file);

                return message;
            }
        }
    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mGridView.getCount(); i++){
            if (mGridView.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if (mGridView.getCheckedItemCount() > 0) {
                mSendMenuItem.setVisible(true);
            } else {
                mSendMenuItem.setVisible(false);
            }

            if (mGridView.isItemChecked(position)) {
                //add a recipient
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                //remove recipient
                checkImageView.setVisibility(View.INVISIBLE );
            }
        }
    };
}
