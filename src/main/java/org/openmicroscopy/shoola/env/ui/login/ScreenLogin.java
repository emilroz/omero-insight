/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2021 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.env.ui.login;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openmicroscopy.shoola.env.LookupNames;
import org.openmicroscopy.shoola.env.config.OMEROInfo;
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.data.login.UserCredentials;
import org.openmicroscopy.shoola.util.CommonsLangUtils;

import org.openmicroscopy.shoola.util.ui.IconManager;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/** 
 * The login frame.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * @since OME3.0
 */
public class ScreenLogin 
	extends JFrame
	implements ActionListener, DocumentListener, PropertyChangeListener
{
	
	/** Bounds property indicating this window is moved to the front. */
	public static final String		TO_FRONT_PROPERTY = "toFront";
	
	/** Bounds property indicating to log in. */
	public static final String 		LOGIN_PROPERTY = "login";
	
	/** Bounds property indicating to log in. */
	public static final String 		QUIT_PROPERTY = "quit";
	
	/** Identifies the user name field. */
	public static final int			USERNAME_FIELD = 0;
	
	/** Identifies the password field. */
	public static final int			PASSWORD_FIELD = 1;

	/** The font color for text. */
	static final Color      		TEXT_COLOR = new Color(110, 102, 96);
	
	/** The font color for foot note. */
	static final Color      		FOOT_NOTE_COLOR = new Color(156, 165, 174);

	/** The default size of the window. */
	static final Dimension			DEFAULT_SIZE = new Dimension(551, 113);
	
	/** The default color for the foreground. */
	private final static Color		FOREGROUND_COLOR = Color.DARK_GRAY;
	
	/** The property name for the user who connects to <i>OMERO</i>. */
	private static final String  	OMERO_USER = "omeroUser";

	/** The property name for the connection speed used to connect to server. */
	private static final String  	OMERO_CONNECTION_SPEED = 
													"omeroConnectionSpeed";
	
	/** 
	 * The property name to indicated that the data transfer is
	 * encrypted or not. 
	 */
	private static final String  	OMERO_TRANSFER_ENCRYPTED = 
		"omeroTransferEncrypted";
	
	/** Indent value for text. */
	private static final int			TEXT_INDENT = 15;
	
	/** The style of the font for the version. */
	private static final int		VERSION_FONT_STYLE = Font.BOLD;

	/** The user name text. */
	private static final String		USER_TEXT = "Username: ";

	/** The password text. */
	private static final String		PASSWORD_TEXT = "Password: ";

	/** The number of column of the text field. */
	private static final int		TEXT_COLUMN = 12;
	
	/** The maximum number of characters.*/
	private static final int 		MAX_CHAR = 50;
	
	/** Text field to enter the login user name. */
	private static JTextField          user;

	/** Password field to enter login password. */
	private JPasswordField      pass;

	/** Button to bring up the <code>ServerDialog</code>. */
	private JButton				configButton;

	/** Button to login to server. */
	private JButton             login;

	/** Button to quit the application. */
	private JButton             cancel;

	/** The name of the server or default value if none already defined. */
	private String				serverName;

	/** Field hosting the server text. */
	private static JTextPane 	serverText;
	
	/** The UI component hosting the server text. */
	private JPanel 				serverTextPane;

	/** Field hosting the server text. */
	private JLabel 				connectionSpeedText;
	
	/** UI component hosting the version of the software. */
	private JTextPane 			versionInfo;

	/** Reference to the editor hosting the table. */
	private ServerEditor		editor;

	/** The selected connection speed. */
	private int					speedIndex;
	
	/** Indicates to show or hide the connection speed option. */
	private boolean				connectionSpeed;
	
	/** The default foreground color. */
	private Color				defaultForeground;
	
	/** The component displaying the login option. */
	private List<JComponent> 	ref;
	
	/** The component displaying the controls. */
	private JPanel 				mainPanel;

	/** 
	 * Button indicating that the transfer of data is secured or not
	 * depending on the selected status. 
	 */
	private JButton				encryptedButton;
	
	/** Flag indicating that the transfer of data is secured or not. */
	private boolean				encrypted;
	
	/** The map hosting the real group names.*/
	private Map<Integer, String> groupNames;
	
	/** The layered hosting the various UI components. */
	private JLayeredPane layers;
	
	/** Displays the name of the task that is currently being executed. */
	private JLabel currentTask;
	
	/** Provides feedback on the state of the initialization process. */
	private JProgressBar progressBar;
	
	/** Indicates when attempting to log in.*/
	private boolean loginAttempt;
	
	/** The listener to encrypt the data transfer.*/
	private ActionListener encryptionListener;
    
    /** The default server name from the configuration file.*/
    private String defaultServer;

    /** List of components to show or hide depending on connection status.*/
    private List<JComponent> components;

    /** Button popping up an message indicating to use either name or session.*/
    private JButton helpButton;

    private boolean configurable = true;

    /** Reference to the Registry */
    private Registry registry;

	/** Quits the application. */
	private void quit()
	{
		String usr = user.getText().trim();
		String server = serverText.getText();
		if (usr == null) usr = "";
		if (server == null) server = "";
		firePropertyChange(QUIT_PROPERTY, Boolean.valueOf(false),
				Boolean.valueOf(true));
	}

	/** Attempts to log in. */
	private void login()
	{
		firePropertyChange(TO_FRONT_PROPERTY, Boolean.valueOf(false),
				Boolean.valueOf(true));
		requestFocusOnField();
		StringBuffer buf = new StringBuffer();
		buf.append(pass.getPassword());
		String usr = user.getText(), psw = buf.toString();
		String s = serverText.getText();
		if (CommonsLangUtils.isBlank(usr) || CommonsLangUtils.isBlank(s)) {
			requestFocusOnField();
			return;
		}
		if (usr != null) usr = usr.trim();
		if (s != null) s = s.trim();
		setControlsEnabled(false);
		try {
			UserCredentials lc = new UserCredentials(usr, psw, s, speedIndex);
			lc.setEscapeChars((String) registry.lookup(LookupNames.LOGIN_ESCAPE_CHARACTERS));
			lc.setEncrypted(encrypted);
			lc.addCmdLineArgs(registry.getCmdLineArgs());
			setUserName(usr);
			setEncrypted();
			setControlsEnabled(false);
			loginAttempt = true;
			login.setEnabled(false);
			firePropertyChange(LOGIN_PROPERTY, null, lc);
		} catch (IllegalArgumentException e) {
			// an unsuppported server URL has been specified
			if (this.registry != null) {
				this.registry.getUserNotifier().notifyError("Error",
						"There is a problem with the server name or URL:\n"+e.getMessage());
			} else {
				JOptionPane.showMessageDialog(this, e.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			setControlsEnabled(true);
			requestFocusOnField();
		}
	}

	/** 
	 * Brings up the server dialog to select an existing server or enter
	 * a new server address.
	 */
	private void config()
	{
		ServerDialog d;
		String s = serverText.getText().trim();
		if (connectionSpeed) 
			d = new ServerDialog(this, editor, s, speedIndex);
		else d = new ServerDialog(this, editor, s);
		if (editor.getRowCount() == 0 && defaultServer != null)
			editor.addRow(defaultServer);
		d.addPropertyChangeListener(this);
		UIUtilities.centerAndShow(d);
	}

	/**
	 * Sets the {@link #encrypted} flag and modifies the icon of the 
	 * {@link #encryptedButton} accordingly.
	 */
	private void encrypt()
	{
		if (!encryptedButton.isEnabled()) return;
		encrypted = !encrypted;
		IconManager icons = IconManager.getInstance();
		if (encrypted) 
			encryptedButton.setIcon(icons.getIcon(IconManager.ENCRYPTED_24));
		else encryptedButton.setIcon(icons.getIcon(IconManager.DECRYPTED_24));
	}
	
	/**
	 * Displays a modal dialog indicating that either username or session ID 
	 * can be used.
	 */
	private void help()
	{
	    JOptionPane.showMessageDialog(this,
	            "Username or sessionID can be used to connect.\n"
	            + "Do not enter a password if a sessionID is entered.",
	            "Connection Info",
	            JOptionPane.PLAIN_MESSAGE);

	}

	/** Adds listeners to the UI components. */
	private void initListeners()
	{
		user.getDocument().addDocumentListener(this);
		pass.getDocument().addDocumentListener(this);
		login.addActionListener(this);
		user.addActionListener(this);
		pass.addActionListener(this);
		helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { help(); }
        });
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { quit(); }
		});
		configButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { config(); }
		});
		encryptionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) { encrypt(); }
		};
		encryptedButton.addActionListener(encryptionListener);
		addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e) {
				requestFocusOnField();
			} 
		});
		user.addMouseListener(new MouseAdapter() {

			/**
			 * Fires a property to move the window to the front.
			 * @see MouseListener#mouseClicked(MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				firePropertyChange(TO_FRONT_PROPERTY, Boolean.valueOf(false), 
									Boolean.valueOf(true));
				user.requestFocus();
				//if (user.getText() != null) 
				//	user.selectAll();
			}

		});
		pass.addMouseListener(new MouseAdapter() {

			/**
			 * Fires a property to move the window to the front.
			 * @see MouseListener#mouseClicked(MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				firePropertyChange(TO_FRONT_PROPERTY, Boolean.valueOf(false), 
						Boolean.valueOf(true));
				//requestFocusOnField();
			}
		});

	}

	/**
	 * Removes border and margin for the specified button and sets the default
	 * cursor to {@link Cursor#HAND_CURSOR}.
	 * 
	 * @param button	The button to set the default for.
	 */
	private void setButtonDefault(JButton button)
	{
		button.setRolloverEnabled(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	/** 
	 * Creates and initializes the components
	 * 
	 * @param userName The name of the user.
	 */
	private void initialize(String userName)
	{
	    helpButton = new JButton(IconManager.getInstance().getIcon(IconManager.HELP));
	    helpButton.setBorder(null);
	    components = new ArrayList<JComponent>();
		//status update.
		currentTask = new JLabel();
		Font newFont = currentTask.getFont().deriveFont(8);
		currentTask.setFont(newFont);
		currentTask.setForeground(ScreenLogin.TEXT_COLOR);
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setStringPainted(false);
		progressBar.setFont(newFont);

		user = new JTextField();
		user.setName("username field");
		user.setText(userName);
		user.setToolTipText("Enter your username.");
		user.setColumns(TEXT_COLUMN);
		pass = new JPasswordField();
		pass.setName("password field");
		pass.setToolTipText("Enter your password.");
		pass.setColumns(TEXT_COLUMN);
		List<String> servers = editor.getServers();
		if (!servers.isEmpty())
			serverName = servers.get(servers.size()-1);
		connectionSpeedText = new JLabel(getConnectionSpeed());
		connectionSpeedText.setForeground(TEXT_COLOR);
		connectionSpeedText.setBorder(
				BorderFactory.createEmptyBorder(5, 0, 0, 0));
		serverText = UIUtilities.buildTextPane(serverName, TEXT_COLOR);
		serverText.setName("server name text pane");
		
		serverTextPane = UIUtilities.buildComponentPanelRight(serverText, 
				false);
		serverTextPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		ref = new ArrayList<JComponent>();
		login = new JButton("Login");
		login.setName("login button");
		defaultForeground = login.getForeground();
		login.setMnemonic('L');
		login.setToolTipText("Login");
		setButtonDefault(login);
		login.setEnabled(false);
		//UIUtilities.enterPressesWhenFocused(login);
		UIUtilities.opacityCheck(login);
		cancel = new JButton("Quit");
		cancel.setMnemonic('Q');
		cancel.setToolTipText("Cancel Login.");
		setButtonDefault(cancel);
		UIUtilities.opacityCheck(cancel);
		configButton = new JButton();
		configButton.setName("config server button");
		configButton.setMnemonic('X');
		configButton.setToolTipText("Enter the server's address.");
		configButton.setBorderPainted(false);
		configButton.setBorder(null);
		//configButton.setMargin(new Insets(1, 1, 1, 1));
		configButton.setFocusPainted(false);
		configButton.setContentAreaFilled(false);
		IconManager icons = IconManager.getInstance();
		configButton.setIcon(icons.getIcon(IconManager.CONFIG_24));
		
		encrypted = !isEncrypted();
		encryptedButton = new JButton();
		List<String> tips = new ArrayList<String>();
		tips.add("The connection to the server is always encrypted.");
		tips.add("If selected, the data transfer (e.g. annotations, images) " +
				"will also be encrypted.");
		tips.add("But the transfer will be much slower.");
		
		encryptedButton.setToolTipText(UIUtilities.formatToolTipText(tips));
		encryptedButton.setBorderPainted(false);
		encryptedButton.setBorder(null);
		encryptedButton.setFocusPainted(false);
		encryptedButton.setContentAreaFilled(false);
		if (encrypted) 
			encryptedButton.setIcon(icons.getIcon(IconManager.ENCRYPTED_24));
		else encryptedButton.setIcon(icons.getIcon(IconManager.DECRYPTED_24));
		getRootPane().setDefaultButton(login);
		enableControls();
	}

	/** 
	 * Layouts the groups or login options.
	 */
	private void layoutMainPanel()
	{
		if (mainPanel == null) return;
		if (ref == null) return;
		Iterator<JComponent> i = ref.iterator();
		boolean visible = false;
		while (i.hasNext()) {
			i.next().setVisible(visible);
		}
		mainPanel.validate();
		mainPanel.repaint();
	}

	/**
	 * Builds the UI component hosting the buttons.
	 * 
	 * @return See above.
	 */
	private JPanel buildLogin()
	{
		//server information
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(serverTextPane);
		p.add(connectionSpeedText);
		
		JToolBar bar = new JToolBar();
		bar.setOpaque(false);
		bar.setBorder(null);
		bar.setFloatable(false);
		if (UIUtilities.isWindowsOS()) {
			bar.add(Box.createHorizontalStrut(5));
			bar.add(encryptedButton);
			bar.add(Box.createHorizontalStrut(5));
			bar.add(configButton);
		} else {
			bar.add(encryptedButton);
			bar.add(configButton);
		}
		
		JPanel row = new JPanel();
		row.setOpaque(false);
		row.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		row.add(p);
		row.add(bar);
		
		mainPanel.add(row);
		components.add(row);
		//user name
		JPanel group = new JPanel();
		group.setOpaque(false);
		group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
		JTextPane l = UIUtilities.buildTextPane(USER_TEXT, TEXT_COLOR);
		row = new JPanel();
		row.setOpaque(false);
		row.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		row.add(l);
		row.add(user);
		row.add(helpButton);
		group.add(row);
		
		//password
		l = UIUtilities.buildTextPane(" "+PASSWORD_TEXT, TEXT_COLOR);
		row = new JPanel();
		row.setOpaque(false);
		row.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		row.add(l);
		row.add(pass);
		row.add(Box.createHorizontalStrut(helpButton.getPreferredSize().width));
		group.add(row);
		
		mainPanel.add(group);
		components.add(group);
		//controls
		JPanel controls = new JPanel();
		controls.setOpaque(false);
		controls.add(Box.createHorizontalGlue());
		controls.add(login);
		controls.add(cancel);
		p = UIUtilities.buildComponentPanelCenter(controls, 0, 0, false);
		mainPanel.add(p);
		components.add(p);

		return mainPanel;
	}

	/** 
	 * Lays out the widgets and positions the window in the middle of
	 * the screen.
	 * 
	 * @param logo 		The Frame's background logo. 
	 * @param version	The version of the software.
	 * @param serverAvailable Pass <code>true</code> if the client needs to 
	 * connect to a server, <code>false</code> otherwise.
	 */
	private void buildGUI(Icon logo, String version, boolean serverAvailable)
	{
		JLabel splash = new JLabel(logo);
		layers = new JLayeredPane(); 
		layers.add(splash, Integer.valueOf(0));
		getContentPane().add(layers);
		int width = logo.getIconWidth();
		int height = logo.getIconHeight();
		layers.setBounds(0, 0, width, height);
		splash.setBounds(0, 0, width, height);
		int h = progressBar.getFontMetrics(progressBar.getFont()).getHeight();
		int top = 120;
		int bottom = 100;
		currentTask.setBounds(TEXT_INDENT, top, width-2*TEXT_INDENT, h);
		top += 20;
		progressBar.setBounds(TEXT_INDENT, top, width-2*TEXT_INDENT, h);
		
		addToLayer(currentTask);
		addToLayer(progressBar);
		
		mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		Font f;
		versionInfo = UIUtilities.buildTextPane(version, FOOT_NOTE_COLOR);
		f = versionInfo.getFont();
		versionInfo.setFont(f.deriveFont(VERSION_FONT_STYLE, f.getSize()-4));
		versionInfo.setOpaque(false);
		//Add login details.
		int y = height-bottom-10;
		y = top+2*h;
        buildLogin();
        displayComponents(serverAvailable);
		mainPanel.add(UIUtilities.buildComponentPanelCenter(
				versionInfo, 0, 0, false));
		mainPanel.setBounds(0, y, width, height-top-bottom);
		addToLayer(mainPanel);
	}

    /**
     * Shows or hides the components.
     *
     * @param visible Pass <code>true</code> to show,
     *                <code>false</code> to hide.
     */
    private void displayComponents(boolean visible)
    {
        Iterator<JComponent> i = components.iterator();
        while (i.hasNext()) {
            i.next().setVisible(visible);
        }
        repaint();
    }

	/** 
	 * Returns the server's name.
	 * 
	 * @return See above.
	 */
	private String getServerName()
	{
		String s = serverText.getText();
		if (s == null) return null;
		return s.trim();
	}

	/** 
	 * Adds the connection speed to the passed string.
	 * 
	 * @return The value of the connection speed.
	 */
	private String getConnectionSpeed()
	{
		switch (speedIndex) {
			case UserCredentials.HIGH: return " [High]";
			case UserCredentials.MEDIUM: return " [Medium]";
			case UserCredentials.LOW: return " [Low]";
		}
		return null;
	}
	
	/** 
	 * Sets the value of the new server.
	 * 
	 * @param s The value to set.
	 */
	private void setNewServer(String s)
	{
		if (CommonsLangUtils.isBlank(s)) {
			if (defaultServer != null)
				s = defaultServer;
		}
		serverText.setText(s);
		serverTextPane.validate();
		serverTextPane.repaint();
		layoutMainPanel();
		enableControls();
	}

	/** Sets the enabled flag of the {@link #login} button.*/
	private void enableControls()
	{
		boolean enabled = true;
		String s = serverText.getText();
		String usr = user.getText().trim();
		if (CommonsLangUtils.isBlank(s) || CommonsLangUtils.isBlank(usr)) {
			enabled = false;
		}
		login.setEnabled(enabled);
		configButton.setEnabled(this.configurable);
		if (enabled) {
			login.setForeground(defaultForeground);
		} else {
			login.setForeground(FOREGROUND_COLOR);
		}
		layoutMainPanel();
	}
	
	/**
	 * Sets the connection speed used to connect to the server.
	 * 
	 * @param speed The connection speed.
	 */
	private void setConnectionSpeed(int speed)
	{
		speedIndex = speed;
		Preferences prefs = Preferences.userNodeForPackage(ScreenLogin.class);
		prefs.put(OMERO_CONNECTION_SPEED, ""+speedIndex);
	}
	
	/**
	 * Retrieves the connection speed used to connect to the server.
	 * 
	 * @return See above.
	 */
	private int retrieveConnectionSpeed()
	{
		Preferences prefs = Preferences.userNodeForPackage(ScreenLogin.class);
		String s = prefs.get(OMERO_CONNECTION_SPEED, null);
		if (s == null || s.trim().length() == 0)
			return UserCredentials.HIGH;
		return Integer.parseInt(s);
	}
	
	/**
	 * Sets the name of the user in the preferences.
	 * 
	 * @param name The name to set.
	 */
	public void setUserName(String  name)
	{
		if (name == null) return;
		Preferences prefs = Preferences.userNodeForPackage(ScreenLogin.class);
		prefs.put(OMERO_USER, name);
	}

	/**
	 * Returns the name of the user if saved.
	 * 
	 * @return See above.
	 */
	private String getUserName()
	{
		Preferences prefs = Preferences.userNodeForPackage(ScreenLogin.class);
		return prefs.get(OMERO_USER, null);
	}

	/**
	 * Returns <code>true</code> if the data transfer is encrypted,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	private boolean isEncrypted()
	{
		Preferences prefs = Preferences.userNodeForPackage(ScreenLogin.class);
		String value = prefs.get(OMERO_TRANSFER_ENCRYPTED, null);
		return "true".equals(value); 
	}
	
	/** 
	 * Sets to <code>true</code> if the data transfer is encrypted, 
	 * <code>false</code> otherwise. 
	 */
	private void setEncrypted()
	{
		String value = "false";
		if (encrypted) value = "true";
		Preferences prefs = Preferences.userNodeForPackage(ScreenLogin.class);
		prefs.put(OMERO_TRANSFER_ENCRYPTED, value);
	}
	
	/** 
	 * Sets the default for the window. 
	 * 
	 * @param frameIcon The icon associated to the frame.
	 */
	private void setProperties(Image frameIcon)
	{
		setIconImage(frameIcon);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Setting resizable to false causes the login screen to be positioned in
		// the center of a dual screen setup, instead of the center of the primary
		// screen.
		setResizable(true);
		setUndecorated(true);
		toFront();
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param title The frame's title.
	 * @param logo The frame's background logo. Mustn't be <code>null</code>.
	 * @param frameIcon The image icon for the window.
	 * @param version The version of the software.
	 * @param serverAvailable Pass <code>true</code> if the client needs to 
	 * connect to a server, <code>false</code> otherwise.
	 */
	public ScreenLogin(String title, Icon logo, Image frameIcon, String version,
					   boolean serverAvailable, Registry registry)
	{
		super(title);
		this.registry = registry;
		setName("login window");
		Dimension d;
		if (logo != null)
			d = new Dimension(logo.getIconWidth(), logo.getIconHeight());
		else d = DEFAULT_SIZE;
		setSize(d);
		setPreferredSize(d);
		editor = new ServerEditor();
		editor.addPropertyChangeListener(ServerEditor.REMOVE_PROPERTY, this);
		speedIndex = retrieveConnectionSpeed();
		initialize(getUserName());
		initListeners();
		buildGUI(logo, version, serverAvailable);
		encrypt();
		setProperties(frameIcon);
		showConnectionSpeed(false);
		addMouseListener(new MouseAdapter() {

			/**
			 * Fires a property to move the window to the front.
			 * @see MouseListener#mouseClicked(MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) { 
				
				firePropertyChange(TO_FRONT_PROPERTY, Boolean.valueOf(false),
									Boolean.valueOf(true));
				requestFocusOnField();
			}
		});
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param title		 The frame's title.
	 * @param logo		 The frame's background logo. 
	 * 					 Mustn't be <code>null</code>.
	 * @param frameIcon  The image icon for the window.
	 * @param version	 The version of the software.
	 */
	public ScreenLogin(String title, Icon logo, Image frameIcon, String version)
	{
		this(title, logo, frameIcon, version, true, null);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param title		The frame's title.
	 * @param logo		The frame's background logo. 
	 * 					Mustn't be <code>null</code>.
	 * @param frameIcon The image icon for the window.
	 */
	public ScreenLogin(String title, Icon logo, Image frameIcon)
	{
		this(title, logo, frameIcon, null);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param logo		The frame's background logo. 
	 * 					Mustn't be <code>null</code>.
	 * @param frameIcon The image icon for the window.
	 * @param version	The version of the software.
	 */
	public ScreenLogin(Icon logo, Image frameIcon, String version)
	{
		this(null, logo, frameIcon, version);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param logo		The frame's background logo. 
	 * 					Mustn't be <code>null</code>.
	 * @param frameIcon The image icon for the window.
	 */
	public ScreenLogin(Icon logo, Image frameIcon)
	{
		this(null, logo, frameIcon, null);
	}
	
	/** 
	 * Indicates to show or hide the connection speed selection. 
	 * By default the speed is hidden.
	 * 
	 * @param connectionSpeed Pass <code>true</code> to show the 
	 * 						  connection speed option, <code>false</code>
	 * 						  otherwise. 						
	 */
	public void showConnectionSpeed(boolean connectionSpeed)
	{
		this.connectionSpeed = connectionSpeed;
		connectionSpeedText.setVisible(connectionSpeed);
	}
	
	/**
	 * Sets whether or not the buttons composing the display are enabled.
	 * 
	 * @param b Pass <code>true</code> if this component should be enabled, 
	 * 			<code>false</code> otherwise.
	 */
	public void setControlsEnabled(boolean b)
	{
		user.setEnabled(b);
		pass.setEnabled(b);
		login.setEnabled(b);
		enableControls();
		//login.requestFocus();
		configButton.setEnabled(this.configurable);
		encryptedButton.setEnabled(b);
		if (b) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			setButtonDefault(login);
			setButtonDefault(cancel);
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			login.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			login.setEnabled(false);
		}
	}

	/** Fails to log in. */
	public void onLoginFailure()
	{
		loginAttempt = false;
		setControlsEnabled(true);
		displayComponents(true);
	}
	
	/** Sets the text of all textFields to <code>null</code>. */
	public void cleanFields()
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		user.setText("");
		pass.setText("");
	}

	/**
	 * Sets the text of the textField corresponding to the specified id
	 * to <code>null</code>.
	 * 
	 * @param fieldID 	The textField's id. One of the following constants:
	 * 					{@link #USERNAME_FIELD} or {@link #PASSWORD_FIELD}.
	 */
	public void cleanField(int fieldID)
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		switch (fieldID) {
			case USERNAME_FIELD:
				user.setText("");
				break;
			case PASSWORD_FIELD:
				pass.setText("");
				break;
			default:
				cleanFields();
		}
	}
	
	/** Closes and disposes. */
	public void close()
	{
	    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		setVisible(false);
		dispose();
	}

	/** 
	 * Sets the focus on the user name field if no user name entered
	 * otherwise, sets the focus on the password field.
	 */
	public void requestFocusOnField()
	{
		if (loginAttempt) return;
		setControlsEnabled(true);
		String txt = user.getText();
		if (txt == null || txt.trim().length() == 0) user.requestFocus();
		else pass.requestFocus();
	}

	public boolean hasAttemptedToLogin() { return loginAttempt; }
	
	/**
	 * Sets the text of the {@link #cancel}.
	 * 
	 * @param text The text to set.
	 */
	public void setQuitButtonText(String text) 
	{
		if (text == null) return;
		text = text.trim();
		if (text.length() == 0) return;
		if (text.equals(cancel.getText())) return;
		cancel.setText(text);
		char c = text.toUpperCase().charAt(0);
		setQuitButtonMnemonic(c);
	}
	
	/**
	 * Sets the text displays when the cursor lingers over the component.
	 * 
	 * @param toolTipText The text to set.
	 */
	public void setQuitButtonToolTipText(String toolTipText)
	{
		cancel.setToolTipText(toolTipText);
	}
	
	/**
	 * Sets the keyboard mnemonic.
	 * 
	 * @param mnemonic The value to set.
	 */
	public void setQuitButtonMnemonic(int mnemonic)
	{
		cancel.setMnemonic(mnemonic);
	}

	/**
	 * Sets the total number of initialization tasks that have to be
     * performed.
     * 
	 * @param maxTask The total number of tasks.
	 */
	public void initProgressBar(int maxTask)
	{
		progressBar.setMinimum(0);
		progressBar.setMaximum(maxTask);
		progressBar.setValue(0);
	}
	
	/**
	 * Adds the specified component to the layeredPane.
	 * 
	 * @param c The component to add.
	 */
	public void addToLayer(JComponent c)
	{
		if (c == null) return;
		layers.add(c, Integer.valueOf(1));
	}
	
	/**
     * Sets the value of the progress bar and the status message.
     * 
     * @param value	The status message.
     * @param perc	The value to set.
     */
    public void setStatus(String value, int perc)
    {
    	currentTask.setText(value);
    	progressBar.setValue(perc);
    }
    
    /** 
     * Shows or hides the progress bar and the tasks label. 
     * 
     * @param visible Pass <code>true</code> to show, <code>false</code> to
     * hide.
     * @param requestFocus Pass <code>true</code> to request focus,
     * <code>false</code> otherwise.
     */
    public void setStatusVisible(boolean visible, boolean requestFocus)
    {
    	currentTask.setVisible(visible);
    	progressBar.setVisible(visible);
    	if (requestFocus) {
    		login.setEnabled(true);
    		requestFocusOnField();
    	}
    }
    
    /**
     * Sets the encryption parameters.
     * 
     * @param encrypted Pass <code>true</code> to encrypt the data transfer,
     * 					<code>false</code> otherwise.
     * @param configurable Pass <code>true</code> to allow the user to interact
     * with the encryption controls, <code>false</code> otherwise.
     */
    public void setEncryptionConfiguration(boolean encrypted,
    		boolean configurable)
    {
    	if ((encrypted && !this.encrypted) || (!encrypted && this.encrypted))
    		encrypt();
    	if (!configurable)
    		encryptedButton.removeActionListener(encryptionListener);
    }
    /**
     * Indicates if the user can modify or not the host name from the UI.
     * 
     * @param info The connection information.
     * @param configurable Pass <code>true</code> to allow to change the 
     * host name, <code>false</code> otherwise.
     */
    public void setDefaultHostConfiguration(OMEROInfo info, boolean configurable)
    {
		defaultServer = info.getConnectionString();
		this.configurable = configurable;
        if (CommonsLangUtils.isNotBlank(defaultServer)) {
            if (configurable) {
                List<String> servers = editor.getServers();
                if (servers == null || servers.size() == 0) 
                    editor.addRow(defaultServer);
                else {
                    Iterator<String> i = servers.iterator();
                    String value;
                    boolean exist = false;
                    while (i.hasNext()) {
                        value = i.next();
                        if (defaultServer.equals(value)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) editor.addRow(defaultServer);
                }
            } else {
                serverName = defaultServer;
            }
            if (editor.getServers().size()<2)
				setNewServer("");
        }
    }

	/**
	 * Reacts to property changes fired by the <code>ScreenDialog</code>
	 * window.
	 * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) 
	{
		String name = evt.getPropertyName();
		if (ServerDialog.SERVER_PROPERTY.equals(name)) {
			String v = getServerName();
			String s = (String) evt.getNewValue();
			if (s == null) {
				setNewServer(null);
				return;
			}
			String trim = s.trim();
			if (v.equals(trim)) return;
			setNewServer(trim);
		} else if (ServerEditor.REMOVE_PROPERTY.equals(name)) {
			requestFocusOnField();
			String v = getServerName();
			String oldValue = (String) evt.getOldValue();
			if (v.equals(oldValue)) 
				setNewServer((String) evt.getNewValue());
		} else if (ServerDialog.CONNECTION_SPEED_PROPERTY.endsWith(name)) {
			setConnectionSpeed(((Integer) evt.getNewValue()).intValue());
			connectionSpeedText.setText(getConnectionSpeed());
		}
	}

	/**
	 * Enables the controls.
	 * @see DocumentListener#insertUpdate(DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent e) { enableControls(); }

	/**
	 * Enables the controls.
	 * @see DocumentListener#removeUpdate(DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent e) { enableControls(); }
	
	/** 
	 * Handles action events fired by the login fields and button.
	 * Once user name and password have been entered, the login fields and
	 * button will be disabled.
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) { login(); }

	/**
	 * Required by the {@link DocumentListener} I/F but no-op implementation 
	 * in our case.
	 * @see DocumentListener#changedUpdate(DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent e) {}

}
