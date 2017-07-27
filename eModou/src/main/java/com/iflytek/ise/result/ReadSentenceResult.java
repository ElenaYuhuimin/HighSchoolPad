/**
 * 
 */
package com.iflytek.ise.result;

import com.iflytek.ise.result.utl.ResultFormatUtl;

/**
 * <p>Title: ReadSentenceResult</p>
 * <p>Description: </p>
 * <p>Company: www.iflytek.com</p>
 * @author iflytek
 * @date 2015年1月12日 下午5:04:14
 */
public class ReadSentenceResult extends Result {
	
	public ReadSentenceResult() {
		category = "read_sentence";
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		if ("cn".equals(language)) {
			buffer.append("[总体结果]\n")
				.append("评测内容：" + content + "\n")
				.append("朗读时长：" + time_len + "\n")
				.append("总分：" + total_score + "\n\n")
				.append("[朗读详情]").append(ResultFormatUtl.formatDetails_CN(sentences));
		} else {
			buffer.append("[总体结果]\n")
				.append("评测内容：" + content + "\n")
				.append("总分：" + total_score + "\n\n")
				.append("[朗读详情]").append(ResultFormatUtl.formatDetails_EN(sentences));
		}
		
		return buffer.toString();
	}
}
