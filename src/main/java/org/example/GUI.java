package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class GUI extends JFrame {
    //Object of the class that is responsible for the automation process.
    private WhatsAppAutomation automation;
    //Field for entering the phone number.
    private final JTextField phoneNumberField;
    //Field for entering the message.
    private final JTextField messageField;
    //The message log for the GUI, that updates each event in the automation.
    private final JTextArea messageLog;
    private String lastPhoneNumber;
    private String lastMessage;

    //The constructor for the GUI.
    public GUI() {
        this.automation = null;
        this.setTitle("WhatsApp Message Sender");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        this.setIconImage(Constants.ICON.getImage());

        //Initialization and visual setting of the message log.
        this.messageLog = new JTextArea();
        this.messageLog.setEditable(false);
        this.messageLog.setLineWrap(true);
        this.messageLog.setWrapStyleWord(true);
        this.messageLog.setPreferredSize(new Dimension(Constants.LOG_SIZE, Constants.LOG_SIZE));
        JScrollPane messageLogScrollPane = new JScrollPane(this.messageLog);
        messageLogScrollPane.setBorder(BorderFactory.createTitledBorder("Message Log"));

        // Setting the layout of the frame.
        this.setLayout(new BorderLayout());
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Panel for phone number input.
        JPanel phonePanel = new JPanel();
        phonePanel.add(new JLabel("Phone Number: "));
        this.phoneNumberField = new JTextField(Constants.PHONE_TEXT_FIELD_LENGTH);
        phonePanel.add(this.phoneNumberField);

        // Panel for message input.
        JPanel messagePanel = new JPanel();
        messagePanel.add(new JLabel("Message: "));
        this.messageField = new JTextField(Constants.MESSAGE_TEXT_FIELD_LENGTH);
        messagePanel.add(this.messageField);

        // Add the input panels to the main panel.
        formPanel.add(phonePanel);
        formPanel.add(messagePanel);

        //Button that opens whatsapp web and starts the automation.
        JPanel buttonPanel = new JPanel();
        JButton button = new JButton("Send");
        buttonPanel.add(button);

        JButton createReportButton = new JButton("Create a report");
        buttonPanel.add(createReportButton);

        // Add the status labels and message log to a new panel.
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(messageLogScrollPane);

        // Each of the panels added to the frame.
        this.add(northPanel, BorderLayout.NORTH);
        this.add(formPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // We are using it to control the button actions
        button.addActionListener(e -> {
            String phoneNumber = this.phoneNumberField.getText();
            String message = this.messageField.getText();
            if (phoneNumber.isEmpty() && message.isEmpty()) {
                addMessageToLog("Both phone number and message are missing.");
            } else if (phoneNumber.isEmpty()) {
                addMessageToLog("Phone number is required.");
            } else if (message.isEmpty()) {
                addMessageToLog("You cannot send an empty message.");
            } else if (!isValidPhoneNumber()) {
                addMessageToLog("Invalid phone number syntax.");
            } else {
                //Here we are saving the phone number and message values to use them in case that the user would like
                //to create a report file.
                this.lastPhoneNumber = this.phoneNumberField.getText();
                this.lastMessage = this.messageField.getText();

                //Due to the additional checks , the user is able to send the message in any valid format.
                if (phoneNumber.charAt(0) == '+') {
                    phoneNumber = phoneNumber.substring(1);
                } else if (phoneNumber.charAt(0) == '0') {
                    String toFormat = "972";
                    phoneNumber = phoneNumber.substring(1);
                    phoneNumber = toFormat + phoneNumber;
                }
                this.automation = new WhatsAppAutomation(phoneNumber, message, this.messageLog);
                this.automation.sendAMessage();
            }
        });
        createReportButton.addActionListener(e -> {
            if (this.automation != null) {
                createReport();
            } else {
                addMessageToLog("No automation detected, file cannot be created.");
            }
        });
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    //This method adds a status message to the log to update the user of the events inside whatsapp.
    private void addMessageToLog(String message) {
        this.messageLog.append(message + "\n");
    }

    //This method checks if the entered phone number is in valid Israeli format.
    public boolean isValidPhoneNumber() {
        String phoneNumber = this.phoneNumberField.getText();
        String test1 = "9725";
        String test2 = "+9725";
        String test3 = "05";
        if (phoneNumber.length() == 12 && phoneNumber.substring(0, 4).equals(test1)) {
            return true;
        } else if (phoneNumber.length() == 13 && phoneNumber.substring(0, 5).equals(test2)) {
            return true;
        } else if (phoneNumber.length() == 10 && phoneNumber.substring(0, 2).equals(test3)) {
            return true;
        }
        return false;
    }

    //This method creates a report file as requested in step 7.
    //Note: You must exit the application to make the file appear under the src folder.
    private void createReport() {
        String recipient = this.lastPhoneNumber;
        String message = this.lastMessage;
        String response = this.automation.getResponse();
        if (recipient.length() == 13) {
            recipient = '0' + recipient.substring(4);
        } else if (recipient.length() == 12) {
            recipient = '0' + recipient.substring(3);
        }
        try (FileWriter writer = new FileWriter(Constants.PATH_TO_REPORT_FILE)) {
            writer.write("Recipient: " + recipient + "\n");
            writer.write("Message: " + message + "\n");
            writer.write("Response: " + response + "\n");
            addMessageToLog("Report file has been successfully created!");
        } catch (IOException e) {
            e.printStackTrace();
            addMessageToLog("Error creating the file...");
        }
    }
}
