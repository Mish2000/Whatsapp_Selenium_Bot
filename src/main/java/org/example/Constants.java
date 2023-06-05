package org.example;

import javax.swing.*;

public class Constants {
    //Paths to drivers.
    public static final String DRIVER_ELEMENT1 = "webdriver.openqa.driver";
    public static final String DRIVER_ELEMENT2 = "web driver/chromedriver.exe";
    //Path to Whatsapp web.
    public static final String WHATSAPP_WEB_URL = "https://web.whatsapp.com/send?phone=";
    //Xpath of the chat box to paste the message there.
    public static final String CHAT_BOX_XPATH = "//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[1]/div/div[1]/p";
    //Xpath of the send button to successfully send the message.
    public static final String SEND_BUTTON_XPATH = "//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[2]/button/span";
    //Xpath of the tools button, I use it as indicator to the fact that the message sender logged in.
    public static final String TOOLS_BUTTON_XPATH = "//*[@id=\"app\"]/div/div/div[4]/header/div[2]/div/span/div[4]/div/span";
    //Css selector of a single v , indicates that the message was sent.
    public static final String SINGLE_V_ARIA_LABEL = "span[aria-label=' נשלחה ']";
    //Css selector of a double v , indicates that the message was received by the target user.
    public static final String DOUBLE_V_ARIA_LABEL = "span[aria-label=' נמסרה ']";
    //Css selector of a double blue v , indicates that the message was read by the target user.
    public static final String DOUBLE_BLUE_V_ARIA_LABEL = "span[aria-label=' נקראה ']";
    //Css selector for a received message , indicates that the message is received and not sent.
    public static final String RECEIVED_MESSAGE_CSS_SELECTOR ="div[data-id^='false'] div._21Ahp span._11JPr.selectable-text.copyable-text > span";
    //Frame width.
    public static final int FRAME_WIDTH = 400;
    //Frame height.
    public static final int FRAME_HEIGHT = 550;
    //Size of the message log.
    public static final int LOG_SIZE = 270;
    //Lengths of the text fields.
    public static final int PHONE_TEXT_FIELD_LENGTH =15;
    public static final int MESSAGE_TEXT_FIELD_LENGTH =30;
    //Icon of the frame.
    public static final ImageIcon ICON =new ImageIcon("src/icon.png");
    //Path to report file.
    public static final String PATH_TO_REPORT_FILE = "src/report.txt";

}
