package com.friendly.walking.util;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.friendly.walking.R;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;

import java.util.List;

/**
 * Sample adapter implementation extending from AsymmetricGridViewAdapter<DemoItem> This is the
 * easiest way to get started.
 */
public class DefaultListAdapter extends ArrayAdapter<WalkingShareItem> implements WalkingShareItemAdapter {

  private final LayoutInflater layoutInflater;
  private Context context;
  private Location location;

  public DefaultListAdapter(Context context, List<WalkingShareItem> items) {
    super(context, 0, items);
    this.context = context;
    layoutInflater = LayoutInflater.from(context);

      location = new Location("my");
      location.setLatitude(37.218288);
      location.setLongitude(127.057187);
  }

  public DefaultListAdapter(Context context) {
    super(context, 0);
    this.context = context;
    layoutInflater = LayoutInflater.from(context);

      location = new Location("my");
      location.setLatitude(37.218288);
      location.setLongitude(127.057187);
  }

  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    View v;
    final WalkingShareItem item = getItem(position);

    if (convertView == null) {
        v = layoutInflater.inflate(R.layout.share_grid_adapter_item, parent, false);
    } else {
        v = convertView;
    }

    TextView nickName = (TextView) v.findViewById(R.id.nickname_text);
    TextView distance = (TextView) v.findViewById(R.id.distance_text);
    final ImageView imageView = v.findViewById(R.id.circle_image);

    nickName.setText(String.valueOf(item.getNickName()));

    Location people = item.getLocation();
    float diff = location.distanceTo(people);
    JWLog.e("distance :"+diff);

    String distanceText;

    if(diff / 1000 >= 1) {
        distanceText = String.format("%.2f km", diff/1000);
    } else {
        distanceText = String.format("%d m", (int)diff);
    }
    distance.setText(distanceText);

    if(item.getImageUrl() == null) {
        FireBaseNetworkManager.getInstance(getContext()).downloadImageForUri(item.getEmail(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                if (result) {
                    if (object != null) {
                        item.setImageUrl((String) object);
                        Glide.with(context).load(object).into(imageView);
                    }
                }
            }
        });
    } else {
        Glide.with(context).load(item.getImageUrl()).into(imageView);
    }
    return v;
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  @Override
  public int getItemViewType(int position) {
    return position % 2 == 0 ? 1 : 0;
  }

  public void appendItems(List<WalkingShareItem> newItems) {
    addAll(newItems);
    notifyDataSetChanged();
  }

  public void setItems(List<WalkingShareItem> moreItems) {
    clear();
    appendItems(moreItems);
  }
}