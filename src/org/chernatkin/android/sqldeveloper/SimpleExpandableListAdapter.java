package org.chernatkin.android.sqldeveloper;

import java.util.List;
import java.util.Map;

import org.chernatkin.android.sqldeveloper.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class SimpleExpandableListAdapter extends BaseExpandableListAdapter {

	private final List<Map.Entry<String, List<String>>> tree;
	
	private final Context context;
	
	public SimpleExpandableListAdapter(final Context context, final List<Map.Entry<String, List<String>>> tree) {
		this.tree = tree;
		this.context = context;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return tree.get(groupPosition).getValue().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final TextView view = (TextView)((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.group_child_item, null);
		view.setText(getChild(groupPosition, childPosition).toString());
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return tree.get(groupPosition).getValue().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return tree.get(groupPosition).getKey();
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
		view.setText(getGroup(groupPosition).toString());
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

}
