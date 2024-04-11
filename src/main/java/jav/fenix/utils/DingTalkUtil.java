package jav.fenix.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 钉钉工具类
 *
 * @author liufeng
 */
@Slf4j
public class DingTalkUtil {


    /**
     * 钉钉蓝光存储研发群 机器人
     */
    static final String DEFAULT_URL = "https://oapi.dingtalk.com/robot/send?access_token=d0156bcf87bc69c509648cd5dfc7e1d33ee9d29490aa7f119005b963a331e1b7";


    public static void main(String[] args) {
        sendMessage("告警测试：hello world");
    }


    public static DingTalkClient initClient() {

        return initClient("");
    }

    public static DingTalkClient initClient(String serverUrl) {
        if (StrUtil.isEmpty(serverUrl)) {
            serverUrl = DEFAULT_URL;
        }
        return new DefaultDingTalkClient(serverUrl);
    }

    public static OapiRobotSendRequest initRequest() {
        OapiRobotSendRequest request = new OapiRobotSendRequest();

        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setIsAtAll(false);

        // 若上一步isAtAll没有设置true，则根据此处设置的手机号来@指定人
        List<String> mobiles = new ArrayList<>();
        at.setAtMobiles(mobiles);

        request.setAt(at);


        return request;
    }

    public static void sendMessage(String content) {

        OapiRobotSendRequest request = initRequest();

//        sentText(request, content);
        sendMarkdown(request);
        DingTalkClient client = initClient();

        OapiRobotSendResponse response = null;
        try {
            response = client.execute(request);
        } catch (ApiException e) {
            log.error("发送告警失败：{}", content);
        }
        log.info("{}", JSON.toJSONString(response));
        log.error(response.getErrmsg());


    }

    public static void sentText(OapiRobotSendRequest request, String content) {
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(content);
        request.setMsgtype("text");
        request.setText(text);
    }

    public static void sendLink(OapiRobotSendRequest request) {
        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
        link.setTitle("好消息！好消息！");
        link.setText("本群与百度成功达成合作关系，今后大家有什么不懂的可以直接百度搜索，不用再群里提问浪费时间啦！");
        link.setMessageUrl("https://www.baidu.com");
        link.setPicUrl("http://www.baidu.com/img/bd_logo1.png");

        request.setMsgtype("link");
        request.setLink(link);
    }

    public static void sendMarkdown(OapiRobotSendRequest request) {
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("好消息！好消息！");
        markdown.setText(
                "#### 杭州天气 @156xxxx8827\n> 9度，西北风1级，空气良89，相对温度73%\n\n"
                        + "> ![screenshot](https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png)\n"
                        + "> ###### 10点20分发布 [天气](http://www.thinkpage.cn/) \n");

        request.setMsgtype("markdown");
        request.setMarkdown(markdown);
    }

    public static void sendActionCard(OapiRobotSendRequest request) {
        OapiRobotSendRequest.Actioncard actioncard = new OapiRobotSendRequest.Actioncard();
        actioncard.setTitle("乔布斯 20 年前想打造一间苹果咖啡厅，而它正是 Apple Store 的前身");
        actioncard.setText(
                "![screenshot](@lADOpwk3K80C0M0FoA) \n"
                        + " ### 乔布斯 20 年前想打造的苹果咖啡厅  Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划");
        actioncard.setHideAvatar("0");
        actioncard.setBtnOrientation("1");
        //    actioncard.setSingleTitle("阅读全文");
        //    actioncard.setSingleURL("https://www.baidu.com/");
        List<OapiRobotSendRequest.Btns> btns = new ArrayList<>();
        OapiRobotSendRequest.Btns btn0 = new OapiRobotSendRequest.Btns();
        btn0.setTitle("内容不错");
        btn0.setActionURL("https://www.qq.com/");
        btns.add(btn0);

        OapiRobotSendRequest.Btns btn1 = new OapiRobotSendRequest.Btns();
        btn1.setTitle("不感兴趣");
        btn1.setActionURL("https://www.baidu.com/");
        btns.add(btn1);

        actioncard.setBtns(btns);
        request.setMsgtype("actionCard");
        request.setActionCard(actioncard);
    }

    //类似公众号头条、次条消息
    public static void sendFeedCard(OapiRobotSendRequest request) {
        OapiRobotSendRequest.Feedcard feedcard = new OapiRobotSendRequest.Feedcard();
        List<OapiRobotSendRequest.Links> linksList = new ArrayList<>();

        OapiRobotSendRequest.Links links0 = new OapiRobotSendRequest.Links();
        links0.setTitle("时代的火车向前开1");
        links0.setMessageURL(
                "https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI");
        links0.setPicURL("https://www.dingtalk.com/");
        linksList.add(links0);

        OapiRobotSendRequest.Links links1 = new OapiRobotSendRequest.Links();
        links1.setTitle("时代的火车向前开2");
        links1.setMessageURL(
                "https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI");
        links1.setPicURL("https://www.dingtalk.com/");
        linksList.add(links1);

        feedcard.setLinks(linksList);

        request.setMsgtype("feedCard");
        request.setFeedCard(feedcard);
    }

}

