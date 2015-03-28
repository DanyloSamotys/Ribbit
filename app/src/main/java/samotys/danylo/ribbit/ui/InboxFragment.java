package samotys.danylo.ribbit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import samotys.danylo.ribbit.MessageAdapter;
import samotys.danylo.ribbit.ParseConstants;
import samotys.danylo.ribbit.R;

/**
 * Created by Danylo on 22.03.15.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if (e == null){
                    //We have messages!
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                    setListAdapter(adapter);
                }
            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);


        if (messageType.equals(ParseConstants.IMAGE_TYPE)){
            //Show image
            ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
            Uri fileUri = Uri.parse(file.getUrl());

            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        }
        else if (messageType.equals(ParseConstants.TEXT_TYPE)){
            //Show text
            Intent intent = new Intent(getActivity(), ViewMessageActivity.class);
            intent.putExtra("message", message.getString(ParseConstants.KEY_MESSAGE));
            startActivity(intent);
        }
        else{
            //Show video
            ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
            Uri fileUri = Uri.parse(file.getUrl());

            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }
    }
}
