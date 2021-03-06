package com.baifenjy.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicBorders.ButtonBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.undo.UndoManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baifenjy.frame.mp3.MusicFrame;
import com.baifenjy.io.Request;
import com.baifenjy.io.Response;
import com.baifenjy.utils.DatePickUtils;
import com.baifenjy.utils.ThreadLocalSimple;
import com.baifenjy.vo.Message;
import com.baifenjy.vo.MessageVO;
import com.eltima.components.ui.DatePicker;
import com.mysql.jdbc.StringUtils;

public class ManagerFrame extends JFrame implements ActionListener{
    private static ManagerFrame managerFrame = null;
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        managerFrame = new ManagerFrame();
  }

  private  ManagerFrame() {
      this.setTitle("管理系统_v2.0_上海力霆教育科技有限公司_[www.baifenjy.com]");
      this.setFont(new Font("宋体",Font.PLAIN,12));
      this.setBounds(300, 200,1000, 800);
      this.addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
              super.windowClosing(e);
              e.getWindow().dispose();
              System.exit(0);
          }
      });
      this.setVisible(true);
      //设置菜单
      JMenuBar jMenuBar = new JMenuBar();
      this.setJMenuBar(jMenuBar);
      JMenu jMenu_enjoy = new JMenu("娱乐天地");
      jMenu_enjoy.setFont(new Font("黑体",Font.PLAIN,14));
      jMenuBar.add(jMenu_enjoy);
      JMenuItem item_music = new JMenuItem("音乐欣赏");
      jMenu_enjoy.add(item_music);
      JMenuItem item_news = new JMenuItem("新闻早报");
      jMenu_enjoy.add(item_news);
      JMenuItem item_game = new JMenuItem("棋牌游戏");
      jMenu_enjoy.add(item_game);
      item_music.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new MusicFrame();
        }
      });
      
      
      //设置水平滚动条不显示,设置垂直滚动条一直显示
      final JScrollPane jScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      jScrollPane.getVerticalScrollBar().setUnitIncrement(30);
      //定义一个JPanel面板 以整合其他面板
      final JPanel panel = new JPanel();
      panel.setPreferredSize(new Dimension(1200,1000)); 
      panel.setLayout(null);
      jScrollPane.setBorder(null);
      jScrollPane.setViewportView(panel);
      this.add(jScrollPane);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      connect();

      final JPanel rightPanel = new JPanel();
      panel.add(rightPanel);
      createRightPanel(rightPanel);

      final JPanel leftPanel = new JPanel();
      panel.add(leftPanel);
      createLeftPanel(leftPanel,rightPanel);
      
      
      this.addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosed(WindowEvent e) {
              disconnect();
          }
      });
      
  }
  
  Socket socket;
  DataOutputStream messageDos;
  DataInputStream messageDis;
  private static final String IP = "127.0.0.1";
  private static final int ORDER_PORT = 8888;
  
  public void connect() {
      try {
          socket = new Socket(IP, ORDER_PORT);
          System.out.println("一个客户端登陆中....!");
          messageDos = new DataOutputStream(socket.getOutputStream());
          messageDis = new DataInputStream(socket.getInputStream());

      } catch (ConnectException e) {
          System.out.println("服务端异常.........");
          System.out.println("请确认服务端是否开启.........");
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
  
  public void disconnect() {
      try {
          if(messageDis != null){
              messageDis.close();
          }
          if (messageDos != null){
              messageDos.close();
          }
          if (socket != null){
              socket.close();
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

    private void createLeftPanel(JPanel leftPanel,JPanel rightPanel) {
        leftPanel.setLayout(null);
        leftPanel.setBorder(new TitledBorder(null, "网站管理", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        leftPanel.setBounds(0, 0, 200, 1000);
        leftPanel.setVisible(true);
        createMenuBar(leftPanel,rightPanel);
        
    }
    
    private void createRightPanel(JPanel leftPanel) {
        leftPanel.setLayout(null);
        leftPanel.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        leftPanel.setBounds(200, 20, 1100, 975);
        leftPanel.setVisible(true);
        
        JPanel firstPanel = new JPanel();
        leftPanel.add(firstPanel);
        createFirstPanel(firstPanel);
        
        JPanel secondPanel = new JPanel();
        leftPanel.add(secondPanel);
        createSecondPanel(secondPanel);
        
        JPanel thirdPanel = new JPanel();
        leftPanel.add(thirdPanel);
        createThirdPanel(thirdPanel);
        
    }


    private void createFirstPanel(JPanel firstPanel) {
        firstPanel.setLayout(null);
        firstPanel.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        firstPanel.setBounds(0, 0, 1100, 40);
        firstPanel.setVisible(true);
        
        JButton addNewMessageButton = new JButton("添加新留言");
        firstPanel.add(addNewMessageButton);
        createAddNewMessageButton(addNewMessageButton);
        
        JButton deleteMessageButton = new JButton("删除留言");
        firstPanel.add(deleteMessageButton);
        createDeleteMessageButton(deleteMessageButton);
    }
    

    private void createDeleteMessageButton(JButton deleteMessageButton) {
        deleteMessageButton.setFont(new Font("宋体",Font.PLAIN,12));
        deleteMessageButton.setBounds(140, 5, 100, 30);
        deleteMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 选择要删除的留言
                
            }
        });
    }

    private void createAddNewMessageButton(JButton addNewMessageButton) {
        addNewMessageButton.setFont(new Font("宋体",Font.PLAIN,12));
        addNewMessageButton.setBounds(10, 5, 100, 30);
        addNewMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSaveFrame();
//                jt.setVisible(false);
            }
            
        });
    }
    
    private void createSaveFrame() {
        isSaveFrameOpen = true;
        message_frame_title = "保存留言";
        if(newSaveFrameCount == 0){
            newSaveFrameCount++;
            createFrame();
        }else if(newSaveFrameCount >= 1){
            return;
        }
        id_text_field.setText("");
        name_field.setText("");
        message_area.setText("");
        call_area.setText("");
    }

    private DatePicker datePicker = DatePickUtils.getDatePicker();
    private JTextField name_field  = new JTextField();
    private JTextField id_text_field = new JTextField();
    private JTextArea message_area = new JTextArea();
    private JTextArea call_area = new JTextArea();
    
    private String message_frame_title ;
    
    private byte newSaveFrameCount = 0;
    private byte newEditFrameCount = 0;
    
    private boolean isSaveFrameOpen;
    
    private  void createFrame() {
        //创建一个JFrame窗口
        JFrame addNewMessageFrame = new JFrame(message_frame_title);
        addNewMessageFrame.setFont(new Font("宋体",Font.PLAIN,12));
        addNewMessageFrame.setResizable(false);
        addNewMessageFrame.setBounds(400,50,650,700);
//        addNewMessageFrame.setAlwaysOnTop(true);
        addNewMessageFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                newEditFrameCount = 0;
                newSaveFrameCount = 0;
                jt.setEnabled(true);
//                jt.setVisible(true);
                isSaveFrameOpen = false;
            }
            @Override
            public void windowClosed(WindowEvent e) {
                newEditFrameCount = 0;
                newSaveFrameCount = 0;
                jt.setEnabled(true);
//                jt.setVisible(true);
                isSaveFrameOpen = false;
            }
        });
//        addNewMessageFrame.setUndecorated(true);
//        addNewMessageFrame.setOpacity(0.9f);
//        addNewMessageFrame.setBackground(new Color(0,0,0));
        addNewMessageFrame.setVisible(true);
        //dispose();
        JScrollPane newMessageScrollPanel = new JScrollPane();
        //设置水平滚动条不显示
        newMessageScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //设置垂直滚动条一直显示
        newMessageScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        newMessageScrollPanel.getVerticalScrollBar().setUnitIncrement(20);
        newMessageScrollPanel.setBorder(null);
        addNewMessageFrame.add(newMessageScrollPanel);
        
        JPanel newMessagePanel = new JPanel();
        newMessagePanel.setPreferredSize(new Dimension(1200,800)); 
        newMessagePanel.setLayout(null);
        newMessageScrollPanel.setViewportView(newMessagePanel);
        
        JLabel time_label = new JLabel("留言时间:");
        time_label.setFont(new Font("宋体",Font.PLAIN,12));
        time_label.setBounds(10,10,70,35);
        newMessagePanel.add(time_label);
        
        
        datePicker.setBounds(80, 10, 200, 35);
        newMessagePanel.add(datePicker);
        
        JLabel name_label = new JLabel("姓名:",SwingConstants.CENTER);
        name_label.setFont(new Font("宋体",Font.PLAIN,12));
        name_label.setBounds(10, 50, 70, 35);
        newMessagePanel.add(name_label);
        
        name_field.setBounds(80, 50, 200, 35);
        name_field.setFont(new Font("宋体",Font.PLAIN,12));
        newMessagePanel.add(name_field);
        
        id_text_field.setBounds(285, 50, 200, 35);
        id_text_field.setEnabled(false);
        id_text_field.setVisible(false);
        newMessagePanel.add(id_text_field);
        
        JLabel message_label = new JLabel(" 留言内容:");
        message_label.setFont(new Font("宋体",Font.PLAIN,12));
        message_label.setBounds(10, 95, 150, 20);
        message_label.setBackground(Color.lightGray);
        newMessagePanel.add(message_label);
        
        JScrollPane message_scroll_pane = new JScrollPane(message_area);
        newMessagePanel.add(message_scroll_pane);
        message_scroll_pane.setBounds(10, 115, 600, 200);
        message_scroll_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //设置垂直滚动条一直显示
        message_scroll_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        message_scroll_pane.getVerticalScrollBar().setUnitIncrement(10);
        message_area.setBounds(0, 0, 600, 180);
        message_area.setFont(new Font("宋体",Font.PLAIN,13));
        message_area.setLineWrap(true);
        //add undoAction event
        addUndoAction(new UndoManager(),message_area);
        
        JLabel call_label = new JLabel("回复内容:",SwingConstants.LEFT);
        call_label.setFont(new Font("宋体",Font.PLAIN,12));
        call_label.setBackground(Color.lightGray);
        call_label.setBounds(10, 315, 150, 20);
        newMessagePanel.add(call_label);
        
        JScrollPane call_message_scroll_pane = new JScrollPane(call_area);
        newMessagePanel.add(call_message_scroll_pane);
        call_message_scroll_pane.setBounds(10, 335, 600, 200);
        call_message_scroll_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //设置垂直滚动条一直显示
        call_message_scroll_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        call_message_scroll_pane.getVerticalScrollBar().setUnitIncrement(10);
        call_area.setBounds(0, 0, 600, 180);
        call_area.setFont(new Font("宋体",Font.PLAIN,14));
        call_area.setLineWrap(true);
        //add undoAction event
        addUndoAction(new UndoManager(),call_area);
    
        
        JButton save_button = new JButton("保存");
        save_button.setFont(new Font("宋体",Font.PLAIN,12));
        save_button.setBounds(250, 535, 80, 30);
        newMessagePanel.add(save_button);
        save_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date date = (Date) datePicker.getValue();
                String time = ThreadLocalSimple.df.get().format(date);
                String name = name_field.getText();
                String id  = id_text_field.getText();
                String message = message_area.getText();
                String callMessage = call_area.getText();
                System.out.println(time+","+name+","+message);
                boolean flag = false;
                if(StringUtils.isNullOrEmpty(time.trim())){
                    javax.swing.JOptionPane.showMessageDialog(ManagerFrame.this, "日期必须填写");
                    flag = true;
                    return;
                }
                if(flag == false && StringUtils.isNullOrEmpty(name.trim())){
                    javax.swing.JOptionPane.showMessageDialog(ManagerFrame.this, "姓名必须填写");
                    flag = true;
                    return;
                }
                if(flag == false && StringUtils.isNullOrEmpty(message.trim())){
                    javax.swing.JOptionPane.showMessageDialog(ManagerFrame.this, "留言必须填写");
                    flag = true;
                    return;
                }
                addNewMessageFrame.dispose();
                if(!flag){
                    try {
                        if(!StringUtils.isNullOrEmpty(id.trim())){
                            messageDos.writeUTF(Request.UPDATE_MESSAGE);
                            String response = messageDis.readUTF();
                            if(Request.UPDATE_MESSAGE.equals(response)){
                                MessageVO messageVO = new MessageVO();
                                messageVO.setId(Long.parseLong(id));
                                messageVO.setTime(time);
                                messageVO.setName(name);
                                messageVO.setMessage(message);
                                messageVO.setCallMessage(callMessage);
                                String mess = JSON.toJSONString(messageVO);
                                messageDos.writeUTF(mess);
                                response = messageDis.readUTF();
                                if(Response.SUCCESS.equals(response)){
                                    //更新数据成功重写查询所有数据
                                    requeryData();
                                }
                            }
                            return;
                        }
                        messageDos.writeUTF(Request.SAVE_MESSAGE);
                        String response = messageDis.readUTF();
                        if(Request.SAVE_MESSAGE.equals(response)){
                            MessageVO messageVO = new MessageVO();
                            messageVO.setTime(time);
                            messageVO.setName(name);
                            messageVO.setMessage(message);
                            messageVO.setCallMessage(callMessage);
                            String mess = JSON.toJSONString(messageVO);
                            messageDos.writeUTF(mess);
                            response = messageDis.readUTF();
                            if(Response.SUCCESS.equals(response)){
                                //保存成功重写查询所有数据
                                requeryData();
                            }
                        }
                        
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    };
                }
            }
            private void requeryData() throws IOException {
                rowData.removeAllElements();
                queryDataFromServer(rowData, columnName,0);
                jt.validate();
                jt.updateUI();
                String desc   =  DESC_LABEL_TEXT.replaceAll("currentPage", Integer.toString(currentPage+1));// beging from 0
                desc = desc.replaceAll("pageCount", Integer.toString(pageCount));
                page_des_label.setText(desc);
                changeButtonEnabled(first_page_button, pre_page_button, next_page_button, last_page_button);
            }
        });
        
        JButton cancel_button = new JButton("取消");
        cancel_button.setFont(new Font("宋体",Font.PLAIN,12));
        cancel_button.setBounds(340, 535, 80, 30);
        newMessagePanel.add(cancel_button);
        cancel_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //关闭当前窗口
                addNewMessageFrame.dispose();
            }
        });
        
    }

    private void addUndoAction(UndoManager undoManager,JTextArea area) {
        area.getDocument().addUndoableEditListener(undoManager);
        area.addKeyListener(new KeyListener() {

                @Override
                public void keyReleased(KeyEvent arg0) {
                }
                @Override
                public void keyPressed(KeyEvent evt) {
                        if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_Z) {
                                if (undoManager.canUndo()) {
                                        undoManager.undo();
                                }
                        }
                        if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_Y) {
                                if (undoManager.canRedo()) {
                                        undoManager.redo();
                                }
                        }
                }
                @Override
                public void keyTyped(KeyEvent arg0) {
                }
        });
    }

    private void createSecondPanel(JPanel secondPanel) {
        secondPanel.setLayout(null);
        secondPanel.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        secondPanel.setBounds(0, 40, 1100, 40);
        secondPanel.setVisible(true);
        
        JLabel name_label = new JLabel("姓名:");
        name_label.setBounds(10, 5, 50, 30);
        name_label.setFont(new Font("宋体",Font.PLAIN,12));
        secondPanel.add(name_label);
        
        JTextField name_text = new JTextField();
        name_text.setBounds(50, 5, 120, 30);
        name_text.setFont(new Font("宋体",Font.PLAIN,12));
        secondPanel.add(name_text);
        name_text.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {
                
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                String name = name_text.getText();
                currentPage = 0;
                rowData.removeAllElements();
                //query first page data from server
                try {
                    queryDataFromServerByName(rowData,columnName,currentPage,name.trim());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //refresh data to JTable
                jt.validate();
                jt.updateUI();
                
                String desc = DESC_LABEL_TEXT.replaceAll("currentPage", Integer.toString(currentPage+1));// beging from 0
                desc = desc.replaceAll("pageCount", Integer.toString(pageCount));
                page_des_label.setText(desc);
                
                changeButtonEnabled(first_page_button, pre_page_button, next_page_button, last_page_button);
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }
        });
        
        JLabel start_label = new JLabel("时间:");
        start_label.setBounds(190, 5, 50, 30);
        start_label.setFont(new Font("宋体",Font.PLAIN,12));
        secondPanel.add(start_label);
        
        DatePicker start_datePicker = DatePickUtils.getDatePicker();
        start_datePicker.setBounds(230, 5, 200, 30);
        secondPanel.add(start_datePicker);
        
        
        JLabel end_label = new JLabel("—");
        end_label.setBounds(465, 5, 50, 30);
        secondPanel.add(end_label);
        
        DatePicker end_datePicker = DatePickUtils.getDatePicker();
        end_datePicker.setBounds(510, 5, 200, 30);
        secondPanel.add(end_datePicker);
        
        JButton query_button = new JButton("查询");
        query_button.setFont(new Font("宋体",Font.PLAIN,12));
        query_button.setBounds(730, 5, 100, 30);
        secondPanel.add(query_button);
        query_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //模糊查询
                String text = name_text.getText();
                
            }
        });
        
        
    }
    
    private DefaultTableModel defaultTableModel = null;
    private JTable jt = null;
    private JScrollPane jsp = null;
    
    private int currentPage ;//当前页码 从0开始
    private final int pageSize = 20;//每页数据条数
    private int rowCount ;//数据库中总记录数
    private int pageCount;//计算的总页数
    
    
    private Vector rowData;
    private Vector columnName;
    
    private String DESC_LABEL_TEXT = "当前页为第currentPage页/共pageCount页";
    
    JButton first_page_button = null;
    JButton pre_page_button = null;
    JButton next_page_button = null;
    JButton last_page_button = null;
    JLabel page_des_label = null;
    
    private void createThirdPanel(JPanel thirdPanel) {
        thirdPanel.setLayout(null);
        thirdPanel.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        thirdPanel.setBounds(0, 80, 1100, 898);
        thirdPanel.setVisible(true);
        
        //query rowData and columnName from server
        rowData = new Vector();
        columnName = new Vector();
        /*构造数据 test
         * for(int i=0;i<30;i++){
            Vector rows = new Vector();
            for (int j = 0; j < 5; j++) {
                rows.add((currentPage+1)+" page data "+i+" "+j);
            }
            rowData.add(rows);
        }
        for (int i = 0; i < 5; i++) {
            columnName.add("column name "+i);
        }*/
        //TODO query data from server
        //initial table data from page 0
        try {
            queryDataFromServer(rowData, columnName,0);
            System.out.println(pageCount+"总页数");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO query total row count from server
//        rowCount = 100;
//        pageCount = (rowCount + pageSize - 1)/pageSize;
        
        jsp = new JScrollPane();
      //设置水平滚动条不显示
        jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //设置垂直滚动条一直显示
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.getVerticalScrollBar().setUnitIncrement(20);
        //定义一个JPanel面板 以整合其他面板
        final JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(1200,1000)); 
        panel.setLayout(null);
        jsp.setViewportView(panel);
        jsp.setBounds(30, 2, 1065, 891);
        jsp.setBorder(null);
        thirdPanel.add(jsp);
        
        createColumnName(panel);
        
        
        defaultTableModel = new DefaultTableModel(rowData,columnName);
        jt = new JTable(defaultTableModel);
        jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jt.getColumnModel().getColumn(0).setPreferredWidth(10);
        jt.getColumnModel().getColumn(1).setPreferredWidth(140);
        jt.getColumnModel().getColumn(2).setPreferredWidth(190);
        jt.getColumnModel().getColumn(3).setPreferredWidth(150);
        jt.getColumnModel().getColumn(4).setPreferredWidth(100);
        jt.setRowHeight(28);
        jt.setFont(new Font("黑体",Font.PLAIN,14));
        jt.setShowGrid(true);
        jt.setGridColor(Color.lightGray);
        jt.validate();
        jt.updateUI();
        createViewTabel(panel);
        
        
        first_page_button = new JButton("首页");
        pre_page_button = new JButton("前一页");
        next_page_button = new JButton("下一页");
        last_page_button = new JButton("末页");
        page_des_label = new JLabel();
        

        createFirstPageButton(panel, first_page_button, pre_page_button, next_page_button, last_page_button,
                page_des_label);
        
        createPrePageButton(panel, first_page_button, pre_page_button, next_page_button, last_page_button,
                page_des_label);
        
        createNextPageButton(panel, first_page_button, pre_page_button, next_page_button, last_page_button,
                page_des_label);
        
        
        createLastPageButton(panel, first_page_button, pre_page_button, next_page_button, last_page_button,
                page_des_label);
        
        
        createPageDesLabel(panel, page_des_label);
        
        changeButtonEnabled(first_page_button, pre_page_button, next_page_button, last_page_button);
        
    }

    private void createColumnName(final JPanel panel) {
        //column name
        JTextField id_text_field = new JTextField("序号");
        id_text_field.setBounds(18, 10, 110, 20);
        id_text_field.setEnabled(false);
        id_text_field.setBorder(null);
        id_text_field.setBackground(Color.GRAY);
        id_text_field.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(id_text_field);
        
        JTextField name_text_field = new JTextField("姓名");
        name_text_field.setBounds(115, 10, 163, 20);
        name_text_field.setEnabled(false);
        name_text_field.setBorder(null);
        name_text_field.setBackground(Color.GRAY);
        name_text_field.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(name_text_field);
        
        JTextField message_text_field = new JTextField("留言");
        message_text_field.setBounds(270, 10, 340, 20);
        message_text_field.setEnabled(false);
        message_text_field.setBorder(null);
        message_text_field.setBackground(Color.GRAY);
        message_text_field.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(message_text_field);
        
        JTextField call_text_field = new JTextField("回复留言");
        call_text_field.setBounds(600, 10, 240, 20);
        call_text_field.setEnabled(false);
        call_text_field.setBorder(null);
        call_text_field.setBackground(Color.GRAY);
        call_text_field.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(call_text_field);
        
        JTextField time_text_field = new JTextField("时间");
        time_text_field.setBounds(830, 10, 190, 20);
        time_text_field.setEnabled(false);
        time_text_field.setBorder(null);
        time_text_field.setBackground(Color.GRAY);
        time_text_field.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(time_text_field);
    }

    private void createViewTabel(final JPanel panel) {
        jt.setBounds(20, 30, 1000, 600);
        panel.add(jt);
        
        jt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jt.setEnabled(false);
                if(e.getClickCount() == 2){
                    int rowIndex = jt.getSelectedRow();
                    int[] selectedColumnIndexs = {0,1,2,3,4};
                    List<String> rows = new ArrayList<String>();
                    for (int columnIndex : selectedColumnIndexs) {
                        rows.add(jt.getValueAt(rowIndex == -1?0:rowIndex, columnIndex == -1?0:columnIndex).toString());
                    }
                    //弹出编辑窗口
                    createEditFrame(rows);
                    
                }
            }
        });
    }

    private void createPageDesLabel(final JPanel panel, JLabel page_des_label) {
        page_des_label.setBounds(800, 625, 200, 30);
        page_des_label.setFont(new Font("黑体",Font.BOLD,12));
        String desc =  DESC_LABEL_TEXT.replaceAll("currentPage", Integer.toString(currentPage+1));// beging from 0
        desc = desc.replaceAll("pageCount", Integer.toString(pageCount));
        page_des_label.setText(desc);
        panel.add(page_des_label);
        
    }

    private void createLastPageButton(final JPanel panel, JButton first_page_button, JButton pre_page_button,
            JButton next_page_button, JButton last_page_button, JLabel page_des_label) {
        last_page_button.setBounds(570, 640, 80, 30);
        last_page_button.setFont(new Font("黑体",Font.BOLD,12));
        panel.add(last_page_button);
        last_page_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentPage <0 || currentPage > pageCount-1){
                    javax.swing.JOptionPane.showMessageDialog(ManagerFrame.this, "last page error "+currentPage);
                    return;
                }
                currentPage = pageCount-1;
                rowData.removeAllElements();
                //query first page data from server
                // TODO
                try {
                    queryDataFromServer(rowData,columnName,currentPage);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                /*
                 * 构造数据 test                
                for(int i=0;i<30;i++){
                    Vector rows = new Vector();
                    for (int j = 0; j < 5; j++) {
                        rows.add((currentPage+1)+" page data "+i+" "+j);
                    }
                    rowData.add(rows);
                }*/
                //refresh data to JTable
                jt.validate();
                jt.updateUI();
                
                String desc =  DESC_LABEL_TEXT.replaceAll("currentPage", Integer.toString(currentPage+1));// beging from 0
                desc = desc.replaceAll("pageCount", Integer.toString(pageCount));
                page_des_label.setText(desc);
                
                changeButtonEnabled(first_page_button, pre_page_button, next_page_button, last_page_button);
            }
        });
    }

    private void createNextPageButton(final JPanel panel, JButton first_page_button, JButton pre_page_button,
            JButton next_page_button, JButton last_page_button, JLabel page_des_label) {
        next_page_button.setBounds(480, 640, 80, 30);
        next_page_button.setFont(new Font("黑体",Font.BOLD,12));
        panel.add(next_page_button);
        next_page_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentPage<0 || currentPage >= pageCount-1){
                    javax.swing.JOptionPane.showMessageDialog(ManagerFrame.this, "next page error "+currentPage);
                    return;
                }
                currentPage++;
                rowData.removeAllElements();
                //query first page data from server
                // TODO
                try {
                    queryDataFromServer(rowData,columnName,currentPage);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                /*
                 * 构造数据 test
                for(int i=0;i<30;i++){
                    Vector rows = new Vector();
                    for (int j = 0; j < 5; j++) {
                        rows.add((currentPage+1)+" page data "+i+" "+j);
                    }
                    rowData.add(rows);
                }*/
                //refresh data to JTable
                jt.validate();
                jt.updateUI();
                
                String desc  =  DESC_LABEL_TEXT.replaceAll("currentPage", Integer.toString(currentPage+1));// beging from 0
                desc = desc.replaceAll("pageCount", Integer.toString(pageCount));
                page_des_label.setText(desc);
                
                changeButtonEnabled(first_page_button, pre_page_button, next_page_button, last_page_button);
            }
        });
    }

    private void createPrePageButton(final JPanel panel, JButton first_page_button, JButton pre_page_button,
            JButton next_page_button, JButton last_page_button, JLabel page_des_label) {
        pre_page_button.setBounds(390, 640, 80, 30);
        pre_page_button.setFont(new Font("黑体",Font.BOLD,12));
        panel.add(pre_page_button);
        pre_page_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentPage <= 0){
                    return;
                }
                
                if(currentPage>0){
                    currentPage--;
                }
                rowData.removeAllElements();
                //query first page data from server
                // TODO
                try {
                    queryDataFromServer(rowData,columnName,currentPage);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                /*
                 * 构造数据 test
                for(int i=0;i<30;i++){
                    Vector rows = new Vector();
                    for (int j = 0; j < 5; j++) {
                        rows.add((currentPage+1)+" page data "+i+" "+j);
                    }
                    rowData.add(rows);
                }*/
                //refresh data to JTable
                jt.validate();
                jt.updateUI();
                
                String desc   =  DESC_LABEL_TEXT.replaceAll("currentPage", Integer.toString(currentPage+1));// beging from 0
                desc = desc.replaceAll("pageCount", Integer.toString(pageCount));
                page_des_label.setText(desc);
                
                changeButtonEnabled(first_page_button, pre_page_button, next_page_button, last_page_button);
            }
        });
    }

    private void createFirstPageButton(final JPanel panel, JButton first_page_button, JButton pre_page_button,
            JButton next_page_button, JButton last_page_button, JLabel page_des_label) {
        first_page_button.setBounds(300, 640, 80, 30);
        first_page_button.setFont(new Font("黑体",Font.BOLD,12));
        panel.add(first_page_button);
        first_page_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = 0;
                rowData.removeAllElements();
                //query first page data from server
                // TODO
                try {
                    queryDataFromServer(rowData,columnName,currentPage);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                /*构造数据 test
                 * for(int i=0;i<30;i++){
                    Vector rows = new Vector();
                    for (int j = 0; j < 5; j++) {
                        rows.add((currentPage+1)+" page data "+i+" "+j);
                    }
                    rowData.add(rows);
                }*/
                //refresh data to JTable
                jt.validate();
                jt.updateUI();
                
                String desc   =  DESC_LABEL_TEXT.replaceAll("currentPage", Integer.toString(currentPage+1));// beging from 0
                desc = desc.replaceAll("pageCount", Integer.toString(pageCount));
                page_des_label.setText(desc);
                
                changeButtonEnabled(first_page_button, pre_page_button, next_page_button, last_page_button);
            }
        });
    }

    private void changeButtonEnabled(JButton first_page_button, JButton pre_page_button,
            JButton next_page_button, JButton last_page_button) {
        if(currentPage == 0) {//初始页面 为第一页 时禁用 首页按钮 与上一页按钮
            if(!last_page_button.isEnabled()) {//当前页是末页，点击首页按钮
                last_page_button.setEnabled(true);
            }
            if(!next_page_button.isEnabled()){
                next_page_button.setEnabled(true);
            }
            first_page_button.setEnabled(false);
            pre_page_button.setEnabled(false);
        }
        if(currentPage > 0) {// 如果在中间页 则 首页与上一页按钮可用
            first_page_button.setEnabled(true);
            pre_page_button.setEnabled(true);
        }
        if(currentPage == pageCount-1) {//如果当前页为最后一页  则禁用 末页 与下一页按钮
            last_page_button.setEnabled(false);
            next_page_button.setEnabled(false);
        }
        if(currentPage <  pageCount-1 && !last_page_button.isEnabled()) {
            last_page_button.setEnabled(true);
            next_page_button.setEnabled(true);
        }
    }

    protected void createEditFrame(List<String> rows) {
        message_frame_title = "编辑留言";
        if(newEditFrameCount == 0){
            newEditFrameCount++;
            if(isSaveFrameOpen){
                return;
            }
            createFrame();
        }else if(newEditFrameCount >= 1){
            return;
        }
        id_text_field.setText(rows.get(0));
        name_field.setText(rows.get(1));
        message_area.setText(rows.get(2));
        call_area.setText(rows.get(3));
    }

    private void queryDataFromServerByName(Vector rowData, Vector columnName,int currentPage,String name) throws IOException {
        messageDos.writeUTF(Request.PAGE_QUERY_MESSAGE);
        String response = messageDis.readUTF();
        if(Request.PAGE_QUERY_MESSAGE.equals(response)){
            Message message = new Message();
            MessageVO messageVO = new MessageVO();
            messageVO.setName(name);
            message.setMessageVO(messageVO);
            message.setCurrentPage(currentPage);
            String messStr = JSON.toJSONString(message);
            messageDos.writeUTF(messStr);
            String mess = messageDis.readUTF();
            JSONObject jsonObj = (JSONObject) JSON.parse(mess);
            JSONArray rowDatas = (JSONArray) jsonObj.get("rowData");
            for (Object rowDataObj : rowDatas) {
                Vector rowLineData = new Vector();
                JSONArray jsonLine = (JSONArray)rowDataObj;
                for (Object object : jsonLine) {
                    rowLineData.add(object.toString());
                }
                rowData.add(rowLineData);
            }
            JSONArray columnNameArr = (JSONArray) jsonObj.get("columnName");
            for (Object obj : columnNameArr) {
                columnName.add(obj.toString());
            }
            this.pageCount = Integer.parseInt(jsonObj.get("pageCount").toString());
        }
    }
    
    private void queryDataFromServer(Vector rowData, Vector columnName,int currentPage) throws IOException {
        messageDos.writeUTF(Request.PAGE_QUERY_MESSAGE);
        String response = messageDis.readUTF();
        if(Request.PAGE_QUERY_MESSAGE.equals(response)){
            Message message = new Message();
            message.setCurrentPage(currentPage);
            String messStr = JSON.toJSONString(message);
            messageDos.writeUTF(messStr);
            String mess = messageDis.readUTF();
            JSONObject jsonObj = (JSONObject) JSON.parse(mess);
            JSONArray rowDatas = (JSONArray) jsonObj.get("rowData");
            for (Object rowDataObj : rowDatas) {
                Vector rowLineData = new Vector();
                JSONArray jsonLine = (JSONArray)rowDataObj;
                for (Object object : jsonLine) {
                    rowLineData.add(object.toString());
                }
                rowData.add(rowLineData);
            }
            JSONArray columnNameArr = (JSONArray) jsonObj.get("columnName");
            for (Object obj : columnNameArr) {
                columnName.add(obj.toString());
            }
            this.pageCount = Integer.parseInt(jsonObj.get("pageCount").toString());
        }
    }


    private void createMenuBar(JPanel leftPanel,JPanel rightPanel) {
        JLabel manager_label = new JLabel("> 留言");
        manager_label.setFont(new Font("宋体",Font.PLAIN,12));
        manager_label.setBounds(10, 20, 180, 30);
        leftPanel.add(manager_label);
        
        JButton manage_leaving_button = new JButton("留言管理");
        manage_leaving_button.setFont(new Font("宋体",Font.PLAIN,12));
        manage_leaving_button.setForeground(Color.WHITE);
        manage_leaving_button.setBounds(10, 50, 180, 30);
        manage_leaving_button.setBorder(null);
        manage_leaving_button.setBackground(Color.GRAY);
        leftPanel.add(manage_leaving_button);
        manage_leaving_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
}

