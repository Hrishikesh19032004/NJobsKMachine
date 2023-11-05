import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class WelcomeGui extends JFrame {
    public WelcomeGui() {
        JFrame f = new JFrame();
        f.setTitle("JobScheduler");
        JLabel l1 = new JLabel();
        l1.setText("JobScheduler");
        l1.setFont(new Font("Book Antiqua", Font.PLAIN, 60));
        l1.setBounds(50, 50, 400, 100);
        JButton b1 = new JButton();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 300);
        b1.setText("Get Started");
        b1.setBackground(new Color(0xc3ced2));
        b1.setBounds(150, 200, 200, 50);

        f.setLayout(null);
        f.setVisible(true);
        f.add(l1);
        f.add(b1);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                f.dispose();
                new IntermediateGui();
            }
        });
    }
}

class IntermediateGui extends JFrame {
    private JTextField nomTextField;
    private JTextField nojTextField;
    private JButton nextButton;

    public IntermediateGui() {
        JLabel nomLabel = new JLabel("Enter the number of machines: ");
        nomTextField = new JTextField(5);
        JLabel nojLabel = new JLabel("Enter the number of jobs: ");
        nojTextField = new JTextField(5);
        nextButton = new JButton("Next");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(nomLabel);
        panel.add(nomTextField);
        panel.add(nojLabel);
        panel.add(nojTextField);
        panel.add(nextButton);

        this.setTitle("Input Dimensions");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 200);
        this.add(panel);
        this.setVisible(true);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int nom = Integer.parseInt(nomTextField.getText());
                    int noj = Integer.parseInt(nojTextField.getText());
                    dispose();
                    new MPRGUI(nom, noj);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(IntermediateGui.this, "Invalid input. Please enter valid numbers.");
                }
            }
        });
    }
}

class MPRGUI extends JFrame {
    private int nom;
    private int noj;
    private JTextField[][] ptTextFields;
    private JTextArea resultTextArea;
    private JButton calculateButton;

    public MPRGUI(int nom, int noj) {
        this.nom = nom;
        this.noj = noj;
        ptTextFields = new JTextField[noj][nom];
        calculateButton = new JButton("Calculate");
        resultTextArea = new JTextArea(10, 30);
        resultTextArea.setEditable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(noj + 2, nom));
        for (int i = 0; i < noj; i++) {
            for (int j = 0; j < nom; j++) {
                ptTextFields[i][j] = new JTextField(5);
                panel.add(new JLabel("Job " + (i + 1) + ", Machine " + (j + 1)));
                panel.add(ptTextFields[i][j]);
            }
        }
        panel.add(calculateButton);
        panel.add(resultTextArea);
        resultTextArea.append("Result:");

        this.setTitle("MPR Calculator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 400);
        this.add(panel);
        this.setVisible(true);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateMPR();
            }
        });
    }

    private void calculateMPR() {
        try {

            int[][] PT = new int[noj][nom];
            for (int i = 0; i < noj; i++) {
                for (int j = 0; j < nom; j++) {
                    PT[i][j] = Integer.parseInt(ptTextFields[i][j].getText());
                }
            }
            
            int mptFM = Integer.MAX_VALUE;
            int mptLM = Integer.MAX_VALUE;
            for (int i = 0; i < noj; i++) {
                int ptFM = PT[i][0];
                int ptLM = PT[i][nom - 1];
                if (ptFM < mptFM) {
                    mptFM = ptFM;
                }
                if (ptLM < mptLM) {
                    mptLM = ptLM;
                }
            }

            
            int[] maxPT = new int[nom - 2];
            for (int j = 1; j < nom - 1; j++) {
                int tmaxPT = Integer.MIN_VALUE;
                for (int i = 0; i < noj; i++) {
                    int tpt = PT[i][j];
                    if (tpt > tmaxPT) {
                        tmaxPT = tpt;
                    }
                }
                maxPT[j - 1] = tmaxPT;
            }

            
            int maxmaxPT = Integer.MIN_VALUE;
            for (int i = 0; i < nom - 2; i++) {
                if (maxPT[i] > maxmaxPT) {
                    maxmaxPT = maxPT[i];
                }
            }
            
            if (mptFM < maxmaxPT && mptLM < maxmaxPT) {
                JOptionPane.showMessageDialog(this, "Cannot continue with this method. Change the inputs.");
                return;
            }

            int[] machineG = new int[noj];
            int[] machineI = new int[noj];
            for (int i = 0; i < noj; i++) {
                for (int j = 0; j < nom - 1; j++) {
                    machineG[i] = machineG[i] + PT[i][j];
                }
                for (int j = 1; j < nom; j++) {
                    machineI[i] = machineI[i] + PT[i][j];
                }
            }

            
            boolean[] allocated = new boolean[noj];
            int[] OS = new int[noj];
            int count = 0;
            for (int slot = 0; slot < noj; slot++) {
                int mdI = Integer.MAX_VALUE;
                int mdG = Integer.MAX_VALUE;
                int iJI = -1;
                int gJI = -1;

                for (int i = 0; i < noj; i++) {
                    if (!allocated[i] && machineG[i] < mdG) {
                        mdG = machineG[i];
                        gJI = i;
                    }
                }
                for (int i = 0; i < noj; i++) {
                    if (!allocated[i] && machineI[i] < mdI) {
                        mdI = machineI[i];
                        iJI = i;
                    }
                }
                if (mdG <= mdI) {
                    OS[count] = gJI + 1;
                    allocated[gJI] = true;
                    count++;
                }
                if (mdI < mdG) {
                    OS[noj - slot - 1] = iJI + 1;
                    allocated[iJI] = true;
                }
            }

            
            int[][] machineTimes = new int[nom][noj];
            for (int i = 0; i < noj; i++) {
                int job = OS[i] - 1; 
                for (int j = 0; j < nom; j++) {
                    if (i == 0) {
                        if (j == 0) {
                            machineTimes[j][i] = PT[job][j];
                        } else {
                            machineTimes[j][i] = machineTimes[j - 1][i] + PT[job][j];
                        }
                    } else {
                        if (j == 0) {
                            machineTimes[j][i] = machineTimes[j][i - 1] + PT[job][j];
                        } else {
                            machineTimes[j][i] = Math.max(machineTimes[j - 1][i], machineTimes[j][i - 1]) + PT[job][j];
                        }
                    }
                }
            }

            
            int minElapsed = machineTimes[nom - 1][noj - 1];

            StringBuilder result = new StringBuilder();
            result.append("\nOptimal Sequence: ");
            for (int i = 0; i < noj; i++) {
                result.append(OS[i]).append("  ");
            }
            result.append("\nMinimum Elapsed Time: ").append(minElapsed);
            resultTextArea.setText(result.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WelcomeGui();
            }
        });
    }
}
