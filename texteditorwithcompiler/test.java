
Author: xiao wang cwid:10427141

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
       
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

  
public class TextEditor extends JFrame  {
    public JTextPane textPane = new JTextPane(); 
    private JFileChooser fc = new JFileChooser(); 
    private JTextArea textArea = new JTextArea(20,60);
    private java.util.List<String> regexSplitLine;  
    private List<Integer> regexSplitLineInt;   
    private int cursorLineNum = 0;  
    private String strPath;
    public JTextArea textAreaResult;
	public JFileChooser filechooser = new JFileChooser();
    
    public TextEditor() {
      super("TextEditor");
        setSize(800,600);
      regexSplitLine = new ArrayList<>();
      regexSplitLineInt = new ArrayList<>();
      Font f = new Font("Helvetica", Font.BOLD, 18);
      textArea.setFont(f);
      textArea = new JTextArea(); textAreaResult = new JTextArea();
        Container container=getContentPane();	
      container.add(textArea, BorderLayout.CENTER);
		container.add(textAreaResult, BorderLayout.SOUTH);
      
      JScrollPane scrollPane=new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      FileFilter txtFilter=new FileNameExtensionFilter("Plain text","txt");
      fc.setFileFilter(txtFilter);
      
      regexSplitLine = new ArrayList<>();
      regexSplitLineInt = new ArrayList<>();
      textArea.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_F4:{
						if(!regexSplitLineInt.isEmpty()){
							showNextErrorLine();
						}
						break;
					}
				}
			}
      });
      add(scrollPane);
      JMenuBar menuBar=new JMenuBar();
      setJMenuBar(menuBar);
      JMenu file=new JMenu("File");
      menuBar.add(file);
      file.add(New);
      file.add(Open);
      file.add(Save);
      file.add(Quit);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      pack();
     
      
      JMenu edit=new JMenu("Edit");
      
      menuBar.add(edit);
      edit.add(Copy);
      edit.add(Cut);
      edit.add(Paste);
      pack();
      setVisible(true);
      
      JMenu Compile = new JMenu("Compile");
      menuBar.add(Compile);
      JMenuItem jmit = new JMenuItem("Compile");
      jmit.addActionListener(new ActionListenerCompile());
      Compile.add(jmit);
   
        
    };
     
    Action New=new AbstractAction("New file"){
         
		public void actionPerformed(ActionEvent e)
		{
			new TextEditor(); 
		}
      };
    
    
    Action Open=new AbstractAction("Open file"){
            @Override
             public void actionPerformed(ActionEvent e){
                 if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
                 File f=filechooser.getSelectedFile();
				strPath = f.getAbsolutePath();
				try
				{
					BufferedReader br = new BufferedReader(new FileReader(strPath));
					String line;
					StringBuilder sbContent = new StringBuilder();
					while ((line = br.readLine())!= null){
						sbContent.append(line+"\n");
					}
					textArea.setText(sbContent.toString());
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
                 }
            }
      };
    
    Action Save=new AbstractAction("Save file"){  
          @Override
          public void actionPerformed(ActionEvent e){
                 saveFile();
          }
    };
      
      
    Action Quit=new AbstractAction("Quit"){  
          @Override
          public void actionPerformed(ActionEvent e){
                 System.exit(0);
          }
    };  
    Action Copy=new AbstractAction("Copy"){
         
		public void actionPerformed(ActionEvent e)
		{
			copyFile(); 
		}
    };
    Action Cut=new AbstractAction("Cut"){
         
		public void actionPerformed(ActionEvent e)
		{
                        cutFile(); 
		}
    };
    Action Paste=new AbstractAction("Paste"){
         
		public void actionPerformed(ActionEvent e)
		{
			pasteFile(); 
		}
    };
    
    
    
    public void openFile(String fileName){
        FileReader fr=null;
        try{
            fr=new FileReader(fileName);
            textArea.read(fr,null);
            fr.close();
            setTitle(fileName);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void saveFile(){
        if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
        FileWriter fw=null;  
            try{
                fw=new FileWriter(fc.getSelectedFile().getAbsolutePath()+ ".txt");
                textArea.write(fw);
                fw.close();
                 }catch(IOException e){
                     e.printStackTrace();
                }
        }
    }
    
    public void copyFile(){
        textArea.copy();
        
    }
    public void cutFile(){
        textArea.cut();
        
    }
    public void pasteFile(){
        textArea.paste();
    }
    
    class ActionListenerCompile implements ActionListener {
		public void actionPerformed(ActionEvent e){
			try {
				if (strPath != null && strPath != "") {

					FileOutputStream out=new FileOutputStream(new File(strPath));
					out.write(textArea.getText().getBytes());
					Process proc1 = Runtime.getRuntime().exec("javac "+ strPath);
					BufferedReader br = new BufferedReader(new InputStreamReader(proc1.getErrorStream()));
					StringBuilder sb = new StringBuilder(); String line = "";
					while ((line = br.readLine()) != null){
						sb.append(line + "\n");
					}
					textAreaResult.setText(sb.toString());
					regexSplit(sb.toString());
					System.out.println(sb.toString());
				}
			}
			catch (Exception e4) {
				e4.printStackTrace();
			}
		}
	}
    
    public void showNextErrorLine(){
		try{
			int lineNum = regexSplitLineInt.get(cursorLineNum) - 1;
			int selectionStart = textArea.getLineStartOffset(lineNum);
			int selectionEnd = textArea.getLineEndOffset(lineNum);
			textArea.requestFocus();
			textArea.setSelectionStart(selectionStart);
			textArea.setSelectionEnd(selectionEnd);
			cursorLineNum++;
			if(cursorLineNum >= regexSplitLineInt.size())
				cursorLineNum = 0;
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
    public void regexSplit(String line){
		String regex = "(([a-zA-Z])\\w+.java:[0-9]+)";
		regexSplitLine.clear();
		Pattern ptn = Pattern.compile(regex);
		Matcher matcher = ptn.matcher(line);
		while(matcher.find()){
			regexSplitLine.add(matcher.group());
		}
		if(!regexSplitLine.isEmpty()){
			regexSplitLineInt.clear();
			for(String s : regexSplitLine){
				String sNum = s.substring(s.lastIndexOf(':') + 1, s.length());
				regexSplitLineInt.add(Integer.valueOf(sNum));
				System.out.println(sNum);
			}
		}
    }
    
    public static void main(String[] args) {
       new TextEditor(); 
    }
}
