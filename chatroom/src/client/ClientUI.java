package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

public class ClientUI extends JFrame implements Event {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    CardLayout card;
    ClientUI self;
    JPanel textArea;
    JPanel userPanel;
    List<User> users = new ArrayList<User>();
    private final static Logger log = Logger.getLogger(ClientUI.class.getName());
    Dimension windowSize = new Dimension(400, 400);

    public ClientUI(String title) {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setPreferredSize(windowSize);
	setLocationRelativeTo(null);
	self = this;
	setTitle(title);
	card = new CardLayout();
	setLayout(card);
	createConnectionScreen();
	createUserInputScreen();
	createPanelRoom();
	createPanelUserList();
	showUI();
    }

    void createConnectionScreen() {
	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	JLabel hostLabel = new JLabel("Host:");
	JTextField host = new JTextField("127.0.0.1");
	panel.add(hostLabel);
	panel.add(host);
	JLabel portLabel = new JLabel("Port:");
	JTextField port = new JTextField("3000");
	panel.add(portLabel);
	panel.add(port);
	JButton button = new JButton("Next");
	button.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String _host = host.getText();
		String _port = port.getText();
		if (_host.length() > 0 && _port.length() > 0) {
		    try {
			connect(_host, _port);
			self.next();
		    }
		    catch (IOException e1) {
			e1.printStackTrace();
			// TODO handle error properly
			log.log(Level.SEVERE, "Error connecting");
		    }
		}
	    }

	});
	panel.add(button);
	this.add(panel);
    }

    void createUserInputScreen() {
	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	JLabel userLabel = new JLabel("Username:");
	JTextField username = new JTextField();
	panel.add(userLabel);
	panel.add(username);
	JButton button = new JButton("Join");
	button.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String name = username.getText();
		if (name != null && name.length() > 0) {
		    SocketClient.setUsername(name);
		    self.next();
		}
	    }

	});
	panel.add(button);
	this.add(panel);
    }

    void createPanelRoom() {
	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());

	textArea = new JPanel();
	textArea.setLayout(new BoxLayout(textArea, BoxLayout.Y_AXIS));
	textArea.setAlignmentY(Component.BOTTOM_ALIGNMENT);
	JScrollPane scroll = new JScrollPane(textArea);
	scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	panel.add(scroll, BorderLayout.CENTER);

	JPanel input = new JPanel();
	input.setLayout(new BoxLayout(input, BoxLayout.X_AXIS));
	JTextField text = new JTextField();
	input.add(text);
	JButton button = new JButton("Send");
	text.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "sendAction");
	text.getActionMap().put("sendAction", new AbstractAction() {
	    public void actionPerformed(ActionEvent actionEvent) {
		button.doClick();
	    }
	});

	button.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (text.getText().length() > 0) {
		    SocketClient.sendMessage(text.getText());
		    text.setText("");
		}
	    }

	});
	input.add(button);
	panel.add(input, BorderLayout.SOUTH);
	this.add(panel);
    }

    void createPanelUserList() {
	userPanel = new JPanel();
	userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
	userPanel.setAlignmentY(Component.TOP_ALIGNMENT);

	JScrollPane scroll = new JScrollPane(userPanel);
	scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

	Dimension d = new Dimension(100, windowSize.height);
	scroll.setPreferredSize(d);

	textArea.getParent().getParent().getParent().add(scroll, BorderLayout.EAST);
    }

    void addClient(String name) {
	User u = new User(name);
	Dimension p = new Dimension(userPanel.getSize().width, 30);
	u.setPreferredSize(p);
	u.setMinimumSize(p);
	u.setMaximumSize(p);
	u.setBackgroundColor(Color.decode("#ebeef0"));
	userPanel.add(u);
	users.add(u);
	pack();
    }

    void removeClient(User client) {
	userPanel.remove(client);
	client.removeAll();
	userPanel.revalidate();
	userPanel.repaint();
    }
    
    //m indicates whether to unmute or mute
    void updateUserList(String m, String username) {
    	if(m.equals("You muted")) {
    		Iterator<User> iter = users.iterator();
    		while (iter.hasNext()) {
    		    User u = iter.next();
    		    if (u.getName().equals(username)) {
    		    	u.setBackgroundColor(Color.GRAY);
    		    }
    		}
    	} else if(m.equals("You unmuted")) {
    		Iterator<User> iter = users.iterator();
    		while (iter.hasNext()) {
    		    User u = iter.next();
    		    if (u.getName().equals(username)) {
    		    	u.setBackgroundColor(Color.decode("#ebeef0"));
    		    }
    		}
    	} else {
    		Iterator<User> iter = users.iterator();
    		while (iter.hasNext()) {
    			User u = iter.next();
    		    if (u.getName().equals(m)) {
    		    	u.setBackgroundColor(Color.CYAN);
    		    } else {
    		    	u.setBackgroundColor(Color.decode("#ebeef0"));
    		    }
    		}
    	}
    }

    /***
     * Attempts to calculate the necessary dimensions for a potentially wrapped
     * string of text. This isn't perfect and some extra whitespace above or below
     * the text may occur
     * 
     * @param str
     * @return
     */
    int calcHeightForText(String str) {
	FontMetrics metrics = self.getGraphics().getFontMetrics(self.getFont());
	int hgt = metrics.getHeight();
	int adv = metrics.stringWidth(str);
	final int PIXEL_PADDING = 6;
	Dimension size = new Dimension(adv, hgt + PIXEL_PADDING);
	final float PADDING_PERCENT = 1.1f;
	// calculate modifier to line wrapping so we can display the wrapped message
	int mult = (int) Math.floor(size.width / (textArea.getSize().width * PADDING_PERCENT));
	// System.out.println(mult);
	mult++;
	return size.height * mult;
    }

    void addMessage(String str) {
	JEditorPane entry = new JEditorPane();
	entry.setContentType("text/html");
	entry.setEditable(false);
	// entry.setLayout(null);
	
	String displayStr = "";
	
	if (str.indexOf(":") > -1) {
		String[] s = str.split(":", 2); //only split at the first instance of :
		String name = s[0];
		String msg = s[1];
		
		if(msg.indexOf("**") > -1) {
			String[] s1 = msg.split("\\*\\*");
			String m = "";
			
			for(int i = 0; i < s1.length; i++) {
				if(i % 2 == 0) {
					m += s1[i];
				}
				else {
					m += "<b>" + s1[i] + "</b>";
				}
			}
			
			msg = m;
		}
		
		if(msg.indexOf("~") > -1) {
			String[] s1 = msg.split("~");
			String m = "";
			
			for(int i = 0; i < s1.length; i++) {
				if(i % 2 == 0) {
					m += s1[i];
				}
				else {
					m += "<i>" + s1[i] + "</i>";
				}
			}
			
			msg = m;
		}
		
		if(msg.indexOf("_") > -1) {
			String[] s1 = msg.split("_");
			String m = "";
			
			for(int i = 0; i < s1.length; i++) {
				if(i % 2 == 0) {
					m += s1[i];
				}
				else {
					m += "<u>" + s1[i] + "</u>";
				}
			}
			
			msg = m;
		}
		
		//almost works, html isn't working
		/*
		if(msg.indexOf("red>") > -1) {
			String[] s1 = msg.split("red>");
			String m = "";
			
			for(int i = 0; i < s1.length; i++) {
				if(i % 2 == 0) {
					m += s1[i];
				}
				else {
					m += "<div style=\"color:red;\">" + s1[i] + "</div>";
				}
			}
			
			msg = m;
		}
		*/
		
		//if it's one of the special commands like Flip or Roll, use different format
		//if not, just do normal message format
		if(name.equals("Flip result")) {
			entry.setBackground(Color.LIGHT_GRAY);
			displayStr = "Flipping a coin... result is <b><u>" + msg + "</b></u>";
		} else if (name.equals("Roll result")) {
			entry.setBackground(Color.LIGHT_GRAY);
			displayStr = "Rolling a die... result is <b><u>" + msg + "</b></u>";
		} else if(name.equals("Muted")) {
			displayStr = "<i><u>" + msg + "</u> muted you!</i>";
		} else if(name.equals("Unmuted")) {
			displayStr = "<i><u>" + msg + "</u> unmuted you!</i>";
		}
		else {
			displayStr = name + ":" + msg;	
		}
	}
	
	entry.setText(displayStr);
	Dimension d = new Dimension(textArea.getSize().width, calcHeightForText(str));
	// attempt to lock all dimensions
	entry.setMinimumSize(d);
	entry.setPreferredSize(d);
	entry.setMaximumSize(d);
	textArea.add(entry);

	pack();
	System.out.println(entry.getSize());
	JScrollBar sb = ((JScrollPane) textArea.getParent().getParent()).getVerticalScrollBar();
	sb.setValue(sb.getMaximum());
    }

    void next() {
	card.next(this.getContentPane());
    }

    void previous() {
	card.previous(this.getContentPane());
    }

    void connect(String host, String port) throws IOException {
	SocketClient.callbackListener(this);
	SocketClient.connectAndStart(host, port);
    }

    void showUI() {
	pack();
	Dimension lock = textArea.getSize();
	textArea.setMaximumSize(lock);
	lock = userPanel.getSize();
	userPanel.setMaximumSize(lock);
	setVisible(true);
    }

    @Override
    public void onClientConnect(String clientName, String message) {
	log.log(Level.INFO, String.format("%s: %s", clientName, message));
	addClient(clientName);
	if (message != null && !message.isBlank()) {
	    self.addMessage(String.format("%s: %s", clientName, message));
	}
    }

    @Override
    public void onClientDisconnect(String clientName, String message) {
	log.log(Level.INFO, String.format("%s: %s", clientName, message));
	Iterator<User> iter = users.iterator();
	while (iter.hasNext()) {
	    User u = iter.next();
	    if (u.getName() == clientName) {
		removeClient(u);
		iter.remove();
		self.addMessage(String.format("%s: %s", clientName, message));
		break;
	    }

	}
    }

    @Override
    public void onMessageReceive(String clientName, String message) {
	log.log(Level.INFO, String.format("%s: %s", clientName, message));
	if(clientName.equals("You muted") || clientName.equals("You unmuted")) {
		updateUserList(clientName, message);
	} else {
		self.addMessage(String.format("%s: %s", clientName, message));
		//terrible logic
		if(!clientName.equals("Flip result") && !clientName.equals("Roll result") && !clientName.equals("Muted") && !clientName.equals("Unmuted")) {
			updateUserList(clientName, message);
		}
	}
    }

    @Override
    public void onChangeRoom() {
	Iterator<User> iter = users.iterator();
	while (iter.hasNext()) {
	    User u = iter.next();
	    removeClient(u);
	    iter.remove();
	}
    }

    public static void main(String[] args) {
	ClientUI ui = new ClientUI("My UI");
	if (ui != null) {
	    log.log(Level.FINE, "Started");
	}
    }
}