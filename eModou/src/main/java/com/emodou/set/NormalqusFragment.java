package com.emodou.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.emodou.R;
import com.iflytek.cloud.InitListener;


import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.SimpleExpandableListAdapter;

public class NormalqusFragment extends Fragment{

	private View normalView;
	
	private ExpandableListView exListView;
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
        normalView = inflater.inflate(R.layout.set_fragment_normalqus, container, false);
        
        init();
        return normalView;
    } 
	
	public void init(){
		
		exListView = (ExpandableListView)normalView.findViewById(R.id.set_fragment_normalqus_exlist);
		//为一级条目提供数据                  
        //Groups  分组
        List<Map<String,Object>> Groups = new ArrayList<Map<String,Object>>();
        Map<String,Object> groupone = new HashMap<String,Object>();
        groupone.put("groupname", "1. eModou移动英语学习平台中提供的学习包与教材有什么区别？");
        Groups.add(groupone);
        Map<String,Object> grouptwo = new HashMap<String,Object>();
        grouptwo.put("groupname", "2.我的班级为什么用不了？");
        Groups.add(grouptwo);
        Map<String,Object> groupthree = new HashMap<String,Object>();
        groupthree.put("groupname", "3. 如何购买学习包？");
        Groups.add(groupthree);
        Map<String,Object> groupfour = new HashMap<String,Object>();
        groupfour.put("groupname", "4. 没有激活码就不能用吗？");
        Groups.add(groupfour);
        Map<String,Object> groupfive = new HashMap<String,Object>();
        groupfive.put("groupname", "5. 如何下载课程？");
        Groups.add(groupfive);
        Map<String,Object> groupsix = new HashMap<String,Object>();
        groupsix.put("groupname", "6. 选择学习包的问题");
        Groups.add(groupsix);
        Map<String,Object> groupseven = new HashMap<String,Object>();
        groupseven.put("groupname", "7. 选择课本的问题");
        Groups.add(groupseven);
        Map<String,Object> groupeight = new HashMap<String,Object>();
        groupeight.put("groupname", "8. 学习包退订问题");
        Groups.add(groupeight);
        //定义两个List<Map<String,Object>>  childone和childtwo
        //为二级条目提供数据
        //childone
        List<Map<String,Object>> childone = new ArrayList<Map<String,Object>>();
        Map<String,Object> childonedateone = new HashMap<String,Object>();
        childonedateone.put("mp3name", "eModou移动英语学习平台旨在从教材出发，超越教材，从而帮助学生有重点有目标地学习，同时也帮助教师轻松教学。\n"+
										"学习内容：本产品拥有培生公司的官方授权，不仅包括教材内容，还有很多内部学习资料供学习者扩展、测试与提升。功能：\n"+
										"（1）本产品将听、说、读和背单词分离出来，用户可根据自身情况，进行重点训练。\n"+
										"（2）本产品拥有个人学习信息管理功能，用户的学习时间、学习成绩、进度都有相应的统计。\n"+
										"（3）本产品具有取词功能，不认识的单词只需轻轻一点，便能查询，用户可添加到生词本，进入单词模块进行循环记忆。\n"+
										"（4）阅读计时功能可以帮助用户冲刺考试。\n"+
										"（5）听力与阅读文章均提供双语阅读，可自由切换。\n"+
										"（6）班级功能让学生与教师互动起来，在这里教师可以布置作业，学生完成作业的同时，大数据会将学生的弱项、错误整理分析出来，让教师更有针对性的授课。");
        childone.add(childonedateone);
         
        //childtwo
        List<Map<String,Object>> childtwo = new ArrayList<Map<String,Object>>();
        Map<String,Object> childtwodateone = new HashMap<String,Object>();
        childtwodateone.put("mp3name", "（1）如果你的学习包是由学校统一订购，那么会由你的英语教师创建班级，并发给你班级码或者班级二维码，你可以在注册完成后或者在“个人”页面里选择“课堂学习情况”输入该码，申请加入班级。待教师确认通过后，使用“班级”功能。\n"+
									   "（2）如果你的学习包并非由学校统一订购，但你的教师也使用“易魔豆教师版app”创建班级，并发给你班级码或者班级二维码，你同样可以在注册完成后或者在“个人”页面里选择“课堂学习情况”输入该码，申请加入班级。待教师确认通过后，使用“班级”功能。\n"+
									   "（3）如果你愿意自己以教师身份创建班级，管理大家学习，可以下载“易魔豆教师版app”使用上述功能。\n"+
									   "（4）如果没有任何教师给你班级码或者班级二维码，那你目前无法使用“班级”功能。");
        childtwo.add(childtwodateone);
        
        //childthree
        List<Map<String,Object>> childthree = new ArrayList<Map<String,Object>>();
        Map<String,Object> childthreedateone = new HashMap<String,Object>();
        childthreedateone.put("mp3name", "eModou移动英语学习平台与中国移动合作，您可以直接在发现页面查看购买学习包。\n"+
									   "发送短信订阅，支付成功后中国移动会发给您一条短信激活码，您需要在激活码输入的有效期限内，下载本应用并在学习包页面输入激活码，便会获得相应课程了。\n"+
									   "如果您是大客户（即省、市、学校直接集体订阅的客户），激活码将由学校组织统一发放。\n");
        childthree.add(childthreedateone);
        
        //childfour
        List<Map<String,Object>> childfour = new ArrayList<Map<String,Object>>();
        Map<String,Object> childfourdateone = new HashMap<String,Object>();
        childfourdateone.put("mp3name", "eModou移动英语学习平台会默认给用户提供高中英语的体验版本，如果您对体验比较满意，欢迎订阅。我们还会不定期在网站、微信上发放限量激活码以体验不同学习包，敬请期待。网址是：www.emodou.com，微信号：易魔豆。");
        childfour.add(childfourdateone);
        
        //childfive
        List<Map<String,Object>> childfive = new ArrayList<Map<String,Object>>();
        Map<String,Object> childfivedateone = new HashMap<String,Object>();
        childfivedateone.put("mp3name", "eModou学习包里面的内容很多，为了满足用户的需求，我们设置了两种下载方法。\n"
        								+"第一种方法是：在课程列表页整体下载。在用户网络畅通的情况下，建议选择此项，下载中途停止也没有关系，系统支持以后继续下载。\n"
        								+"第二种方法是：网络不畅通的情况下，建议在课程列表页面点击进入您要学习的课程，右侧的“下载”按钮会出现进度，很快便会完成该课的下载，再次点击就可以学习了。");
        childfive.add(childfivedateone);
        
        //childsix
        List<Map<String,Object>> childsix = new ArrayList<Map<String,Object>>();
        Map<String,Object> childsixdateone = new HashMap<String,Object>();
        childsixdateone.put("mp3name", "首页左下方有“xxx高中英语学习包”，前方的按钮代表“更换学习包”，进入后，点击您想选择的学习包进行使用即可。");
        childsix.add(childsixdateone);
        
        //childseven
        List<Map<String,Object>> childseven = new ArrayList<Map<String,Object>>();
        Map<String,Object> childsevendateone = new HashMap<String,Object>();
        childsevendateone.put("mp3name", "在首页左下方有“第几册”显示，前方的按钮就代表“换书”，进入后，点击您想选择的书进行学习即可。系统会自动跳转到学习首页。");
        childseven.add(childsevendateone);
        
        //childeight
        List<Map<String,Object>> childeight = new ArrayList<Map<String,Object>>();
        Map<String,Object> childeightdateone = new HashMap<String,Object>();
        childeightdateone.put("mp3name", "首页左下方有“xxx高中英语学习包”，前方的按钮代表“更换学习包”，如果您需要退订已购买的学习包，只需进入该页面，点击退订按钮，点击发送短信，次月生效。");
        childeight.add(childeightdateone);
        
        
         
        //Childs
        List<List<Map<String,Object>>> Childs = new ArrayList<List<Map<String,Object>>>();
        Childs.add(childone);
        Childs.add(childtwo);
        Childs.add(childthree);
        Childs.add(childfour);
        Childs.add(childfive);
        Childs.add(childsix);
        Childs.add(childseven);
        Childs.add(childeight);
        /**
         * 使用SimpleExpandableListAdapter显示ExpandableListView
         * 参数1.上下文对象Context
         * 参数2.一级条目目录集合
         * 参数3.一级条目对应的布局文件
         * 参数4.fromto，就是map中的key，指定要显示的对象
         * 参数5.与参数4对应，指定要显示在groups中的id
         * 参数6.二级条目目录集合
         * 参数7.二级条目对应的布局文件
         * 参数8.fromto，就是map中的key，指定要显示的对象
         * 参数9.与参数8对应，指定要显示在childs中的id
         */
 
        final ExpandableListAdapter expandListAdapter = new SimpleExpandableListAdapter(getActivity(),
                Groups, R.layout.set_fragment_normalqus_group, new String[]{"groupname"},new int[]{R.id.set_fragment_normalqus_grouptext},
                Childs, R.layout.set_fragment_normalqus_child, new String[]{"mp3name"},new int[]{R.id.set_fragment_normalqus_childtext});     
        
        
        exListView.setAdapter(expandListAdapter);
        
        // 这里是控制只有一个group展开的效果  
        exListView.setOnGroupExpandListener(new OnGroupExpandListener() {  
            @Override  
            public void onGroupExpand(int groupPosition) {  
                for (int i = 0; i < expandListAdapter.getGroupCount(); i++) {  
                    if (groupPosition != i) {  
                        exListView.collapseGroup(i);  
                    }  
                }  
            }  
        });  
	}
}
