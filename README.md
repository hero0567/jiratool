https://community.atlassian.com/t5/Jira-questions/jira-rest-java-client-2-0-0-m2/qaq-p/73631
https://packages.atlassian.com/public/com/atlassian/jira/jira-rest-java-client-parent/5.2.0/
https://ecosystem.atlassian.net/wiki/spaces/JRJC/overview
https://community.atlassian.com/t5/Jira-questions/Issue-history-through-REST-API/qaq-p/503830

rejectCause.put("Model-Detail" ,"Model-Detail");
rejectCause.put("di-mension" ,"di-mension");
rejectCause.put("Model-Geometry" ,"Model-Geometry");
rejectCause.put("Model-Error" ,"Model-Error");
rejectCause.put("colo-r" ,"colo-r");
rejectCause.put("App-earance" ,"App-earance");
rejectCause.put("up-load" ,"up-load");
rejectCause.put("Process-Missing Comment" ,"Process-Missing Comment");



ChangelogGroup{author=BasicUser{name=bijaylax, displayName=Bijaylaxmi Pattanaik, self=https://amazon.rooomy.com.cn/rest/api/2/user?username=bijaylax}, created=2020-01-08T01:29:00.135+08:00, items=[ChangelogItem{fieldType=JIRA, field=assignee, from=null, fromString=null, to=bijaylax, toString=Bijaylaxmi Pattanaik}]}


mvn assembly:assembly

1. 找出assignee里面的最后2个comments
2. 改为remark状态的时候的前后3小时，是否上传附件，是否添加comments
3. assignee去掉固定的一些名字




1. 现在暂时只支持从txt去读ISSUE Key，结果也是保存到txt里面
2. ISSUE Key的txt格式如下：第一个为QA的名字，第二个为ISSUE Key
xiao.fei;AMZCUS5-5412
xiao.fei;AMZCUS5-11391
3. 生成的结果为issueresult.txt，就在当前目录。结果可以通过Execl的导入功能直接导入到Excel
4. 程序通过双击jira-tool.jar直接运行，选择ISSUE Key有2种方式。
    1). 通过拖文件的方式，直接把文件放在第一个灰色的输入框
    2). 通过Choose..的按钮找到issue key文件
    
    







