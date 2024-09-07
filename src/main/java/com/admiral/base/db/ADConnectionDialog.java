package com.admiral.base.db;

import com.admiral.base.util.Ini;
import com.admiral.client.plaf.AdmiralPLAF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ADConnectionDialog extends JDialog implements ActionListener {
    private static final Logger log = Logger.getLogger(ADConnectionDialog.class.getName());

    private ADConnection m_cc = null;
    private ADConnection m_ccResult = null;
    private boolean m_updating = false;
    private boolean m_saved = false;

    // components
    private JPanel mainPanel = new JPanel();
    private BorderLayout mainLayout = new BorderLayout();
    private JPanel centerPanel = new JPanel();
    private JPanel southPanel = new JPanel();
    private JButton bOK = AdmiralPLAF.getOkButton();
    private JButton bCancel = AdmiralPLAF.getCancelButton();
    private FlowLayout soutLayout = new FlowLayout();
    private GridBagLayout centerLayout = new GridBagLayout();

    private JLabel nameLabel = new JLabel();
    private JTextField nameField = new JTextField();
    private JLabel dbTypeLabel = new JLabel();
    private JComboBox dbTypeField = new JComboBox(DataBasesSupported.DATABASE_NAMES);
    private JLabel hostLabel = new JLabel();
    private JTextField hostField = new JTextField();
    private JLabel portLabel = new JLabel();
    private JTextField dbPortField = new JTextField();
    private JLabel sidLabel = new JLabel();
    private JTextField sidField = new JTextField();
    private JButton bTestDB = new JButton();
    private JLabel dbUidLabel = new JLabel();
    private JTextField dbUidField = new JTextField();
    private JPasswordField dbPwdField = new JPasswordField();

    private boolean isCancel = true;

    private void jbInit() throws Exception {
        this.setTitle("Admiral Connection Dialog");
        mainPanel.setLayout(mainLayout);
        southPanel.setLayout(soutLayout);
        soutLayout.setAlignment(FlowLayout.RIGHT);
        centerPanel.setLayout(centerLayout);

        nameLabel.setText("Name");
        nameField.setColumns(30);
        nameField.setEditable(false);

        dbTypeLabel.setText("DataBasesSupported Type");
        hostLabel.setText("DataBasesSupported Host");
        hostField.setColumns(30);
        portLabel.setText("DataBasesSupported Port");
        dbPortField.setColumns(10);
        sidLabel.setText("DataBasesSupported Name");
        sidField.setColumns(30);
        dbUidLabel.setText("User/Password");
        dbUidField.setColumns(10);

        bTestDB.setText("Test DataBasesSupported");
        bTestDB.setHorizontalAlignment(JLabel.LEFT);

        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel,  BorderLayout.SOUTH);
        southPanel.add(bCancel, null);
        southPanel.add(bOK, null);

        // Connection Name
        centerPanel.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(12, 12, 5, 5), 0, 0));
        centerPanel.add(nameField,  new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
                ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(12, 0, 5, 12), 0, 0));

        //	DB
        centerPanel.add(dbTypeLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 12, 5, 5), 0, 0));
        centerPanel.add(dbTypeField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
                ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
//        centerPanel.add(cbBequeath, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
//                ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 12), 0, 0));
        centerPanel.add(hostLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
                ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 12, 5, 5), 0, 0));
        centerPanel.add(hostField,  new GridBagConstraints(1, 5, 2, 1, 0.0, 0.0
                ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 12), 0, 0));
        centerPanel.add(portLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
                ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        centerPanel.add(dbPortField, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
                ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
        centerPanel.add(sidLabel,  new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
                ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        centerPanel.add(sidField,   new GridBagConstraints(1, 7, 2, 1, 0.0, 0.0
                ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 12), 0, 0));
        centerPanel.add(dbUidLabel, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
                ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        centerPanel.add(dbUidField, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
                ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
        centerPanel.add(dbPwdField, new GridBagConstraints(2, 8, 1, 1, 1.0, 0.0
                ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 12), 0, 0));

        centerPanel.add(bTestDB,  new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0
                ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 12, 0), 0, 0));

        dbTypeField.addActionListener(this);
        hostField.addActionListener(this);
        dbPortField.addActionListener(this);
        sidField.addActionListener(this);
        bTestDB.addActionListener(this);
        bOK.addActionListener(this);
        bCancel.addActionListener(this);

    }

    public ADConnectionDialog() {
        this(null);
    }

    public ADConnectionDialog(ADConnection cc) {
        super((Frame)null, true);
        try{
            jbInit();
            setConnection(cc);
        }
        catch (Exception e){
            log.log(Level.SEVERE, e.toString());
        }
        AdmiralPLAF.showCenterScreen(this);
    }

    private void updateInfo(){
        m_updating = true;
        nameField.setText(m_cc.getDbName());

        dbTypeField.setSelectedItem(m_cc.getDbType());
        hostField.setText(m_cc.getDbHost());
        dbPortField.setText(m_cc.getDbPort());
        sidField.setText(m_cc.getDbName());

        dbUidField.setText(m_cc.getDbUser());
        dbPwdField.setText(m_cc.getDbPass());

        bTestDB.setToolTipText(m_cc.getConnectionURL());
        bTestDB.setIcon(getStatusIcon(m_cc.isDatabaseOK()));
        m_updating = false;
    }

    private void updateADConnection(){
        if(Ini.isClient()){
            if(!hostField.getText().equals(m_cc.getDbHost())){
                m_cc.setAppHost(hostField.getText());
            }
        }
        else{
            m_cc.setAppHost("localhost");
        }

        //
        m_cc.setDbType((String) dbTypeField.getSelectedItem());
        m_cc.setDbHost(hostField.getText());
        m_cc.setDbPort(dbPortField.getText());
        m_cc.setDbName(sidField.getText());
        m_cc.setDbUser(dbUidField.getText());
        m_cc.setDbPass(String.valueOf(dbPwdField.getPassword()));
    }

    public void setConnection(ADConnection cc){
        m_cc = cc;
        if (m_cc == null) {
            m_cc = ADConnection.get();
        }

        m_ccResult = m_cc;

        String type = m_cc.getDbType();
        if(type == null || type.isEmpty()){
            dbTypeField.setSelectedItem(null);
        }
        else{
            m_cc.setDbType(m_cc.getDbType());
        }
        updateInfo();
    }

    public ADConnection getConnection()
    {
        return m_ccResult;
    }   //  getConnection;

    public boolean isCancel() {
        return isCancel;
    }

    private Icon getStatusIcon (boolean ok)
    {
        if (ok)
            return bOK.getIcon();
        else
            return bCancel.getIcon();
    }   //  getStatusIcon

    private void setBusy(boolean busy){
        if(busy){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }else{
            this.setCursor(Cursor.getDefaultCursor());
        }
        m_updating = busy;
    }

    private void cmd_testDB(){
        setBusy(true);
        Exception e = m_cc.testDatabase();

        if(e != null){
            JOptionPane.showMessageDialog(ADConnectionDialog.this, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }

        setBusy(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(m_updating)
            return;
        Object	src	= e.getSource();

        if(src == bOK){
            m_cc.setName();
            m_ccResult = m_cc;
            dispose();
            return;
        }else if(src == bCancel){
            m_cc.setName();
            dispose();
            return;
        }else if(src == dbTypeField){
            if(dbTypeField.getSelectedItem() == null){
                return;
            }

            if(m_cc.getDbType() != (String)dbTypeField.getSelectedItem()){
                m_cc.setDbType((String)dbTypeField.getSelectedItem());
                // todo m_cc.setDatabaseDefaults()
            }

            dbPortField.setText(String.valueOf(m_cc.getDbPort()));
        }

        updateADConnection();
        if(src == bTestDB){
            cmd_testDB();
        }

        if(src == nameField){
            m_cc.setName(nameField.getText());
        }

        updateInfo();
    }
}
