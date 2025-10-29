/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author peper
 */
import controller.KontakController;
import java.io.*;
import model.Kontak;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PengelolaanKontakFrame extends javax.swing.JFrame {
     private DefaultTableModel model;
     private KontakController controller;
     

    /**
     * Creates new form PengelolaanKontakFrame
     */
    public PengelolaanKontakFrame() {
        initComponents();
        
        controller = new KontakController();
        model = new DefaultTableModel (new String[]
                {"No" , "Nama" , "Nomor Telepon"  , "Kategori"}, 0);
        tblKontak.setModel(model);
        
        loadContact();
    }
    private void loadContact() {
        try {
model.setRowCount(0);
List<Kontak> contacts = controller.getAllContacts();
int rowNumber = 1;
for (Kontak contact : contacts) {
model.addRow(new Object[]{
rowNumber++,
contact.getNama(),
contact.getNomorTelepon(),
contact.getKategori()
});
}
} catch (SQLException e) {
showError(e.getMessage());
}
}

private void showError(String message) {
JOptionPane.showMessageDialog(this, message, "Error",
JOptionPane.ERROR_MESSAGE);
    }
private void addContact(){
    String nama = txtNama.getText().trim();
    String nomorTelepon = txtNomorTelepon.getText().trim();
    String kategori = (String) cmbKategori.getSelectedItem();
    
    if (!validatePhoneNumber(nomorTelepon)) {
        return; // Validasi nomor telepon gagal
}
    try {
if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
JOptionPane.showMessageDialog(this, "Kontak nomor telepon inisudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
return;
}
controller.addContact(nama, nomorTelepon, kategori);
loadContact();
JOptionPane.showMessageDialog(this, "Kontak berhasilditambahkan!");
clearInputFields();
    } catch (SQLException ex) {
        showError("Gagal Menambahka Kontak: " + ex.getMessage());
    }
}
private boolean validatePhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nomor telepon tidak boleh kosong.");
        return false;
    }
    if (!phoneNumber.matches("\\d+")){ //hanya angka
        JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka.");
        return false;
    }
    if (phoneNumber.length() < 8 || phoneNumber.length() > 15) {
        JOptionPane.showMessageDialog(this, "Nomor telepon harus memiliki panjang antara 8 hingga 15 karakter.");
        return false;
    }
    return true;
}
private void clearInputFields() {
    txtNama.setText("");
    txtNomorTelepon.setText("");
    cmbKategori.setSelectedIndex(0);
}
private void editContact() {
    int selectedRow = tblKontak.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih Kontak yang ingin diperbarui.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    int id = (int) model.getValueAt(selectedRow, 0);
    String nama = txtNama.getText().trim();
    String nomorTelepon = txtNomorTelepon.getText().trim();
    String kategori = (String) cmbKategori.getSelectedItem();
    if (!validatePhoneNumber(nomorTelepon)) {
        return;
}
    try {
        if (controller.isDuplicatePhoneNumber(nomorTelepon, id)) {
            JOptionPane.showMessageDialog(this, "Kontak nomor telepon ini sudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        controller.updateContact(id, nama, nomorTelepon, kategori);
        loadContact();
        JOptionPane.showMessageDialog(this, "Kontak Berhasil diperbarui!");
        clearInputFields();
    } catch (SQLException ex){
        showError("Gagal memperbarui kontak: " + ex.getMessage());
    }
}
private void populateInputFields(int selectedRow) {
   String nama = model.getValueAt(selectedRow, 1).toString();
   String nomorTelepon = model.getValueAt(selectedRow, 2).toString();
   String kategori = model.getValueAt(selectedRow, 3).toString(); 
   
txtNama.setText(nama);
txtNomorTelepon.setText(nomorTelepon);
cmbKategori.setSelectedItem(kategori);
}

private void deleteContact() {
   int selectedRow = tblKontak.getSelectedRow();
   if (selectedRow != -1) {
       int id = (int) model.getValueAt(selectedRow, 0);
       try {
           controller.deleteContact(id);
           loadContact();
           JOptionPane.showMessageDialog(this ,"Kontak Berhasil dihapus!");
           clearInputFields();
       } catch (SQLException e) {
           showError(e.getMessage());
       }
   }
}

private void searchContact() {
String keyword = txtPencarian.getText().trim();
if (!keyword.isEmpty()) {
try {
List<Kontak> contacts = controller.searchContacts(keyword);
model.setRowCount(0); // Bersihkan tabel
for (Kontak contact : contacts) {
model.addRow(new Object[]{
contact.getId(),
contact.getNama(),
contact.getNomorTelepon(),
contact.getKategori()
});
}
if (contacts.isEmpty()) {
JOptionPane.showMessageDialog(this, "Tidak ada kontakditemukan.");
}
} catch (SQLException ex) {
showError(ex.getMessage());
}
} else {
loadContact();
}
}
private void exportToCSV() {
JFileChooser fileChooser = new JFileChooser();
fileChooser.setDialogTitle("Simpan File CSV");
int userSelection = fileChooser.showSaveDialog(this);
if (userSelection == JFileChooser.APPROVE_OPTION) {
File fileToSave = fileChooser.getSelectedFile();
// Tambahkan ekstensi .csv jika pengguna tidak menambahkannya
if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
}
try (BufferedWriter writer = new BufferedWriter(new
FileWriter(fileToSave))) {
writer.write("ID,Nama,Nomor Telepon,Kategori\n"); // HeadersCSV
for (int i = 0; i < model.getRowCount(); i++) {
writer.write(
model.getValueAt(i, 0) + "," +
model.getValueAt(i, 1) + "," +
model.getValueAt(i, 2) + "," +
model.getValueAt(i, 3) + "\n"
);
}
JOptionPane.showMessageDialog(this, "Data berhasil dieksporke " + fileToSave.getAbsolutePath());
} catch (IOException ex) {
showError("Gagal menulis file: " + ex.getMessage());
}
}
}
private void importFromCSV() {
showCSVGuide();
int confirm = JOptionPane.showConfirmDialog(
this,
"Apakah Anda yakin file CSV yang dipilih sudah sesuai denganformat?",
"Konfirmasi Impor CSV",
JOptionPane.YES_NO_OPTION
);
if (confirm == JOptionPane.YES_OPTION) {
JFileChooser fileChooser = new JFileChooser();
fileChooser.setDialogTitle("Pilih File CSV");
int userSelection = fileChooser.showOpenDialog(this);
if (userSelection == JFileChooser.APPROVE_OPTION) {
File fileToOpen = fileChooser.getSelectedFile();
try (BufferedReader reader = new BufferedReader(new
FileReader(fileToOpen))) {
    String line = reader.readLine(); // Baca header
if (!validateCSVHeader(line)) {
JOptionPane.showMessageDialog(this, "Format headerCSV tidak valid. Pastikan header adalah: ID,Nama,Nomor Telepon,Kategori","Kesalahan CSV", JOptionPane.ERROR_MESSAGE);
return;
}
int rowCount = 0;
int errorCount = 0;
int duplicateCount = 0;
StringBuilder errorLog = new StringBuilder("Baris dengankesalahan:\n");
while ((line = reader.readLine()) != null) {
rowCount++;

String[] data = line.split(",");

if (data.length != 4) {
errorCount++;

errorLog.append("Baris ").append(rowCount +

1).append(": Format kolom tidak sesuai.\n");
continue;
}
String nama = data[1].trim();
String nomorTelepon = data[2].trim();
String kategori = data[3].trim();
if (nama.isEmpty() || nomorTelepon.isEmpty()) {
errorCount++;

errorLog.append("Baris ").append(rowCount +

1).append(": Nama atau Nomor Telepon kosong.\n");
continue;
}
if (!validatePhoneNumber(nomorTelepon)) {
errorCount++;

errorLog.append("Baris ").append(rowCount +

1).append(": Nomor Telepon tidak valid.\n");
continue;
}
try {
if
(controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
duplicateCount++;

errorLog.append("Baris ").append(rowCount +

1).append(": Kontak sudah ada.\n");
continue;
}
} catch (SQLException ex) {
Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(Level.SEVERE
, null, ex);
}
try {
controller.addContact(nama, nomorTelepon,
kategori);
} catch (SQLException ex) {
errorCount++;
errorLog.append("Baris ").append(rowCount +
1).append(": Gagal menyimpan ke database -").append(ex.getMessage()).append("\n");
}
}
loadContact();
if (errorCount > 0 || duplicateCount > 0) {
errorLog.append("\nTotal baris dengan kesalahan:").append(errorCount).append("\n");
errorLog.append("Total baris duplikat:").append(duplicateCount).append("\n");
JOptionPane.showMessageDialog(this,
errorLog.toString(), "Kesalahan Impor", JOptionPane.WARNING_MESSAGE);
} else {
JOptionPane.showMessageDialog(this, "Semua databerhasil diimpor.");
}
} catch (IOException ex) {
showError("Gagal membaca file: " + ex.getMessage());
}
}
}
}
private void showCSVGuide() {
String guideMessage = "Format CSV untuk impor data:\n" +
"- Header wajib: ID, Nama, Nomor Telepon, Kategori\n" +
"- ID dapat kosong (akan diisi otomatis)\n" +
"- Nama dan Nomor Telepon wajib diisi\n" +
"- Contoh isi file CSV:\n" +
" 1, Andi, 08123456789, Teman\n" +
" 2, Budi Doremi, 08567890123, Keluarga\n\n" +
"Pastikan file CSV sesuai format sebelum melakukan impor.";JOptionPane.showMessageDialog(this, guideMessage, "Panduan FormatCSV", JOptionPane.INFORMATION_MESSAGE);
}
private boolean validateCSVHeader(String header) {
return header != null &&
header.trim().equalsIgnoreCase("ID,Nama,Nomor Telepon,Kategori");
}




   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new java.awt.Panel();
        lblJudul = new java.awt.Label();
        label2 = new java.awt.Label();
        label3 = new java.awt.Label();
        label4 = new java.awt.Label();
        label5 = new java.awt.Label();
        label6 = new java.awt.Label();
        label1 = new java.awt.Label();
        txtNama = new java.awt.TextField();
        txtNomorTelepon = new java.awt.TextField();
        cmbKategori = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKontak = new javax.swing.JTable();
        txtPencarian = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Aplikasi Pengelolaan Kontak"); // NOI18N

        lblJudul.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        lblJudul.setText("APLIKASI PENGELOLA KONTAK");

        label2.setFont(new java.awt.Font("Lucida Bright", 0, 14)); // NOI18N
        label2.setText("Nama Kontak");

        label3.setText("label3");

        label4.setFont(new java.awt.Font("Lucida Bright", 0, 14)); // NOI18N
        label4.setText("Nomor Telepon");

        label5.setText("label5");

        label6.setFont(new java.awt.Font("Lucida Bright", 0, 14)); // NOI18N
        label6.setText("Kategori");

        label1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        label1.setFont(new java.awt.Font("Lucida Bright", 0, 14)); // NOI18N
        label1.setText("Pencarian");

        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });

        txtNomorTelepon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomorTeleponActionPerformed(evt);
            }
        });

        cmbKategori.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "keluarga", "teman", "kantor" }));

        btnTambah.setFont(new java.awt.Font("Verdana", 1, 13)); // NOI18N
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setFont(new java.awt.Font("Verdana", 1, 13)); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setFont(new java.awt.Font("Verdana", 1, 13)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        tblKontak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblKontak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKontakMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKontak);

        txtPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPencarianKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addContainerGap(227, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnTambah)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(lblJudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnExport)
                            .addGap(55, 55, 55)
                            .addComponent(btnImport)
                            .addGap(83, 83, 83))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                            .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(93, 93, 93)
                                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                            .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(105, 105, 105))
                                        .addGroup(panel1Layout.createSequentialGroup()
                                            .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtNomorTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(btnHapus)
                                            .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGap(419, 419, 419)))))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addComponent(lblJudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addComponent(txtNomorTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)))
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnEdit)
                    .addComponent(btnHapus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPencarian, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(380, 380, 380)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnImport)
                            .addComponent(btnExport))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))))
        );

        lblJudul.getAccessibleContext().setAccessibleName("APLIKASI PENGELOLA KONTAK");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1.getAccessibleContext().setAccessibleName("Aplikasi Pengelolaan Kontak");
        panel1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaActionPerformed
    txtNama.setText("");
    // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaActionPerformed

    private void txtNomorTeleponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomorTeleponActionPerformed
    txtNomorTelepon.setText("");
    // TODO add your handling code here:
    }//GEN-LAST:event_txtNomorTeleponActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
       addContact(); // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
     editContact();   // TODO add your handling code here:
    }//GEN-LAST:event_btnEditActionPerformed

    private void tblKontakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKontakMouseClicked
      int selectedRow = tblKontak.getSelectedRow();
      if (selectedRow != -1) {
          populateInputFields(selectedRow);
}  // TODO add your handling code here:
    }//GEN-LAST:event_tblKontakMouseClicked

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
    deleteContact();    // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void txtPencarianKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPencarianKeyTyped
     searchContact();   // TODO add your handling code here:
    }//GEN-LAST:event_txtPencarianKeyTyped

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
       exportToCSV();
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
     importFromCSV();   // TODO add your handling code here:
    }//GEN-LAST:event_btnImportActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanKontakFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private java.awt.Label label6;
    private java.awt.Label lblJudul;
    private java.awt.Panel panel1;
    private javax.swing.JTable tblKontak;
    private java.awt.TextField txtNama;
    private java.awt.TextField txtNomorTelepon;
    private javax.swing.JTextField txtPencarian;
    // End of variables declaration//GEN-END:variables
}
