package org.chernatkin.android.sqldeveloper.expandablelist;

import java.util.List;
import java.util.Map;

import org.chernatkin.android.sqldeveloper.R;
import org.chernatkin.android.sqldeveloper.R.layout;
import org.chernatkin.android.sqldeveloper.utils.DialogUtils;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class EventExpandableListAdapter extends BaseExpandableListAdapter {

	private final Map<ExpandableListItem, List<ExpandableListItem>> tree;
	
	private final Context context;
	
	public EventExpandableListAdapter(final Context context, final Map<ExpandableListItem, List<ExpandableListItem>> tree) {
		this.tree = tree;
		this.context = context;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return getByIndex(groupPosition).getValue().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final TextView view = (TextView)((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.group_child_item, null);
		final ExpandableListItem item = (ExpandableListItem)getChild(groupPosition, childPosition);
		
		view.setText(Html.fromHtml(item.getText()));
		if(item.getOnClickListener() != null){
			view.setOnClickListener(item.getOnClickListener());
		}
		
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return getByIndex(groupPosition).getValue().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return getByIndex(groupPosition).getKey();
	}

	@Override
	public int getGroupCount() {
		return tree.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		final TextView view = (TextView)((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.group_expandable_item, null);
		final ExpandableListItem item = (ExpandableListItem)getGroup(groupPosition);
		
		view.setText(Html.fromHtml(item.getText()));
		if(item.getOnClickListener() != null){
			view.setOnClickListener(item.getOnClickListener());
		}
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	protected Map.Entry<ExpandableListItem, List<ExpandableListItem>> getByIndex(final int index){
		int i = 0;
		for(Map.Entry<ExpandableListItem, List<ExpandableListItem>> entry : tree.entrySet()){
			if(i == index){ return entry; }
			i++;
		}
		return null;
	}
}