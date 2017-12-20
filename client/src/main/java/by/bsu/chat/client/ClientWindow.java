package by.bsu.chat.client;

import by.bsu.chat.client.connection.TCPConnection;
import by.bsu.chat.client.connection.TCPConnectionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final Logger LOGGER = LogManager.getLogger(ClientWindow.class);

    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("Entered nickname");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(WIDTH, HEIGHT);
            setLocationRelativeTo(null);
            setAlwaysOnTop(true);
            log.setEditable(false);
            log.setLineWrap(true);
            add(log, BorderLayout.CENTER);

            fieldInput.addActionListener(this);
            add(fieldInput, BorderLayout.SOUTH);
            add(fieldNickname, BorderLayout.NORTH);

            setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            LOGGER.error(e);
            printErrorMessage("Connection exception" + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldInput.getText();
        if (message.equals("")) return;
        fieldInput. setToolTipText(null);
        connection.sendString(fieldNickname.getText() + ": " + message);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printErrorMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printErrorMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printErrorMessage("Connection close!");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printErrorMessage("Connection exception" + e);
    }

    private synchronized void printErrorMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
