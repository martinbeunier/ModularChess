package gui;

import profile.PlayerManager;
import logic.Player;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.view.ViewBox;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class ProfileSettings extends JPanel {

    private MainFrame frame;
    private JTextField nameField;
    private JTextField cheatField;
    private JLabel statusLabel;

    private JLabel avatarPreviewLabel;

    private static final String SKINS_DIR = "src/files/images/skins";
    private static final String AVATARS_DIR = "src/files/images/avatars";

    public ProfileSettings(MainFrame frame) {
        this.frame = frame;

        int w = frame.getWidth();
        int h = frame.getHeight();

        setLayout(null);

        JButton backButton = new JButton("Zpět do menu");
        backButton.addActionListener(e -> frame.showScene("MENU"));
        backButton.setBounds(
                UI.toPercent(10, w),
                UI.toPercent(10, h),
                UI.toPercent(20, w),
                UI.toPercent(20, h)
        );
        add(backButton);

        nameInputbox(w, h);
        cheatInputbox(w, h);
        avatarGallery(w, h);
        skinGallery(w, h);
        escape();
    }

    private void saveName() {
        String newName = nameField.getText().trim();

        if (newName.isEmpty()) {
            statusLabel.setText("Jméno nesmí být prázdné.");
            return;
        }

        if (newName.contains(";")) {
            statusLabel.setText("Jméno nesmí obsahovat ';'.");
            return;
        }

        PlayerManager.setName(newName);
        statusLabel.setText("Jméno bylo uloženo.");
    }

    private void nameInputbox(int w, int h) {

        JLabel titleLabel = new JLabel("Nastavení profilu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(
                UI.toPercent(40, w),
                UI.toPercent(25, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(titleLabel);

        JLabel nameLabel = new JLabel("Jméno hráče:");
        nameLabel.setBounds(
                UI.toPercent(40, w),
                UI.toPercent(35, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(nameLabel);

        Player currentPlayer = PlayerManager.getCurrentHumanPlayer();

        nameField = new JTextField(currentPlayer.getName(), 20);
        nameField.setBounds(
                UI.toPercent(40, w),
                UI.toPercent(40, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(nameField);

        JButton saveButton = new JButton("Uložit");
        saveButton.addActionListener(e -> saveName());
        saveButton.setBounds(
                UI.toPercent(45, w),
                UI.toPercent(47, h),
                UI.toPercent(10, w),
                UI.toPercent(5, h)
        );
        add(saveButton);

        statusLabel = new JLabel(" ");
        statusLabel.setBounds(
                UI.toPercent(40, w),
                UI.toPercent(54, h),
                UI.toPercent(20, w),
                UI.toPercent(10, h)
        );
        add(statusLabel);
    }

    private void cheatInputbox(int w, int h) {

        JLabel cheatLabel = new JLabel("Cheat code:");
        cheatLabel.setBounds(
                UI.toPercent(10, w),
                UI.toPercent(65, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(cheatLabel);

        JButton cheatButton = new JButton("Aktivovat cheat");
        cheatButton.setBounds(
                UI.toPercent(10, w),
                UI.toPercent(70, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(cheatButton);

        cheatField = new JTextField();
        cheatField.setBounds(
                UI.toPercent(10, w),
                UI.toPercent(40, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(cheatField);

        cheatButton.addActionListener(e -> {
            if (cheatField.getText().equals("174 bpm")) {

                PlayerManager.cheatcodeActivated = true;
                System.out.println("Cheat code Activated");

                statusLabel.setText(
                        "<html>" +
                                "Cheat code is activated.<br>" +
                                "Your progress will not be written.<br>" +
                                "You have unlocked all maps.<br>" +
                                "Cheat code will be reseted after turning off the game." +
                                "</html>"
                );
            }
        });
    }

    // ================================================================
    // GALERIE AVATARŮ (PNG) — reálný výběr, ukládá se do profilu
    // ================================================================

    private void avatarGallery(int w, int h) {

        JLabel avatarTitle = new JLabel("Vyber avatara:");
        avatarTitle.setBounds(
                UI.toPercent(65, w),
                UI.toPercent(10, h),
                UI.toPercent(30, w),
                UI.toPercent(5, h)
        );
        add(avatarTitle);

        // Náhled aktuálně vybraného avatara — použijeme stejný fit-to-box princip jako v Loop
        avatarPreviewLabel = new JLabel();
        avatarPreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarPreviewLabel.setBounds(
                UI.toPercent(90, w),
                UI.toPercent(10, h),
                UI.toPercent(8, w),
                UI.toPercent(15, h)
        );
        add(avatarPreviewLabel);
        updateAvatarPreview(PlayerManager.getCurrentHumanPlayer().getAvatarPath());

        JPanel avatarContent = new JPanel();
        avatarContent.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        avatarContent.setBackground(Color.WHITE);

        File dir = new File(AVATARS_DIR);
        File[] files = dir.exists() ? dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png")) : null;

        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));

            for (File f : files) {
                Icon icon = loadFitToBoxThumbnail(f, 64, 64);
                if (icon == null) continue;

                JButton btn = new JButton(icon);
                btn.setToolTipText(f.getName());
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);

                String path = f.getPath().replace("\\", "/");
                btn.addActionListener(e -> {
                    PlayerManager.setAvatarPath(path);
                    updateAvatarPreview(path);
                    statusLabel.setText("Avatar byl uložen: " + f.getName());
                });

                avatarContent.add(btn);
            }
        } else {
            avatarContent.add(new JLabel("Složka s avatary nebyla nalezena: " + dir.getAbsolutePath()));
        }

        JScrollPane scrollPane = new JScrollPane(avatarContent);
        scrollPane.setBounds(
                UI.toPercent(65, w),
                UI.toPercent(16, h),
                UI.toPercent(33, w),
                UI.toPercent(30, h)
        );
        add(scrollPane);
    }

    private void updateAvatarPreview(String path) {
        Icon icon = loadFitToBoxThumbnail(new File(path), 80, 80);
        avatarPreviewLabel.setIcon(icon);
    }

    private Icon loadFitToBoxThumbnail(File file, int boxW, int boxH) {
        if (file == null || !file.exists()) return null;

        Image img;
        if (file.getName().toLowerCase().endsWith(".svg")) {
            img = renderSvgToBufferedImage(file, Math.max(boxW, boxH) * 2);
        } else {
            try {
                img = javax.imageio.ImageIO.read(file);
            } catch (java.io.IOException e) {
                System.err.println("Chyba při načítání PNG: " + file.getAbsolutePath());
                e.printStackTrace();
                return null;
            }
        }

        if (img == null) {
            System.err.println("Obrázek se nepodařilo načíst (null): " + file.getAbsolutePath());
            return null;
        }

        int srcW = img.getWidth(null);
        int srcH = img.getHeight(null);
        if (srcW <= 0 || srcH <= 0) return null;

        double scale = Math.min((double) boxW / srcW, (double) boxH / srcH);
        int scaledW = Math.max(1, (int) Math.round(srcW * scale));
        int scaledH = Math.max(1, (int) Math.round(srcH * scale));

        BufferedImage canvas = new BufferedImage(boxW, boxH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Image scaledImg = img.getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);
        int drawX = (boxW - scaledW) / 2;
        int drawY = (boxH - scaledH) / 2;
        g2d.drawImage(scaledImg, drawX, drawY, null);
        g2d.dispose();

        return new ImageIcon(canvas);
    }

    private BufferedImage renderSvgToBufferedImage(File svgFile, int size) {
        try {
            SVGLoader loader = new SVGLoader();
            SVGDocument doc = loader.load(svgFile.toURI().toURL());
            if (doc == null) return null;

            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            doc.render(null, g2, new ViewBox(0, 0, size, size));
            g2.dispose();

            return img;
        } catch (Exception e) {
            return null;
        }
    }

    private Icon loadSvgIcon(File svgFile, int baseTileSize, double scale) {
        int size = Math.max(1, (int) Math.round(baseTileSize * scale));
        BufferedImage img = renderSvgToBufferedImage(svgFile, size);
        return (img != null) ? new ImageIcon(img) : null;
    }

    // ================================================================
    // GALERIE FIGUREK (SVG) — pouze náhled, respektuje scale jako ve hře
    // ================================================================

    private void skinGallery(int w, int h) {

        JLabel skinTitle = new JLabel("Galerie figurek (skiny):");
        skinTitle.setBounds(
                UI.toPercent(65, w),
                UI.toPercent(48, h),
                UI.toPercent(33, w),
                UI.toPercent(5, h)
        );
        add(skinTitle);

        JPanel skinContent = new JPanel();
        skinContent.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        skinContent.setBackground(Color.WHITE);

        File dir = new File(SKINS_DIR);
        File[] files = dir.exists() ? dir.listFiles((d, name) -> name.toLowerCase().endsWith(".svg")) : null;

        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));

            int baseTileSize = 48; // odpovídá zhruba jednomu poli šachovnice v náhledu

            for (File f : files) {
                String pieceType = extractPieceType(f.getName());
                double scale = PieceVisuals.getScaleByClass(pieceType);

                Icon icon = loadSvgIcon(f, baseTileSize, scale);
                if (icon == null) continue;

                JButton btn = new JButton(icon);
                btn.setToolTipText(f.getName());
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);

                btn.addActionListener(e -> showEnlargedSkinPreview(f, pieceType, scale));

                skinContent.add(btn);
            }
        } else {
            skinContent.add(new JLabel("Složka se skiny nebyla nalezena: " + dir.getAbsolutePath()));
        }

        JScrollPane scrollPane = new JScrollPane(skinContent);
        scrollPane.setBounds(
                UI.toPercent(65, w),
                UI.toPercent(54, h),
                UI.toPercent(33, w),
                UI.toPercent(40, h)
        );
        add(scrollPane);
    }

    private void showEnlargedSkinPreview(File f, String pieceType, double scale) {
        Icon icon = loadSvgIcon(f, 120, scale);
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String path = f.getPath().replace("\\", "/");

        JButton useAsAvatarButton = new JButton("Použít jako profilovku");

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(useAsAvatarButton, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                f.getName() + " (scale " + scale + ")",
                true
        );
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);

        useAsAvatarButton.addActionListener(e -> {
            PlayerManager.setAvatarPath(path);
            updateAvatarPreview(path);
            statusLabel.setText("Profilovka byla nastavena na: " + f.getName());
            dialog.dispose();
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /** Z názvu souboru (např. "whitepawn.svg") vytáhne typ figurky bez barvy a přípony. */
    private String extractPieceType(String fileName) {
        String base = fileName.substring(0, fileName.lastIndexOf('.')).toLowerCase();
        if (base.startsWith("white")) return base.substring(5);
        if (base.startsWith("black")) return base.substring(5);
        return base;
    }



    private void escape() {

        getInputMap(
                WHEN_IN_FOCUSED_WINDOW
        ).put(
                KeyStroke.getKeyStroke("ESCAPE"),
                "backTo"
        );

        getActionMap().put(
                "backTo",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        frame.showScene("MENU");
                    }
                }
        );
    }
}