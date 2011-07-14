package pf.io.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pf.main.MainDescriptiveMatcher;
import pf.main.MainFunctionalMatcher;
import pf.vo.DescriptiveData;
import pf.vo.FunctionalData;

public class MainFrameUI extends JFrame implements ActionListener {

	private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="10,54"
	private JPanel jPanel = null;
	private JTextField txtRequest = null;
	private JButton btnRequest = null;
	private JLabel jLabel1 = null;
	private JTextField txtService = null;
	private JLabel jLabel2 = null;
	private JButton btnService = null;
	private JPanel jPanel1 = null;
	private JCheckBox chkExact = null;
	private JCheckBox chkPlugin = null;
	private JCheckBox chkSubsumes = null;
	private JCheckBox chkSibling = null;
	private JCheckBox chkFail = null;
	private JCheckBox chkHybrid = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel17 = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JLabel jLabel7 = null;
	private JButton btnFunctional = null;
	private JPanel jPanel2 = null;
	private JTextField txtCoefBasica = null;
	private JLabel jLabel8 = null;
	private JLabel jLabel9 = null;
	private JLabel jLabel10 = null;
	private JLabel jLabel11 = null;
	private JLabel jLabel12 = null;
	private JLabel jLabel13 = null;
	private JLabel jLabel16 = null;
	private JTextField txtCoefEstrut = null;
	private JTextField txtCoefAnces = null;
	private JSlider jSlider = null;
	private JTextField txtCoefFilhos = null;
	private JTextField txtCoefIrmaos = null;
	private JTextField txtCoefFolhas = null;
	private JLabel jLabel14 = null;
	private JLabel jLabel15 = null;
	private JButton btnDescriptive = null;
	private JPanel jPanel3 = null;
	private JTextArea txtResult = null;
	private JButton btnExport = null;
	private JButton btnClean = null;
	private JTextField txtDictionary = null;
	private JLabel lblDictionary = null;
	private JButton btnDictionary = null;
	private JFileChooser fileChooser = null;
	private JScrollPane jScrollPane = null;
	
	//Lore
	private JPanel jPanel4 = null;
	private JLabel jLabel18 = null;
	private JLabel jLabel19 = null;
	private JLabel jLabel20 = null;
	private JLabel jLabel21 = null;
	private JLabel jLabel22 = null;
	private JCheckBox chkExactPE = null;
	private JCheckBox chkPluginPE = null;
	private JCheckBox chkSubsumesPE = null;
	private JCheckBox chkSiblingPE = null;
	private JCheckBox chkFailPE = null;
	
	
	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	public void getJFrame() {
		try {
		      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    } catch(Exception e) {
		    	
		    }
		setSize(new Dimension(795,665));
		setTitle("OWL-S Discovery - v1.0");
		setContentPane(getJContentPane());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel15 = new JLabel();
			jLabel15.setText("0.0..............0.5..............1.0");
			jLabel15.setBounds(new Rectangle(14, 163, 160, 16));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.setSize(new Dimension(563, 674));
			jContentPane.add(getJPanel(), null);
			jContentPane.add(getJPanel1(), null);
			jContentPane.add(getJPanel2(), null);
			jContentPane.add(getJPanel3(), null);
			//Lore
			jContentPane.add(getJPanel4(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			lblDictionary = new JLabel();
			lblDictionary.setBounds(new Rectangle(15, 113, 58, 16));
			lblDictionary.setText("Dicion?rio");
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(16, 68, 53, 16));
			jLabel2.setText("Servi?os");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(16, 23, 65, 16));
			jLabel1.setText("Requisi??o");
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.setBorder(BorderFactory.createTitledBorder(null, "Entrada", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11), new Color(51, 51, 51)));
			jPanel.setBounds(new Rectangle(16, 15, 751, 175));
			jPanel.add(getJTextField1(), null);
			jPanel.add(getJButton1(), null);
			jPanel.add(jLabel1, null);
			jPanel.add(getJTextField2(), null);
			jPanel.add(jLabel2, null);
			jPanel.add(getJButton2(), null);
			jPanel.add(getJTextField9(), null);
			jPanel.add(lblDictionary, null);
			jPanel.add(getJButton21(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField1() {
		if (txtRequest == null) {
			txtRequest = new JTextField();
			txtRequest.setBounds(new Rectangle(15, 42, 464, 20));
		}
		return txtRequest;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (btnRequest == null) {
			btnRequest = new JButton();
			btnRequest.setBounds(new Rectangle(491, 30, 40, 40));
			btnRequest.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/kappfinder.png"))));
			btnRequest.setToolTipText("Carregar Requisi??o");
			btnRequest.addActionListener(this);
		}
		return btnRequest;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField2() {
		if (txtService == null) {
			txtService = new JTextField();
			txtService.setBounds(new Rectangle(16, 85, 463, 20));
		}
		return txtService;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (btnService == null) {
			btnService = new JButton();
			btnService.setBounds(new Rectangle(491, 73, 40, 40));
			btnService.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/kappfinder.png"))));
			btnService.setToolTipText("Carregar Servi?os");
			btnService.addActionListener(this);
		}
		return btnService;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel7 = new JLabel();
			jLabel7.setBounds(new Rectangle(45, 153, 21, 16));
			jLabel7.setText("Fail");
			jLabel6 = new JLabel();
			jLabel6.setBounds(new Rectangle(45, 123, 40, 16));
			jLabel6.setText("Sibling");
			jLabel5 = new JLabel();
			jLabel5.setBounds(new Rectangle(45, 93, 63, 16));
			jLabel5.setText("Subsumes");
			jLabel4 = new JLabel();
			jLabel4.setBounds(new Rectangle(45, 63, 47, 16));
			jLabel4.setText("Plug-in");
			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(45, 33, 35, 16));
			jLabel3.setText("Exact");
			jLabel17 = new JLabel();
			jLabel17.setBounds(new Rectangle(139, 33, 63, 16));
			jLabel17.setText("H?brido");
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			jPanel1.setBounds(new Rectangle(220, 192, 194, 189));
			jPanel1.setBorder(BorderFactory.createTitledBorder(null, "Sem?ntico Funcional", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11), new Color(51, 51, 51)));
			jPanel1.add(getJCheckBox(), null);
			jPanel1.add(getJCheckBox1(), null);
			jPanel1.add(getJCheckBox2(), null);
			jPanel1.add(getJCheckBox3(), null);
			jPanel1.add(getJCheckBox4(), null);
			jPanel1.add(getJCheckBox5(), null);
			jPanel1.add(jLabel3, null);
			jPanel1.add(jLabel4, null);
			jPanel1.add(jLabel5, null);
			jPanel1.add(jLabel6, null);
			jPanel1.add(jLabel7, null);
			jPanel1.add(jLabel17, null);
			jPanel1.add(getJButton3(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox() {
		if (chkExact == null) {
			chkExact = new JCheckBox();
			chkExact.setBounds(new Rectangle(15, 30, 21, 21));
		}
		return chkExact;
	}

	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox5() {
		if (chkHybrid == null) {
			chkHybrid = new JCheckBox();
			chkHybrid.setBounds(new Rectangle(110, 30, 21, 21));
		}
		return chkHybrid;
	}
	
	/**
	 * This method initializes jCheckBox1	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox1() {
		if (chkPlugin == null) {
			chkPlugin = new JCheckBox();
			chkPlugin.setBounds(new Rectangle(15, 60, 21, 21));
		}
		return chkPlugin;
	}

	/**
	 * This method initializes jCheckBox2	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox2() {
		if (chkSubsumes == null) {
			chkSubsumes = new JCheckBox();
			chkSubsumes.setBounds(new Rectangle(15, 90, 21, 21));
		}
		return chkSubsumes;
	}

	/**
	 * This method initializes jCheckBox3	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox3() {
		if (chkSibling == null) {
			chkSibling = new JCheckBox();
			chkSibling.setBounds(new Rectangle(15, 120, 21, 21));
		}
		return chkSibling;
	}

	/**
	 * This method initializes jCheckBox4	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox4() {
		if (chkFail == null) {
			chkFail = new JCheckBox();
			chkFail.setBounds(new Rectangle(15, 150, 21, 21));
		}
		return chkFail;
	}
	
	/**
	 * This method initializes jCheckBox6	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox6() {
		if (chkExactPE == null) {
			chkExactPE = new JCheckBox();
			chkExactPE.setBounds(new Rectangle(15, 30, 21, 21));
		}
		return chkExactPE;
	}
	
	/**
	 * This method initializes jCheckBox7	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox7() {
		if (chkPluginPE == null) {
			chkPluginPE = new JCheckBox();
			chkPluginPE.setBounds(new Rectangle(15, 60, 21, 21));
		}
		return chkPluginPE;
	}

	/**
	 * This method initializes jCheckBox8	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox8() {
		if (chkSubsumesPE == null) {
			chkSubsumesPE = new JCheckBox();
			chkSubsumesPE.setBounds(new Rectangle(15, 90, 21, 21));
		}
		return chkSubsumesPE;
	}

	/**
	 * This method initializes jCheckBox9	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox9() {
		if (chkSiblingPE == null) {
			chkSiblingPE = new JCheckBox();
			chkSiblingPE.setBounds(new Rectangle(15, 120, 21, 21));
		}
		return chkSiblingPE;
	}

	/**
	 * This method initializes jCheckBox10	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox10() {
		if (chkFailPE == null) {
			chkFailPE = new JCheckBox();
			chkFailPE.setBounds(new Rectangle(15, 150, 21, 21));
		}
		return chkFailPE;
	}

	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton3() {
		if (btnFunctional == null) {
			btnFunctional = new JButton();
			btnFunctional.setBounds(new Rectangle(111, 157, 75, 25));
			btnFunctional.setText("Buscar");
			btnFunctional.addActionListener(this);
		}
		return btnFunctional;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			TitledBorder titledBorder = BorderFactory.createTitledBorder(null, "Sem?ntico Descritivo", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51));
			titledBorder.setTitleFont(new Font("Tahoma", Font.PLAIN, 11));
			jLabel14 = new JLabel();
			jLabel14.setBounds(new Rectangle(16, 125, 38, 16));
			jLabel14.setText("Limiar");
			jLabel13 = new JLabel();
			jLabel13.setBounds(new Rectangle(15, 75, 66, 16));
			jLabel13.setText("Coef. Filhos");
			jLabel12 = new JLabel();
			jLabel12.setBounds(new Rectangle(210, 75, 70, 16));
			jLabel12.setText("Coef. Folhas");
			jLabel11 = new JLabel();
			jLabel11.setBounds(new Rectangle(105, 75, 75, 16));
			jLabel11.setText("Coef. Irm?os");
			jLabel10 = new JLabel();
			jLabel10.setBounds(new Rectangle(210, 22, 97, 16));
			jLabel10.setText("Coef. Ancestrais");
			jLabel9 = new JLabel();
			jLabel9.setBounds(new Rectangle(105, 22, 90, 16));
			jLabel9.setText("Coef. Estrutural");
			jLabel8 = new JLabel();
			jLabel8.setBounds(new Rectangle(15, 22, 76, 16));
			jLabel8.setText("Coef. B?sica");
			jPanel2 = new JPanel();
			jPanel2.setLayout(null);
			jPanel2.setBounds(new Rectangle(420, 192, 347, 189));
			jPanel2.setBorder(titledBorder);
			jPanel2.add(getJTextField3(), null);
			jPanel2.add(jLabel8, null);
			jPanel2.add(jLabel9, null);
			jPanel2.add(jLabel10, null);
			jPanel2.add(jLabel11, null);
			jPanel2.add(jLabel12, null);
			jPanel2.add(jLabel13, null);
			jPanel2.add(getJTextField4(), null);
			jPanel2.add(getJTextField5(), null);
			jPanel2.add(getJSlider(), null);
			jLabel16 = new JLabel();
			jLabel16.setBounds(new Rectangle(180, 144, 38, 16));
			jLabel16.setFont(new Font("Tahoma", Font.BOLD, 11));
			jLabel16.setText(String.valueOf(Double.parseDouble(String.valueOf(jSlider.getValue()))/100));
			jPanel2.add(getJTextField6(), null);
			jPanel2.add(getJTextField7(), null);
			jPanel2.add(getJTextField8(), null);
			jPanel2.add(jLabel14, null);
			jPanel2.add(jLabel15, null);
			jPanel2.add(jLabel16, null);
			jPanel2.add(getJButton4(), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField3() {
		if (txtCoefBasica == null) {
			try {
				javax.swing.text.MaskFormatter format_textField = new javax.swing.text.MaskFormatter(
						"#.##");
				format_textField.setValidCharacters("0123456789.");
				txtCoefBasica = new javax.swing.JFormattedTextField(format_textField);
				txtCoefBasica.setBounds(new Rectangle(15, 40, 35, 20));
			} catch (Exception e) {
			}
		}
		return txtCoefBasica;
	}

	/**
	 * This method initializes jTextField4	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField4() {
		if (txtCoefEstrut == null) {
			try {
				javax.swing.text.MaskFormatter format_textField = new javax.swing.text.MaskFormatter(
						"#.##");
				format_textField.setValidCharacters("0123456789.");
				txtCoefEstrut = new javax.swing.JFormattedTextField(format_textField);
				txtCoefEstrut.setBounds(new Rectangle(105, 40, 35, 20));
			} catch (Exception e) {
			}
		}
		return txtCoefEstrut;
	}

	/**
	 * This method initializes jTextField5	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField5() {
		if (txtCoefAnces == null) {
			try {
				javax.swing.text.MaskFormatter format_textField = new javax.swing.text.MaskFormatter(
						"#.##");
				format_textField.setValidCharacters("0123456789.");
				txtCoefAnces = new javax.swing.JFormattedTextField(format_textField);
				txtCoefAnces.setBounds(new Rectangle(210, 40, 35, 20));
			} catch (Exception e) {
			}
		}
		return txtCoefAnces;
	}

	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getJSlider() {
		if (jSlider == null) {
			jSlider = new JSlider();
			jSlider.setBounds(new Rectangle(10, 143, 166, 22));
			jSlider.addChangeListener( new ChangeListener() {
				public void stateChanged( ChangeEvent evt) {
					jSliderStateChanged( evt );
				}
			});	
		}
		return jSlider;
	}
	
	private void jSliderStateChanged(ChangeEvent evt) {
		jLabel16.setText(String.valueOf(Double.parseDouble(String.valueOf(jSlider.getValue()))/100));
	}

	/**
	 * This method initializes jTextField6	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField6() {
		if (txtCoefFilhos == null) {
			try {
				javax.swing.text.MaskFormatter format_textField = new javax.swing.text.MaskFormatter(
						"#.##");
				format_textField.setValidCharacters("0123456789.");
				txtCoefFilhos = new javax.swing.JFormattedTextField(format_textField);
				txtCoefFilhos.setBounds(new Rectangle(15, 93, 35, 20));
			} catch (Exception e) {
			}
		}
		return txtCoefFilhos;
	}

	/**
	 * This method initializes jTextField7	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField7() {
		if (txtCoefIrmaos == null) {
			try {
				javax.swing.text.MaskFormatter format_textField = new javax.swing.text.MaskFormatter(
						"#.##");
				format_textField.setValidCharacters("0123456789.");
				txtCoefIrmaos = new javax.swing.JFormattedTextField(format_textField);
				txtCoefIrmaos.setBounds(new Rectangle(105, 93, 35, 20));
			} catch (Exception e) {
			}
		}
		return txtCoefIrmaos;
	}

	/**
	 * This method initializes jTextField8	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField8() {
		if (txtCoefFolhas == null) {
			try {
				javax.swing.text.MaskFormatter format_textField = new javax.swing.text.MaskFormatter(
						"#.##");
				format_textField.setValidCharacters("0123456789.");
				txtCoefFolhas = new javax.swing.JFormattedTextField(format_textField);
				txtCoefFolhas.setBounds(new Rectangle(210, 93, 35, 20));
			} catch (Exception e) {
			}
		}
		return txtCoefFolhas;
	}


	//Lore
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jLabel22 = new JLabel();
			jLabel22.setBounds(new Rectangle(45, 153, 21, 16));
			jLabel22.setText("Fail");
			jLabel21 = new JLabel();
			jLabel21.setBounds(new Rectangle(45, 123, 40, 16));
			jLabel21.setText("Sibling");
			jLabel20 = new JLabel();
			jLabel20.setBounds(new Rectangle(45, 93, 63, 16));
			jLabel20.setText("Subsumes");
			jLabel19 = new JLabel();
			jLabel19.setBounds(new Rectangle(45, 63, 47, 16));
			jLabel19.setText("Plug-in");
			jLabel18 = new JLabel();
			jLabel18.setBounds(new Rectangle(45, 33, 35, 16));
			jLabel18.setText("Exact");
			jPanel4 = new JPanel();
			jPanel4.setLayout(null);
			jPanel4.setBounds(new Rectangle(16, 192, 195, 189));
			jPanel4.setBorder(BorderFactory.createTitledBorder(null, "Pr?-Condi??es e Efeitos", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11), new Color(51, 51, 51)));
			jPanel4.add(getJCheckBox6(), null);
			jPanel4.add(getJCheckBox7(), null);
			jPanel4.add(getJCheckBox8(), null);
			jPanel4.add(getJCheckBox9(), null);
			jPanel4.add(getJCheckBox10(), null);
			jPanel4.add(jLabel18, null);
			jPanel4.add(jLabel19, null);
			jPanel4.add(jLabel20, null);
			jPanel4.add(jLabel21, null);
			jPanel4.add(jLabel22, null);	
		}
		return jPanel4;
	}
	
	
	
	/**
	 * This method initializes jButton4	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton4() {
		if (btnDescriptive == null) {
			btnDescriptive = new JButton();
			btnDescriptive.setBounds(new Rectangle(263, 157, 75, 25));
			btnDescriptive.setText("Buscar");
			btnDescriptive.addActionListener(this);
		}
		return btnDescriptive;
	}


	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(null);
			jPanel3.setBounds(new Rectangle(16, 382, 752, 230));
			jPanel3.setBorder(BorderFactory.createTitledBorder(null, "Sa?da", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11), new Color(51, 51, 51)));
			jPanel3.add(getJTextPane(), null);
			jPanel3.add(getJButton5(), null);
			jPanel3.add(getJButton6(), null);
			jPanel3.add(getJScrollPane(), null);
		}
		return jPanel3;
	}

	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextArea getJTextPane() {
		if (txtResult == null) {
			txtResult = new JTextArea();
			txtResult.setFont(new Font("Tahoma", Font.PLAIN, 11));
			txtResult.setEnabled(false);
		}
		return txtResult;
	}
	
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(12, 16, 524, 176));
			jScrollPane.setViewportView(getJTextPane());
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jButton5	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton5() {
		if (btnExport == null) {
			btnExport = new JButton();
			btnExport.setBounds(new Rectangle(463, 198, 75, 25));
			btnExport.setText("Exportar");
			btnExport.addActionListener(this);
			btnExport.setEnabled(false);
		}
		return btnExport;
	}

	/**
	 * This method initializes jButton6	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton6() {
		if (btnClean == null) {
			btnClean = new JButton();
			btnClean.setBounds(new Rectangle(383, 198, 75, 25));
			btnClean.setText("Limpar");
			btnClean.addActionListener(this);
		}
		return btnClean;
	}
	
	/**
	 * This method initializes jTextField9	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField9() {
		if (txtDictionary == null) {
			txtDictionary = new JTextField();
			txtDictionary.setBounds(new Rectangle(15, 130, 465, 20));
		}
		return txtDictionary;
	}

	/**
	 * This method initializes jButton21	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton21() {
		if (btnDictionary == null) {
			btnDictionary = new JButton();
			btnDictionary.setBounds(new Rectangle(491, 117, 41, 42));
			btnDictionary.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/kappfinder.png"))));
			btnDictionary.setToolTipText("Carregar Dicion?rio");
			btnDictionary.addActionListener(this);
		}
		return btnDictionary;
	}
	
	/**
	 * @return the fileChooser
	 */
	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	/**
	 * @param fileChooser the fileChooser to set
	 */
	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	// Events
	
	public void actionPerformed(ActionEvent e) {
//		Lore
		this.txtRequest.setText("C:\\xampp\\htdocs\\queries\\testes\\r9.owls");
		this.txtService.setText("C:\\xampp\\htdocs\\services\\testes1");
		
		if (e.getSource() == btnRequest) {
			fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
			      public boolean accept(File f){
			        return f.getName().toLowerCase().endsWith(".owls") || f.isDirectory();
			      }

			      public String getDescription() {
			        return "Descritor de Requesi??o (.owls)";
			      }
			    });
			int returnVal = fileChooser.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
                                //efraim descomentou a linha de baixo
//				Lore - Comentado para nao rpecisar selecionar o caminho
				//this.txtRequest.setText(file.getPath());
			}

		} else if (e.getSource() == btnService) {
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fileChooser.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
//				Lore - Comentado para nao rpecisar selecionar o caminho
				//this.txtService.setText(file.getPath());
				
			}
		} else if (e.getSource() == btnDictionary) {
			fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
			      public boolean accept(File f){
			        return f.getName().toLowerCase().endsWith(".dic") || f.isDirectory();
			      }

			      public String getDescription() {
			        return "Dictionary (.dic)";
			      }
			    });
			int returnVal = fileChooser.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				this.txtDictionary.setText(file.getPath());
				
			}
		} else if (e.getSource() == btnFunctional) {
			btnExport.setEnabled(false);
			txtResult.setText("");
			// This variable enable or disable the discovery algorithm
			boolean isChecked = true;
			/*
			 * Coding a validation
			 */
			if(chkHybrid.isSelected() == true) {
				if (txtRequest.getText().trim().equals("") || txtService.getText().trim().equals("") || 
					txtCoefAnces.getText().trim().equals("") || txtCoefBasica.getText().trim().equals("") ||
					txtCoefEstrut.getText().trim().equals("") || txtCoefFilhos.getText().trim().equals("") ||
					txtCoefFolhas.getText().trim().equals("") || txtCoefIrmaos.getText().trim().equals("") ||
					txtDictionary.getText().trim().equals("")) {
					txtResult.setText("ERROR\n\nAll fields are required.");
					isChecked = false;
				}
			}
				
//			 else if(txtRequest.getText().trim().equals("") || txtService.getText().trim().equals("")) {
//				/*
//				 * Creating the message error
//				 */
//				txtResult.setText("ERROR\n\nThe Request or the Service directory weren't specified.");
//				isChecked = false;
//			}
			if (isChecked == true) {
				String[] arg = new String[2];
				arg[0] = txtRequest.getText();
				arg[1] = txtService.getText();
				
				String[] filter = new String[5];
				if(chkExact.isSelected())
					filter[0] = "EXACT";
				if(chkPlugin.isSelected())
					filter[1] = "PLUG IN";
				if(chkSubsumes.isSelected())
					filter[2] = "SUBSUMES";
				if(chkSibling.isSelected())
					filter[3] = "SIBLING";
				if(chkFail.isSelected())
					filter[4] = "FAIL";
				
				
				//Lore INICIO
				String[] filterPE = new String[5];
				boolean PETreatment = false;
				if(chkExactPE.isSelected()) {
					filterPE[0] = "EXACT";
					PETreatment = true;				
				}
				if(chkPluginPE.isSelected()) {
					filterPE[1] = "PLUG IN";
					PETreatment = true;
				}
				if(chkSubsumesPE.isSelected()) {
					filterPE[2] = "SUBSUMES";
					PETreatment = true;
				}
				if(chkSiblingPE.isSelected()){
					filterPE[3] = "SIBLING";
					PETreatment = true;
				}
				if(chkFailPE.isSelected()){
					filterPE[4] = "FAIL";
					PETreatment = true;
				}
							    
								
				//Lore FIM
				
				DescriptiveData descriptiveData = null;
				if (chkHybrid.isSelected() == true) {
					descriptiveData = new DescriptiveData();
					descriptiveData.setBasicCoefficient(Double.parseDouble(txtCoefBasica.getText().trim()));
					descriptiveData.setStructuralCoefficient(Double.parseDouble(txtCoefEstrut.getText().trim()));
					descriptiveData.setAncestorCoefficient(Double.parseDouble(txtCoefAnces.getText().trim()));
					descriptiveData.setImmediateChldCoefficient(Double.parseDouble(txtCoefFilhos.getText().trim()));
					descriptiveData.setLeafCoefficient(Double.parseDouble(txtCoefFolhas.getText().trim()));
					descriptiveData.setSiblingCoefficient(Double.parseDouble(txtCoefIrmaos.getText().trim()));
					descriptiveData.setThreshold(Double.parseDouble(String.valueOf(jSlider.getValue()))/100);
					descriptiveData.setDicitonaryPath(txtDictionary.getText().trim());
				}
				
				FunctionalData functionalData = new FunctionalData();
				functionalData.setFilters(filter);
				functionalData.setFiltersPE(filterPE);
				functionalData.setHybridTreatment(chkHybrid.isSelected());
//				Lore
				functionalData.setPETreatment(PETreatment);
				
//				MainFunctionalMatcher.invoke(arg, functionalData, descriptiveData, txtResult);
				
				try { //Adicionado o Try Catch pq tava dando erro na hroa de compilar!
					MainFunctionalMatcher.invoke(arg, functionalData, descriptiveData, true, txtResult);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				btnExport.setEnabled(true);
			}
		} else if (e.getSource() == btnClean) {
			txtResult.setText("");
			btnExport.setEnabled(false);
		} else if (e.getSource() == btnDescriptive) {
			btnExport.setEnabled(false);
			txtResult.setText("");
			/*
			 * Coding a validation
			 */
			if (txtCoefAnces.getText().trim().equals("") || txtCoefBasica.getText().trim().equals("") || txtCoefEstrut.getText().trim().equals("") ||
			   txtCoefFilhos.getText().trim().equals("") || txtCoefFolhas.getText().trim().equals("") || txtCoefIrmaos.getText().trim().equals("")) {
				txtResult.setText("ERROR\n\nAll the coeficients has to be specified.");
			} else if(txtRequest.getText().trim().equals("") || txtService.getText().trim().equals("") || txtDictionary.getText().trim().equals("")) {
				txtResult.setText("ERROR\n\nThe Dictionary directory, the Request or the Service directory weren't specified.");
			} else if ((Double.parseDouble(txtCoefBasica.getText().trim()) +  Double.parseDouble(txtCoefEstrut.getText().trim())) != 1.0) {
					txtResult.setText("ERROR\n\n The add of the Basic Coeficient and the Structural Coeficient has to be equal 1 (one)");
			} else if ((Double.parseDouble(txtCoefAnces.getText().trim()) + Double.parseDouble(txtCoefFilhos.getText().trim()) +
					Double.parseDouble(txtCoefFolhas.getText().trim()) + Double.parseDouble(txtCoefIrmaos.getText().trim())) != 1.0) {
					txtResult.setText("ERROR\n\n The add of the Ancestor Coeficient, the Child Coeficient,\n" +
									  "Leaf Coeficient and Sibling Coeficient has to be equal 1 (one)");
			} else {
				String[] arg = new String[3];
				arg[0] = txtRequest.getText();
				arg[1] = txtService.getText();
				arg[2] = txtDictionary.getText();
				
				DescriptiveData descriptiveData = new DescriptiveData();
				descriptiveData.setBasicCoefficient(Double.parseDouble(txtCoefBasica.getText().trim()));
				descriptiveData.setStructuralCoefficient(Double.parseDouble(txtCoefEstrut.getText().trim()));
				descriptiveData.setAncestorCoefficient(Double.parseDouble(txtCoefAnces.getText().trim()));
				descriptiveData.setImmediateChldCoefficient(Double.parseDouble(txtCoefFilhos.getText().trim()));
				descriptiveData.setLeafCoefficient(Double.parseDouble(txtCoefFolhas.getText().trim()));
				descriptiveData.setSiblingCoefficient(Double.parseDouble(txtCoefIrmaos.getText().trim()));
				descriptiveData.setThreshold(Double.parseDouble(String.valueOf(jSlider.getValue()))/100);

//				MainDescriptiveMatcher.invoke(arg, descriptiveData, txtResult);
				
				try { //Try Catch adicionado pq tava dando erro na hora de compilar!!!
					MainDescriptiveMatcher.invoke(arg, descriptiveData, txtResult);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				btnExport.setEnabled(true);
			}

		} else if (e.getSource() == btnExport) {
			try{  
			    FileOutputStream out = new FileOutputStream("result.txt");  
			    PrintStream p = new PrintStream(out);  
			    p.print(txtResult.getText());  
			    p.close();
			    JOptionPane.showMessageDialog(null, "The result was exported to the file 'result.txt' in the root of the application!");
			    btnExport.setEnabled(false);
			  } catch(Exception exc) {  
			    System.err.println(exc);  
			  } 
		}
  
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrameUI frame = new MainFrameUI();
		frame.getJFrame();
		frame.setVisible(true);
	}
}
