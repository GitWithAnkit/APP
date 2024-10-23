import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PasswordGenerator {

    public static void main(String[] args) {
        // Create a new frame
        JFrame frame = new JFrame("Password Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);
        frame.setLayout(new GridBagLayout());  // Use GridBagLayout for better control
        frame.getContentPane().setBackground(new Color(60, 63, 65));  // Dark background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Padding for components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title label
        JLabel titleLabel = new JLabel("Password Generator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);  // White text
        frame.add(titleLabel, gbc);

        // Password length label and text field to display the value
        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel lengthLabel = new JLabel("Password Length:");
        lengthLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        lengthLabel.setForeground(Color.WHITE);
        frame.add(lengthLabel, gbc);

        // Text field that dynamically shows the length of password
        gbc.gridx = 1;
        JTextField lengthDisplay = new JTextField(3);
        lengthDisplay.setEditable(false);
        lengthDisplay.setHorizontalAlignment(JTextField.CENTER);
        lengthDisplay.setFont(new Font("Arial", Font.BOLD, 14));
        lengthDisplay.setBackground(Color.WHITE);
        frame.add(lengthDisplay, gbc);

        // Password length slider (without lines and labels)
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JSlider lengthSlider = new JSlider(JSlider.HORIZONTAL, 4, 20, 8);  // Min length 4, Max 20, Initial 8
        lengthSlider.setBackground(new Color(60, 63, 65));  // Match background
        lengthSlider.setForeground(Color.WHITE);
        lengthSlider.setPaintTicks(false);  // Remove lines from slider
        lengthSlider.setPaintLabels(false);

        // Dynamically update the length display when slider is moved
        lengthSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lengthDisplay.setText(String.valueOf(lengthSlider.getValue()));  // Display slider value in the box
            }
        });
        lengthDisplay.setText(String.valueOf(lengthSlider.getValue()));  // Initial value in the box
        frame.add(lengthSlider, gbc);

        // Checkboxes for options
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        JCheckBox uppercaseCheck = new JCheckBox("Include Uppercase");
        uppercaseCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        uppercaseCheck.setForeground(Color.WHITE);
        uppercaseCheck.setBackground(new Color(60, 63, 65));  // Match background
        frame.add(uppercaseCheck, gbc);

        gbc.gridx = 1;
        JCheckBox lowercaseCheck = new JCheckBox("Include Lowercase");
        lowercaseCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        lowercaseCheck.setForeground(Color.WHITE);
        lowercaseCheck.setBackground(new Color(60, 63, 65));
        frame.add(lowercaseCheck, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JCheckBox numbersCheck = new JCheckBox("Include Numbers");
        numbersCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        numbersCheck.setForeground(Color.WHITE);
        numbersCheck.setBackground(new Color(60, 63, 65));
        frame.add(numbersCheck, gbc);

        gbc.gridx = 1;
        JCheckBox specialCheck = new JCheckBox("Include Special Characters");
        specialCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        specialCheck.setForeground(Color.WHITE);
        specialCheck.setBackground(new Color(60, 63, 65));
        frame.add(specialCheck, gbc);

        // Password display
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JTextField passwordField = new JTextField(20);
        passwordField.setEditable(false);
        passwordField.setBackground(Color.WHITE);
        frame.add(passwordField, gbc);

        // Generate button
        gbc.gridy++;
        JButton generateButton = new JButton("Generate Password");
        generateButton.setBackground(new Color(44, 102, 230));  // Blue color
        generateButton.setForeground(Color.WHITE);  // White text
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        frame.add(generateButton, gbc);

        // Copy button
        gbc.gridy++;
        JButton copyButton = new JButton("Copy Password");
        copyButton.setBackground(new Color(100, 200, 100));  // Green color
        copyButton.setForeground(Color.WHITE);  // White text
        copyButton.setFont(new Font("Arial", Font.BOLD, 14));
        copyButton.setEnabled(false);  // Initially disabled, until password is generated
        frame.add(copyButton, gbc);

        // Action listener for the "Generate Password" button
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int length = lengthSlider.getValue();  // Get the value from the slider

                boolean includeUppercase = uppercaseCheck.isSelected();
                boolean includeLowercase = lowercaseCheck.isSelected();
                boolean includeNumbers = numbersCheck.isSelected();
                boolean includeSpecial = specialCheck.isSelected();

                if (!includeUppercase && !includeLowercase && !includeNumbers && !includeSpecial) {
                    JOptionPane.showMessageDialog(frame, "Please select at least one character set", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Generate the password
                String password = generatePassword(length, includeUppercase, includeLowercase, includeNumbers, includeSpecial);
                passwordField.setText(password);

                // Enable the copy button once password is generated
                copyButton.setEnabled(true);
            }
        });

        // Action listener for the "Copy Password" button
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = passwordField.getText();
                if (!password.isEmpty()) {
                    // Copy the password to the system clipboard
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(password), null);
                    JOptionPane.showMessageDialog(frame, "Password copied to clipboard!");
                }
            }
        });

        // Set frame visible
        frame.setVisible(true);
    }

    // Password generation logic
    public static String generatePassword(int length, boolean uppercase, boolean lowercase, boolean numbers, boolean special) {
        StringBuilder characterSet = new StringBuilder();
        Random random = new Random();

        if (uppercase) {
            characterSet.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }
        if (lowercase) {
            characterSet.append("abcdefghijklmnopqrstuvwxyz");
        }
        if (numbers) {
            characterSet.append("0123456789");
        }
        if (special) {
            characterSet.append("!@#$%^&*()-_=+<>?");
        }

        if (characterSet.length() == 0) {
            throw new IllegalArgumentException("No character set selected");
        }

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(characterSet.charAt(random.nextInt(characterSet.length())));
        }

        return password.toString();
    }
}
