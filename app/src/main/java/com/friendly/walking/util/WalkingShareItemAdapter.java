package com.friendly.walking.util;

import android.widget.ListAdapter;

import java.util.List;

public interface WalkingShareItemAdapter extends ListAdapter {

  void appendItems(List<WalkingShareItem> newItems);

  void setItems(List<WalkingShareItem> moreItems);
}
