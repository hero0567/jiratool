

通过jira-rest-java-client访问jira信息
1. 通txt里面的id获取对应的jira信息
2. 获取jira的comments
3. 获取jira的history
4. 获取jira的attachment
5. 获取jira的assignee
6. 把结果输出到excel或txt里面
7. 通过swing启动

https://community.atlassian.com/t5/Jira-questions/jira-rest-java-client-2-0-0-m2/qaq-p/73631
https://packages.atlassian.com/public/com/atlassian/jira/jira-rest-java-client-parent/5.2.0/
https://ecosystem.atlassian.net/wiki/spaces/JRJC/overview
https://community.atlassian.com/t5/Jira-questions/Issue-history-through-REST-API/qaq-p/503830

mvn assembly:assembly

功能介绍
1. 现在暂时只支持从txt去读ISSUE Key，结果也是保存到txt里面
2. ISSUE Key的txt格式如下：第一个为QA的名字，第二个为ISSUE Key
xiao.fei;AMZCUS5-5412
xiao.fei;AMZCUS5-11391
3. 生成的结果为issueresult.txt，就在当前目录。结果可以通过Execl的导入功能直接导入到Excel
4. 程序通过双击jira-tool.jar直接运行，选择ISSUE Key有2种方式。
    1). 通过拖文件的方式，直接把文件放在第一个灰色的输入框
    2). 通过Choose..的按钮找到issue key文件


release 1.1
1. 找出assignee里面的最后2个comments
2. 改为remark状态的时候的前后3小时，是否上传附件，是否添加comments
3. assignee去掉固定的一些名字


release 1.2
1 最后一次Remark by Customer状态修改的Date
2 获取JIRA上的Unique ASIN Qty或者是Variation ASIN Qty（取决于当前的Issuekey是Unique 还是variation）
3 获取CUS界面的客户Assignee信息
4 获取FAC界面的QA信息
5 获取FAC界面的External QA Round和Internal QA Round

release 1.3
1. 添加status，ticket type
2. Assignees改为Comment Authors，展示的数据规则也变化
3. ACount改为Comment Author Count 数量
4. Last Comment和Second Comment由于2的变化也相应变化


release 1.4
1 加一列展示最后两轮(每次comment为一轮)的QA是否更换的信息。
2 Comments的所有User都需要列出来，同名的也需要列出来。 但是Count还是保持之前的unique不变。
3 用户排除列表中需要排除掉xuelan Jin
4 "Last Ready For Customer" miss and back again.


release 1.4.1
1. 修改QA Changed为qty的数量
2. 修改Remark Comment为qty的数量
3. 修改Remark Attachemtn为qty的数量

release 1.4.2
1. 把Reject的flag 1改为QTY的值
2. 添加FAC的assignee

release 1.4.3
1. 替换所有的name从displayName替换为name