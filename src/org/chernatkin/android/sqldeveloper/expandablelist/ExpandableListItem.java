package org.chernatkin.android.sqldeveloper.expandablelist;

import android.view.View;
import android.view.View.OnClickListener;

public class ExpandableListItem {

	private final String text;
	
	private final View.OnClickListener onClickListener;

	public ExpandableListItem(final String text, final OnClickListener onClickListener) {
		this.text = text;
		this.onClickListener = onClickListener;
	}

	public ExpandableListItem(String text) {
		this.text = text;
		this.onClickListener = null;
	}

	public String getText() {
		return text;
	}

	public View.OnClickListener getOnClickListener() {
		return onClickListener;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpandableListItem other = (ExpandableListItem) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
	
	
}
