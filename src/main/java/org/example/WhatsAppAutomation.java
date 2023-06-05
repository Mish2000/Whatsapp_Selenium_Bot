package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WhatsAppAutomation {
    //Main object of the selenium automation.
    private WebDriver driver;
    //The phone number of the target user.
    private final String phoneNumber;
    //The message that will be sent to the target user.
    private final String message;
    //Object of the message log.
    private final JTextArea messageLog;
    private String response;

    //Constructor of WhatsAppAutomation class.
    public WhatsAppAutomation(String phoneNumber, String message, JTextArea messageLog) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.messageLog = messageLog;
        //Locating the files of the driver.
        System.setProperty(Constants.DRIVER_ELEMENT1, Constants.DRIVER_ELEMENT2);
        this.driver = new ChromeDriver();
    }

    //This method is responsible for the sending process , then it calls for an additional method .
    public void sendAMessage() {
        if (this.driver == null) {
            this.driver = new ChromeDriver();
        }
        this.driver.get(Constants.WHATSAPP_WEB_URL + phoneNumber);
        //This object is responsible for waiting for each element to appear for an amount of time ,I chose 60 sec.
        //If your pc or internet connection is slow, you can adjust it as you wish.
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(60));
        //This object is used for delaying the execution of some commands for a short period, to make the method flow
        // work without errors, I must mention that using Thread.sleep() in this case is causing errors.
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Constants.TOOLS_BUTTON_XPATH)));
            SwingUtilities.invokeLater(() -> {
                messageLog.append("Login was successful.\n");
                messageLog.setCaretPosition(messageLog.getDocument().getLength());
                messageLog.repaint();
            });

            // Delay for 1 second the execution (like Thread.sleep(1000)).
            scheduledExecutorService.schedule(() -> {
                try {
                    // Waiting until the chat box is appeared.
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(Constants.CHAT_BOX_XPATH)));

                    // Locate the message input field and send the message
                    WebElement messageBox = this.driver.findElement(By.xpath(Constants.CHAT_BOX_XPATH));
                    messageBox.sendKeys(this.message);

                    // Delay for 1 second the execution (like Thread.sleep(1000)).
                    scheduledExecutorService.schedule(() -> {
                        try {
                            // Locate the send button and click it
                            WebElement sendButton = this.driver.findElement(By.xpath(Constants.SEND_BUTTON_XPATH));
                            sendButton.click();

                            SwingUtilities.invokeLater(() -> {
                                this.messageLog.append("The message was sent successfully.\n");
                                this.messageLog.setCaretPosition(this.messageLog.getDocument().getLength());
                                this.messageLog.repaint();
                            });

                            // Calling the next method to continue the automation process.
                            checkMessageStatus();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //This method is responsible for updating the status of the message that was sent.
    //Every time when the message get new mark, this event is updated in the message log.
    //Note: Because I am using Thread.sleep() in the helper method, it may take a moment until
    //the message log will inform the user about the change in the status of the message.
    private void checkMessageStatus() {
        //The same object from the previous method, adjust the seconds by your needs.
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(60));
        try {
            // Checking if the last message got one v .
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(Constants.SINGLE_V_ARIA_LABEL)));
            SwingUtilities.invokeLater(() -> {
                this.messageLog.append("Sent message got one V.\n");
                this.messageLog.setCaretPosition(messageLog.getDocument().getLength());
                this.messageLog.repaint();
            });
            //Calling to helper method twice. First time to detect the double v and then to detect the double blue v.
            //Counter for the previous messages with double v to detect when the current message is getting double v.
            int messagesVVPrevSize = this.driver.findElements(By.cssSelector(Constants.DOUBLE_V_ARIA_LABEL)).size();
            checkVStatus(messagesVVPrevSize, Constants.DOUBLE_V_ARIA_LABEL, "double V");
            //Counter for the previous messages with double blue v to detect when the current message is getting double blue v.
            int messagesBlueVVPrevSize = this.driver.findElements(By.cssSelector(Constants.DOUBLE_BLUE_V_ARIA_LABEL)).size();
            checkVStatus(messagesBlueVVPrevSize, Constants.DOUBLE_BLUE_V_ARIA_LABEL, "double blue V");
            // Calling the next method to continue the automation process.
            checkIncomingMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //This is a helper method to check for the double v and double blue v marks.
    //Note: This method will not work if the target user is already inside the chat with the bot.
    //I ignored this case because in reality the target user will not wait inside the chat for the bot to send him a message.
    private void checkVStatus(int prevSize, String path, String type) {
        boolean isRead = false;
        while (!isRead) {
            // Delaying the loop iterations to reduce CPU usage.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Getting the list of all messages with double v/blue double v in the chat.
            List<WebElement> messages = this.driver.findElements(By.cssSelector(path));
            // If the last message in the list is displayed, then it has been read.
            if (!messages.isEmpty()) {
                int messagesCount = messages.size();
                //If the method detects that the message amount of some type changed, then it detects that it is
                //the last message.
                if (messages.get(messagesCount - 1).isDisplayed() && messagesCount > prevSize) {
                    isRead = true;
                    SwingUtilities.invokeLater(() -> {
                        this.messageLog.append("Sent message got " + type + ".\n");
                        this.messageLog.setCaretPosition(this.messageLog.getDocument().getLength());
                        this.messageLog.repaint();
                    });
                }
            }
        }
    }
    //This method responsible for detecting the last received message in the chat, and then streaming it in the
    //message log.
    private void checkIncomingMessage() {
        try {
            boolean messageReceived = false;
            String lastMessageBeforeSend = null;

            // Get the amount of all received messages in this chat before the target user responds to your message.
            List<WebElement> receivedMessagesBeforeSend = this.driver.findElements(By.cssSelector(Constants.RECEIVED_MESSAGE_CSS_SELECTOR));
            //If there was received messages before the one that I send.
            if (!receivedMessagesBeforeSend.isEmpty()) {
                //Then we are saving the value of the last message to use it later.
                lastMessageBeforeSend = receivedMessagesBeforeSend.get(receivedMessagesBeforeSend.size() - 1).getText();
            }
            while (!messageReceived) {
                // Delaying the loop iterations to reduce CPU usage (doing the check every 10 seconds as described in
                //the instructions)
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Updating the list of all received messages for each loop iteration to detect changes.
                List<WebElement> allReceivedMessages = this.driver.findElements(By.cssSelector(Constants.RECEIVED_MESSAGE_CSS_SELECTOR));
                // If there is at least one received message in the list, and it is displayed as the last , then we are making another check.
                if (!allReceivedMessages .isEmpty() && allReceivedMessages .get(allReceivedMessages .size() - 1).isDisplayed()) {
                    //Then we are saving the value of the last received message in the chat and checking if it is not the
                    //last message as before we entered the loop.
                    String currentResponse = allReceivedMessages .get(allReceivedMessages .size() - 1).getText();
                    //If they are not equal, then it must be a new received message, and then we streaming it in the message log.
                    if (!currentResponse.equals(lastMessageBeforeSend)) {
                        messageReceived = true;
                        final String responseMessage = currentResponse;
                        this.response = responseMessage;
                        SwingUtilities.invokeLater(() -> {
                            messageLog.append("Response received: " + responseMessage + "\n");
                            messageLog.setCaretPosition(messageLog.getDocument().getLength());
                            messageLog.repaint();
                        });
                    }
                }
            }

            // Waiting five seconds before closing the browser (you can delete it) I did it for comfort.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Closing the browser.
            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Getter for target users response, to paste it in the report file.
    public String getResponse()
    {
        return this.response;
    }


}

