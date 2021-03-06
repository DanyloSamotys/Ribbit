package samotys.danylo.ribbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

import samotys.danylo.ribbit.utils.ParseConstants;
import samotys.danylo.ribbit.R;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static android.text.format.DateUtils.getRelativeTimeSpanString;

/**
 * Created by Danylo on 28.03.15.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    private List<ParseObject> mMessages;
    private String mTime;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView)convertView.findViewById(R.id.messageIcon);
            holder.textView = (TextView)convertView.findViewById(R.id.senderLabel);
            holder.timeView = (TextView)convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject messages = mMessages.get(position);
        if (messages.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TEXT_TYPE)){
            holder.imageView.setImageResource(R.drawable.ic_action_5_content_unread);
        }
        else if (messages.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.IMAGE_TYPE)){
            holder.imageView.setImageResource(R.drawable.ic_picture);
        }
        else {
            holder.imageView.setImageResource(R.drawable.ic_video);
        }
        
        mTime = getRelativeTimeSpanString(messages.getCreatedAt().getTime(),
                new Date().getTime(),
                SECOND_IN_MILLIS).toString();

        holder.textView.setText(messages.getString(ParseConstants.KEY_SENDER_NAME));
        holder.timeView.setText(mTime);

        return convertView;
    }

    public void refill(List<ParseObject> messages){
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
        TextView timeView;
    }
}
