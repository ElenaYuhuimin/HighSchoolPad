package com.emodou.extend;

import com.emodou.util.TextPage;

/**
 * TextPage
 * 
 * @author lijingwei
 * 
 */
public interface TextPageSelectTextCallBack{
	public void selectTextEvent(String selectText, TextPage tp, int start, int end, int index);

	public void selectParagraph(int paragraph);
}
