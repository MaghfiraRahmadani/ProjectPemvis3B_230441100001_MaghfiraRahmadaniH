package rs;

import com.mysql.cj.xdevapi.Statement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;





/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author asus
 */
public class menu extends javax.swing.JFrame {
    public Statement st;
    public ResultSet rs;
    Connection conn;
    private DefaultTableModel model;
    private DefaultTableModel model1;
    private DefaultTableModel model2;
    private DefaultTableModel model3;
    
    //inisiasi GLobal Variable
 
    public menu() {
       
        conn = koneksi.getConnection();
        initComponents();
        loadData();
        model = new DefaultTableModel();
        tb_ranap.setModel(model);
        tb_ranap.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {

        int row = tb_ranap.getSelectedRow();

        if (row != -1) {

            int id = (int) model.getValueAt(row, 0); // Asumsikan kolom pertama adalah ID

            loadDataToEdit(id);

        }

    }

    
});
         tb_dokter.addMouseListener(new MouseAdapter() {

        public void mouseClicked(MouseEvent e) {

            int row = tb_dokter.getSelectedRow();

            if (row != -1) {

                int idDokter = (int) tb_dokter.getValueAt(row, 0);
                String namaDokter = (String) tb_dokter.getValueAt(row, 1);
                String spesialisDokter = (String) tb_dokter.getValueAt(row, 2);
                String tlpDokter = (String) tb_dokter.getValueAt(row, 3); // Set the values to the text fields

                id_dokter.setText(String.valueOf(idDokter));

                nama_dokter.setText(namaDokter);

                spesialis.setText(spesialisDokter);

                tlp.setText(tlpDokter);

            }

        }

    });
    model.addColumn("ID Pasien");
    model.addColumn("Nama Pasien");
    model.addColumn("Status");
    model.addColumn("Ruangan");
    model.addColumn("Dokter");
    model.addColumn("Pemeriksaan");
    model.addColumn("Durasi Inap");
    model.addColumn("Total");
    
//    loadData();
//    
    
    model1 = new DefaultTableModel();
    tb_rawatjalan.setModel(model1);
    
    model1.addColumn("ID Pasien");
    model1.addColumn("Nama Pasien");
    model1.addColumn("Dokter");
    model1.addColumn("Pemeriksaan");
    model1.addColumn("Total");
    
    loadData1();
    }
   

    //end global variabel
   

   private void saveDataRawatInap() {
    // Validasi input untuk Rawat Inap
    if (napas.getText().isEmpty() || 
        stapas.getSelectedItem().toString().isEmpty() || 
        kamar.getSelectedItem().toString().isEmpty() || 
        dok.getSelectedItem().toString().isEmpty() || 
        periksa.getText().isEmpty() || 
        durasii.getSelectedItem().toString().isEmpty() || 
        total.getText().isEmpty()) {
        
        JOptionPane.showMessageDialog(this, "Isi data Rawat Inap terlebih dahulu", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Query INSERT tanpa kolom id
        String sql = "INSERT INTO tb_ranap (nama, status, kamar, dokter, pemeriksaan, durasi, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setString(1, napas.getText());
        ps.setString(2, (String) stapas.getSelectedItem());
        ps.setString(3, (String) kamar.getSelectedItem());
        ps.setString(4, (String) dok.getSelectedItem());
        ps.setString(5, periksa.getText());
        ps.setString(6, (String) durasii.getSelectedItem());
        ps.setString(7, total.getText());
        
        ps.executeUpdate();
        
        JOptionPane.showMessageDialog(this, "Data Rawat Inap berhasil disimpan");
        
        // Memuat ulang data ke tabel
        loadData();
        
    } catch (SQLException e) {
        System.out.println("Error Save Data Rawat Inap: " + e.getMessage());
    }
}

private void loadData() {
    model.setRowCount(0); // Menghapus semua baris sebelum memuat data baru
    
    try {
        String sql = "SELECT * FROM tb_ranap";
        java.sql.Statement st = conn.createStatement();
        java.sql.ResultSet rs = st.executeQuery(sql);
        
        while (rs.next()) {
            // Menambahkan data dari ResultSet ke model tabel
            model.addRow(new Object[]{
                rs.getInt("id_pasien"),  // ID yang dihasilkan otomatis
                rs.getString("nama"),
                rs.getString("status"),
                rs.getString("kamar"),
                rs.getString("dokter"),
                rs.getString("pemeriksaan"),
                rs.getString("durasi"),
                rs.getString("total")
            });
        }
        
        rs.close();
        st.close();
    } catch (SQLException e) {
        System.out.println("Error Load Data Rawat Inap: " + e.getMessage());
    }
}



private void updateHarga() {
    String status = (String) stapas.getSelectedItem();
    
    if ("BPJS".equals(status)) {
        hatus.setText("0"); // Harga BPJS
        hadok.setText(""); // Kosongkan harga dokter untuk BPJS
    } else if ("UMUM".equals(status)) {
        hatus.setText("500000"); // Harga UMUM
        hadok.setText(""); // Kosongkan harga dokter jika UMUM
    } else if ("ASURANSI".equals(status)) {
        hatus.setText("700000"); // Harga ASURANSI
        hadok.setText(""); // Kosongkan harga dokter jika ASURANSI
    } else {
        hatus.setText(""); // Kosongkan harga jika tidak ada pilihan yang valid
    }
    
    // Memanggil fungsi untuk menghitung total setelah update harga
    hitungTotal();
}
private void hitungTotal() {
    try {
        int hargaKamar = 0;
        int hargaDokter = Integer.parseInt(hadok.getText().replaceAll("[^\\d]", "")); // Mengambil harga dokter dari input
        int hargaHatus = Integer.parseInt(hatus.getText().replaceAll("[^\\d]", "")); // Mengambil harga hatus (status pembayaran)
        int durasi = Integer.parseInt((String) durasii.getSelectedItem().toString().replaceAll("[^\\d]", ""));
        
        // Menentukan harga kamar berdasarkan pilihan
        if ("Kamar A".equals(kamar.getSelectedItem().toString())) {
            hargaKamar = 100000;
        } else if ("Kamar B".equals(kamar.getSelectedItem().toString())) {
            hargaKamar = 75000;
        } else {
            hargaKamar = 50000;
        }
        
        // Menghitung total biaya
        int totalBiaya = hargaKamar * durasi + hargaDokter + hargaHatus; // Menjumlahkan semua harga
        total.setText(String.valueOf(totalBiaya)); // Menampilkan total biaya di textfield 'total'
    } catch (NumberFormatException e) {
        total.setText(""); // Mengosongkan total jika ada input yang tidak valid
    }
}




//end

//HAPOSS
private void deleteDataRanap() {
    try {
        // Check if the ID field is filled
        if (idpas.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Karyawan harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            idpas.requestFocus();
            return; // Stop the process if the ID field is empty
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return; // If the user selects "No", exit the method
        }

        // Prepare the SQL delete statement
        String sql = "DELETE FROM tb_ranap WHERE id_pasien = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, Integer.parseInt(idpas.getText())); // Ensure this is the correct ID
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
            loadData(); // Reload the data in the table
            napas.setText("");
            stapas.setSelectedItem("");
            kamar.setSelectedItem("");
            dok.setSelectedItem("");
            periksa.setText("");
            durasii.setSelectedItem("");
            total.setText("");
            idpas.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    } catch (SQLException e) {
        System.out.println("Error Hapus Data: " + e.getMessage());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "ID Pasien harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
//UPDATE

// Variabel untuk menyimpan ID pasien yang dipilih
private int selectedId;

// Metode untuk memuat data ke form edit
private void loadDataToEdit(int id) {
    try {
        String sql = "SELECT * FROM tb_ranap WHERE id_pasien = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = (ResultSet) ps.executeQuery();
        
        if (rs.next()) {
            // Mengisi komponen dengan data yang diambil
            napas.setText((String) rs.getString("nama"));
            stapas.setSelectedItem(rs.getString("status"));
            kamar.setSelectedItem(rs.getString("kamar"));
            dok.setSelectedItem(rs.getString("dokter"));
            periksa.setText((String) rs.getString("pemeriksaan"));
            durasii.setSelectedItem(rs.getString("durasi"));
            total.setText((String) rs.getString("total"));
            selectedId = id; // Simpan ID pasien yang dipilih
        }
        
        rs.close();
        ps.close();
    } catch (SQLException e) {
        System.out.println("Error Load Data to Edit: " + e.getMessage());
    }
}



// Metode untuk menyimpan perubahan
private void updateDataRawatInap() {
    if (napas.getText().isEmpty() || 
        stapas.getSelectedItem().toString().isEmpty() || 
        kamar.getSelectedItem().toString().isEmpty() || 
        dok.getSelectedItem().toString().isEmpty() || 
        periksa.getText().isEmpty() || 
        durasii.getSelectedItem().toString().isEmpty() || 
        total.getText().isEmpty()) {
        
        JOptionPane.showMessageDialog(this, "Isi data Rawat Inap terlebih dahulu", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        String sql = "UPDATE tb_ranap SET nama = ?, status = ?, kamar = ?, dokter = ?, pemeriksaan = ?, durasi = ?, total = ? WHERE id_pasien = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setString(1, napas.getText());
        ps.setString(2, (String) stapas.getSelectedItem());
        ps.setString(3, (String) kamar.getSelectedItem());
        ps.setString(4, (String) dok.getSelectedItem());
        ps.setString(5, periksa.getText());
        ps.setString(6, (String) durasii.getSelectedItem());
        ps.setString(7, total.getText());
        ps.setInt(8, selectedId); // ID pasien yang ingin diupdate
        
        ps.executeUpdate();
        
        JOptionPane.showMessageDialog(this, "Data Rawat Inap berhasil diupdate");
        
        // Memuat ulang data ke tabel
        loadData();
        
    } catch (SQLException e) {
        System.out.println("Error Update Data Rawat Inap: " + e.getMessage());
    }
}




//HAPOSS


private void saveDataRawatJalan() {
    if (namapasien.getText().isEmpty() || 
        status.getSelectedItem().toString().isEmpty() || 
        dokterr.getSelectedItem().toString().isEmpty() || 
        pemeriksaan.getText().isEmpty() || 
        total2.getText().isEmpty()) {
        
        JOptionPane.showMessageDialog(this, "Isi data Rawat Jalan terlebih dahulu", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Pastikan jumlah kolom sesuai dengan parameter di query SQL
        String sql = "INSERT INTO tb_rawatjalan (nama_pasien, status, dokter, pemeriksaan, total) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, namapasien.getText());
        ps.setString(2, (String) status.getSelectedItem());
        ps.setString(3, (String) dokterr.getSelectedItem());
        ps.setString(4, pemeriksaan.getText());
        ps.setString(5, total2.getText());
        ps.executeUpdate();
        JOptionPane.showMessageDialog(this, "Data Rawat Jalan berhasil disimpan");
        loadData1(); // Memanggil loadData1 untuk tabel Rawat Jalan
    } catch (SQLException e) {
        System.out.println("Error Save Data Rawat Jalan: " + e.getMessage());
    }
}

         // loadData() dan loadData1() untuk memuat ulang data tabel
    
    
    private void loadData1() {
        // Implementasi kode untuk memuat data Rawat Jalan dari database
    model.setRowCount(0); // Menghapus semua baris sebelum memuat data baru
    
    try {
        String sql = "SELECT * FROM tb_rawatjalan";
        java.sql.Statement st = conn.createStatement();
        java.sql.ResultSet rs = st.executeQuery(sql);
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("nama"),
                rs.getString("status"),
                rs.getString("kamar"),
                rs.getString("dokter"),
                rs.getString("pemeriksaan"),
                rs.getString("durasi"),
                rs.getString("total")
            });
        }
        
        rs.close();
        st.close();
    } catch (SQLException e) {
        System.out.println("Error Load Data Rawat Inap: " + e.getMessage());
    }
}
    
//TAMBAH DOKTER
   private void tambahDokter() {
    // Periksa setiap input untuk memastikan tidak ada yang kosong
    if (nama_dokter.getText().isEmpty() || 
        spesialis.getText().isEmpty() || 
        tlp.getText().isEmpty()) {
        
        JOptionPane.showMessageDialog(this, "Isi data dokter terlebih dahulu", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Query INSERT yang benar tanpa tanda koma di akhir
        String sql = "INSERT INTO tb_dokter (nama_dokter, spesialis, tlp) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        // Set parameter untuk query SQL
        ps.setString(1, nama_dokter.getText());
        ps.setString(2, spesialis.getText());
        ps.setString(3, tlp.getText());
        
        // Eksekusi query
        ps.executeUpdate();
        
        JOptionPane.showMessageDialog(this, "Data Dokter berhasil disimpan");
        
        // Refresh tabel untuk menampilkan data terbaru
        tampilkanDataDokter();
        
        // Reset form setelah berhasil menyimpan data
        nama_dokter.setText("");
        spesialis.setText("");
        tlp.setText("");
        
    } catch (SQLException e) {
        System.out.println("Error Save Data Dokter: " + e.getMessage());
        JOptionPane.showMessageDialog(this, "Gagal menyimpan data dokter: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


  private void tampilkanDataDokter() {
    DefaultTableModel modell = (DefaultTableModel) tb_dokter.getModel();
    modell.setRowCount(0);  // Bersihkan tabel sebelum menampilkan data
    
    try {
        Connection conn = koneksi.getConnection();
        String sql = "SELECT * FROM tb_dokter";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = (ResultSet) ps.executeQuery();
        
        while (rs.next()) {
            // Ambil data dari setiap kolom di tabel
            int idDokter = rs.getInt("id_dokter"); // Menggunakan getInt() untuk id_dokter
            String namaDokter = (String) rs.getString("nama_dokter");
            String spesialisDokter = (String) rs.getString("spesialis");
            String tlpDokter = (String) rs.getString("tlp");
            
            // Tambahkan data ke model tabel
            modell.addRow(new Object[]{idDokter, namaDokter, spesialisDokter, tlpDokter});
        }
        
    } catch (SQLException e) {
        System.out.println("Error Load Data Dokter: " + e.getMessage());
        JOptionPane.showMessageDialog(this, "Gagal menampilkan data dokter: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

  
  //EDITT
  private void editDokter() {
    // Periksa apakah ID dokter tersedia
    if (id_dokter.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Pilih data dokter yang ingin diedit.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Periksa input kosong
    if (nama_dokter.getText().isEmpty() || 
        spesialis.getText().isEmpty() || 
        tlp.getText().isEmpty()) {
        
        JOptionPane.showMessageDialog(this, "Isi data dokter terlebih dahulu", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Query UPDATE
        String sql = "UPDATE tb_dokter SET nama_dokter = ?, spesialis = ?, tlp = ? WHERE id_dokter = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        // Set parameter untuk query SQL
        ps.setString(1, nama_dokter.getText());
        ps.setString(2, spesialis.getText());
        ps.setString(3, tlp.getText());
        ps.setString(4, id_dokter.getText()); // ID dokter sebagai kondisi WHERE
        
        // Eksekusi query
        ps.executeUpdate();
        
        JOptionPane.showMessageDialog(this, "Data Dokter berhasil diperbarui");
        
        // Refresh tabel untuk menampilkan data terbaru
        tampilkanDataDokter();
        
        // Reset form setelah berhasil menyimpan data
        id_dokter.setText("");
        nama_dokter.setText("");
        spesialis.setText("");
        tlp.setText("");
        
    } catch (SQLException e) {
        System.out.println("Error Update Data Dokter: " + e.getMessage());
        JOptionPane.showMessageDialog(this, "Gagal mengubah data dokter: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
  


   
    

//   
    /**
     * Creates new form menu
     */
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpframe = new javax.swing.JPanel();
        jpheader = new javax.swing.JPanel();
        jpmenu = new javax.swing.JPanel();
        ranap = new javax.swing.JButton();
        raja = new javax.swing.JButton();
        trans = new javax.swing.JButton();
        profil = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ruang = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jpisi = new javax.swing.JPanel();
        rawatinap = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tb_ranap = new javax.swing.JTable();
        edit = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        idpas = new javax.swing.JTextField();
        jTextField14 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        cbdokter = new javax.swing.JComboBox<>();
        jTextField15 = new javax.swing.JTextField();
        jComboBox8 = new javax.swing.JComboBox<>();
        jTextField19 = new javax.swing.JTextField();
        jTextField20 = new javax.swing.JTextField();
        jTextField21 = new javax.swing.JTextField();
        jTextField22 = new javax.swing.JTextField();
        jTextField23 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        rawatjalan = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        namapasien = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        status = new javax.swing.JComboBox<>();
        jTextField8 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        dokterr = new javax.swing.JComboBox<>();
        jTextField9 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        total2 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_rawatjalan = new javax.swing.JTable();
        pemeriksaan = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        id2 = new javax.swing.JTextField();
        transaksi = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        idpasien = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        napas = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        stapas = new javax.swing.JComboBox<>();
        hatus = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        kamar = new javax.swing.JComboBox<>();
        dok = new javax.swing.JComboBox<>();
        periksa = new javax.swing.JTextField();
        durasii = new javax.swing.JComboBox<>();
        total = new javax.swing.JTextField();
        biayakamar = new javax.swing.JTextField();
        hadok = new javax.swing.JTextField();
        hadu = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        perawatan = new javax.swing.JComboBox<>();
        tambah = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        bersih = new javax.swing.JButton();
        profdok = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        id_dokter = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        nama_dokter = new javax.swing.JTextField();
        tambahdok = new javax.swing.JButton();
        editdokter = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tb_dokter = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        spesialis = new javax.swing.JTextField();
        tlp = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        ruangan = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jpframe.setBackground(new java.awt.Color(4, 4, 87));

        jpheader.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jpheaderLayout = new javax.swing.GroupLayout(jpheader);
        jpheader.setLayout(jpheaderLayout);
        jpheaderLayout.setHorizontalGroup(
            jpheaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpheaderLayout.setVerticalGroup(
            jpheaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
        );

        jpmenu.setBackground(new java.awt.Color(218, 218, 218));

        ranap.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ranap.setIcon(new javax.swing.ImageIcon("C:\\Users\\asus\\Downloads\\rawat_inap-removebg-preview(1).png")); // NOI18N
        ranap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ranapActionPerformed(evt);
            }
        });

        raja.setIcon(new javax.swing.ImageIcon("C:\\Users\\asus\\Downloads\\rawat_jalan-removebg-preview(1).png")); // NOI18N
        raja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rajaActionPerformed(evt);
            }
        });

        trans.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        trans.setIcon(new javax.swing.ImageIcon("C:\\Users\\asus\\Downloads\\transaksi-removebg-preview(2).png")); // NOI18N
        trans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transActionPerformed(evt);
            }
        });

        profil.setIcon(new javax.swing.ImageIcon("C:\\Users\\asus\\Downloads\\profil dokter.png")); // NOI18N
        profil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Rawat Inap");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Rawat Jalan");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Profil Dokter");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Transaksi");

        ruang.setIcon(new javax.swing.ImageIcon("C:\\Users\\asus\\Downloads\\ruangan(1).png")); // NOI18N
        ruang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ruangActionPerformed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel33.setText("Ruangan");

        javax.swing.GroupLayout jpmenuLayout = new javax.swing.GroupLayout(jpmenu);
        jpmenu.setLayout(jpmenuLayout);
        jpmenuLayout.setHorizontalGroup(
            jpmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpmenuLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jpmenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ruang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpmenuLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jpmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(trans)
                                    .addGroup(jpmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(ranap)
                                        .addComponent(raja, javax.swing.GroupLayout.Alignment.TRAILING)))
                                .addComponent(profil, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGroup(jpmenuLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jpmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel2))))))
                .addGap(181, 181, 181))
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpmenuLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel33)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpmenuLayout.setVerticalGroup(
            jpmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpmenuLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(ranap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(raja)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trans)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profil)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(ruang)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel33)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpisi.setBackground(new java.awt.Color(218, 218, 218));
        jpisi.setLayout(new java.awt.CardLayout());

        rawatinap.setBackground(new java.awt.Color(255, 204, 204));

        tb_ranap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID ", "Nama", "Status", "Ruangan", "Dokter", "Pemeriksaan", "Durasi", "Total"
            }
        ));
        tb_ranap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tb_ranapMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tb_ranap);

        edit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        edit.setText("Edit");
        edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setText("Hapus");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel34.setText("DATA RAWAT INAP");

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel35.setText("ID Pasien :");

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel36.setText("Nama Pasien :");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel38.setText("Pilih Status :");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel39.setText("Pilih Ruangan :");

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel40.setText("Pilih Dokter :");

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel41.setText("Pemeriksaan :");

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel42.setText("Pilih Durasi Inap :");

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel43.setText("Total keseluruhan :");

        idpas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idpasActionPerformed(evt);
            }
        });

        jTextField14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField14ActionPerformed(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbdokter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField21ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("Lihat");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("jButton2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rawatinapLayout = new javax.swing.GroupLayout(rawatinap);
        rawatinap.setLayout(rawatinapLayout);
        rawatinapLayout.setHorizontalGroup(
            rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rawatinapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rawatinapLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rawatinapLayout.createSequentialGroup()
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, rawatinapLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(rawatinapLayout.createSequentialGroup()
                                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35)
                                    .addComponent(jLabel36))
                                .addGap(39, 39, 39)
                                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(idpas)
                                    .addComponent(jTextField14)))
                            .addGroup(rawatinapLayout.createSequentialGroup()
                                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel43)
                                    .addComponent(jLabel42)
                                    .addComponent(jLabel41)
                                    .addComponent(jLabel40)
                                    .addComponent(jLabel39)
                                    .addComponent(jLabel38))
                                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(rawatinapLayout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cbdokter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jTextField15)))
                                    .addGroup(rawatinapLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jComboBox8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jTextField19))))
                                .addGap(18, 18, 18)
                                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField20)
                                    .addComponent(jTextField21)
                                    .addComponent(jTextField22)
                                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(rawatinapLayout.createSequentialGroup()
                        .addContainerGap(171, Short.MAX_VALUE)
                        .addComponent(jLabel34)
                        .addGap(134, 134, 134)))
                .addGap(21, 21, 21))
        );
        rawatinapLayout.setVerticalGroup(
            rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rawatinapLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(idpas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(cbdokter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatinapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit)
                    .addComponent(jButton3)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jpisi.add(rawatinap, "card2");

        rawatjalan.setBackground(new java.awt.Color(77, 203, 234));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("DATA RAWAT JALAN");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("Silahkan Masukkan Data Pasien");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setText("Nama Pasien :");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("Pilih Status Pasien :");

        status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setText("Pilih Dokter :");

        dokterr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setText("Total Keseluruhan :");

        total2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                total2ActionPerformed(evt);
            }
        });

        tb_rawatjalan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Pasien", "Nama", "Status", "Dokter", "Pemeriksaan", "Total"
            }
        ));
        jScrollPane1.setViewportView(tb_rawatjalan);

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setText("Pemeriksaan :");

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton5.setText("Edit");

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton6.setText("Hapus");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel31.setText("ID Pasien :");

        id2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                id2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rawatjalanLayout = new javax.swing.GroupLayout(rawatjalan);
        rawatjalan.setLayout(rawatjalanLayout);
        rawatjalanLayout.setHorizontalGroup(
            rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rawatjalanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rawatjalanLayout.createSequentialGroup()
                        .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(rawatjalanLayout.createSequentialGroup()
                                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel17)
                                    .addGroup(rawatjalanLayout.createSequentialGroup()
                                        .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel16)
                                            .addComponent(jLabel15)
                                            .addComponent(jLabel18)
                                            .addComponent(jLabel29)
                                            .addComponent(jLabel31))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(total2, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(rawatjalanLayout.createSequentialGroup()
                                                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(pemeriksaan, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(dokterr, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(status, javax.swing.GroupLayout.Alignment.LEADING, 0, 146, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                                                    .addComponent(jTextField9)))
                                            .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(id2, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(namapasien, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)))))
                                .addGap(0, 80, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rawatjalanLayout.createSequentialGroup()
                        .addGap(0, 161, Short.MAX_VALUE)
                        .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rawatjalanLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(149, 149, 149))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rawatjalanLayout.createSequentialGroup()
                                .addComponent(jButton5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6)
                                .addGap(16, 16, 16))))))
        );
        rawatjalanLayout.setVerticalGroup(
            rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rawatjalanLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(id2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namapasien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(18, 18, 18)
                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(dokterr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pemeriksaan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addGap(18, 18, 18)
                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(total2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(rawatjalanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jpisi.add(rawatjalan, "card3");

        transaksi.setBackground(new java.awt.Color(153, 102, 255));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("TRANSAKSI PEMBAYARAN");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Silahkan Masukkan Data Pasien :");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("ID Pasien :");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Nama Pasien :");

        napas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                napasActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Pilih Status Pasien :");

        stapas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih--", "Pasien Umum", "Pasien Asuransi", "Pasien BPJS" }));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Pilih Ruang Kamar :");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Pilih Durasi Inap :");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Total Keseluruhan :");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Pilih Dokter :");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Pemeriksaan :");

        kamar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih--", "Suite", "VVIP", "VIP", "Isolasi", "Kelas 1", "Kelas 2", "Kelas 3" }));
        kamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kamarActionPerformed(evt);
            }
        });

        dok.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        dok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dokActionPerformed(evt);
            }
        });

        periksa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                periksaActionPerformed(evt);
            }
        });

        durasii.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih--", "3 Hari", "4 Hari", "5 Hari", "6 Hari", "7 Hari" }));

        hadu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                haduActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Perawatan :");

        perawatan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih--", "Rawat Inap", "Rawat Jalan", " " }));

        tambah.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tambah.setText("Tambah");
        tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setText("Simpan");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        bersih.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bersih.setText("Bersih");
        bersih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bersihActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout transaksiLayout = new javax.swing.GroupLayout(transaksi);
        transaksi.setLayout(transaksiLayout);
        transaksiLayout.setHorizontalGroup(
            transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transaksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, transaksiLayout.createSequentialGroup()
                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel30))
                        .addGap(22, 22, 22)
                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(transaksiLayout.createSequentialGroup()
                                .addComponent(stapas, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(hatus, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                            .addGroup(transaksiLayout.createSequentialGroup()
                                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(periksa, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dok, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kamar, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)
                                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hadok)
                                    .addComponent(biayakamar)
                                    .addComponent(hadu))))
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, transaksiLayout.createSequentialGroup()
                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, transaksiLayout.createSequentialGroup()
                                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel28)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel5))
                                .addGap(53, 53, 53)
                                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(idpasien)
                                    .addComponent(napas)
                                    .addComponent(perawatan, 0, 132, Short.MAX_VALUE)))
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, transaksiLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(22, 22, 22)
                                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(durasii, 0, 135, Short.MAX_VALUE)
                                    .addComponent(total))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(transaksiLayout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addComponent(jLabel19)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transaksiLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tambah)
                .addGap(18, 18, 18)
                .addComponent(bersih)
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addGap(23, 23, 23))
        );
        transaksiLayout.setVerticalGroup(
            transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transaksiLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(idpasien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(napas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(perawatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(stapas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(kamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(biayakamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hadok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addComponent(periksa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(durasii, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hadu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tambah)
                    .addComponent(jButton4)
                    .addComponent(bersih))
                .addContainerGap(244, Short.MAX_VALUE))
        );

        jpisi.add(transaksi, "card4");

        profdok.setBackground(new java.awt.Color(40, 195, 195));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("PROFIL DOKTER");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("ID Dokter :");

        id_dokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                id_dokterActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Nama Dokter :");

        tambahdok.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tambahdok.setText("Tambah");
        tambahdok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahdokActionPerformed(evt);
            }
        });

        editdokter.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        editdokter.setText("Edit");
        editdokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editdokterActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton9.setText("Hapus");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        tb_dokter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Dokter", "Nama Dokter", "Spesialis", "No Tlp"
            }
        ));
        jScrollPane4.setViewportView(tb_dokter);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Spesialis :");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("No Tlp :");

        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton7.setText("Lihat");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout profdokLayout = new javax.swing.GroupLayout(profdok);
        profdok.setLayout(profdokLayout);
        profdokLayout.setHorizontalGroup(
            profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profdokLayout.createSequentialGroup()
                .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(profdokLayout.createSequentialGroup()
                        .addGap(137, 137, 137)
                        .addComponent(jLabel25))
                    .addGroup(profdokLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(profdokLayout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(38, 38, 38)
                                .addComponent(id_dokter, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(profdokLayout.createSequentialGroup()
                                .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel21))
                                .addGap(18, 18, 18)
                                .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(profdokLayout.createSequentialGroup()
                                        .addComponent(tambahdok)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(editdokter)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton7))
                                    .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(tlp, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(spesialis, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(nama_dokter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        profdokLayout.setVerticalGroup(
            profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profdokLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel25)
                .addGap(18, 18, 18)
                .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(id_dokter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(nama_dokter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(spesialis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(tlp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(profdokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tambahdok)
                    .addComponent(editdokter)
                    .addComponent(jButton9)
                    .addComponent(jButton7))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jpisi.add(profdok, "card5");

        ruangan.setBackground(new java.awt.Color(255, 153, 255));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("RUANGAN");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("ID Ruangan");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Nama Ruangan");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setText("Harga");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID Ruangan", "Nama Ruangan", "Harga"
            }
        ));
        jScrollPane3.setViewportView(jTable1);

        jButton10.setText("Tambah");

        jButton11.setText("Edit");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Hapus");

        javax.swing.GroupLayout ruanganLayout = new javax.swing.GroupLayout(ruangan);
        ruangan.setLayout(ruanganLayout);
        ruanganLayout.setHorizontalGroup(
            ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ruanganLayout.createSequentialGroup()
                .addGroup(ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ruanganLayout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(jLabel22))
                    .addGroup(ruanganLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(ruanganLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(26, 26, 26)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ruanganLayout.createSequentialGroup()
                                .addGroup(ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel32))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField12)
                                    .addComponent(jTextField11)
                                    .addGroup(ruanganLayout.createSequentialGroup()
                                        .addComponent(jButton10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton11)
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton12)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ruanganLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        ruanganLayout.setVerticalGroup(
            ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ruanganLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel22)
                .addGap(18, 18, 18)
                .addGroup(ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addGap(18, 18, 18)
                .addGroup(ruanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10)
                    .addComponent(jButton11)
                    .addComponent(jButton12))
                .addGap(9, 9, 9)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE))
        );

        jpisi.add(ruangan, "card6");

        javax.swing.GroupLayout jpframeLayout = new javax.swing.GroupLayout(jpframe);
        jpframe.setLayout(jpframeLayout);
        jpframeLayout.setHorizontalGroup(
            jpframeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpframeLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jpframeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpheader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpframeLayout.createSequentialGroup()
                        .addComponent(jpmenu, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpframeLayout.setVerticalGroup(
            jpframeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpframeLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jpheader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpframeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpmenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpisi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpframe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpframe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ranapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ranapActionPerformed
        jpisi.removeAll();
        jpisi.repaint();
        jpisi.revalidate();
        
        //menambahkan panel
        jpisi.add(rawatinap);
        jpisi.repaint();
        jpisi.revalidate();
        
    }//GEN-LAST:event_ranapActionPerformed

    private void total2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_total2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_total2ActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed

    private void id_dokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_id_dokterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_id_dokterActionPerformed

    private void rajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rajaActionPerformed
        jpisi.removeAll();
        jpisi.repaint();
        jpisi.revalidate();
        
        //menambahkan panel
        jpisi.add(rawatjalan);
        jpisi.repaint();
        jpisi.revalidate();
    }//GEN-LAST:event_rajaActionPerformed

    private void transActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transActionPerformed
         jpisi.removeAll();
        jpisi.repaint();
        jpisi.revalidate();
        
        //menambahkan panel
        jpisi.add(transaksi);
        jpisi.repaint();
        jpisi.revalidate();
    }//GEN-LAST:event_transActionPerformed

    private void profilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilActionPerformed
        jpisi.removeAll();
        jpisi.repaint();
        jpisi.revalidate();
        
        //menambahkan panel
        jpisi.add(profdok);
        jpisi.repaint();
        jpisi.revalidate();
    }//GEN-LAST:event_profilActionPerformed

    private void tambahdokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahdokActionPerformed
        // TODO add your handling code here:
   tambahDokter();

    }//GEN-LAST:event_tambahdokActionPerformed

    private void periksaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_periksaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_periksaActionPerformed

    private void haduActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_haduActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_haduActionPerformed

    private void napasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_napasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_napasActionPerformed

    private void id2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_id2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_id2ActionPerformed

    private void dokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dokActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void ruangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ruangActionPerformed
        jpisi.removeAll();
        jpisi.repaint();
        jpisi.revalidate();
        
        //menambahkan panel
        jpisi.add(ruangan);
        jpisi.repaint();
        jpisi.revalidate();
    }//GEN-LAST:event_ruangActionPerformed

    private void idpasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idpasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idpasActionPerformed

    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField14ActionPerformed

    private void jTextField21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField21ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField21ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahActionPerformed
        // TODO add your handling code here:
       saveDataRawatInap();
    }//GEN-LAST:event_tambahActionPerformed

    private void kamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kamarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kamarActionPerformed

    private void bersihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bersihActionPerformed
        // TODO add your handling code here:
        napas.setText("");
        perawatan.setSelectedItem(null);
        stapas.setSelectedItem(null);
        kamar.setSelectedItem(null);
        dok.setSelectedItem(null);
        periksa.setText("");
        durasii.setSelectedItem(null);
        total.setText("");
    }//GEN-LAST:event_bersihActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        tb_ranap.setValueAt("",tb_ranap.getSelectedRow(),0);
        tb_ranap.setValueAt("",tb_ranap.getSelectedRow(),1);
        tb_ranap.setValueAt("",tb_ranap.getSelectedRow(),2);
        tb_ranap.setValueAt("",tb_ranap.getSelectedRow(),3);
        tb_ranap.setValueAt("",tb_ranap.getSelectedRow(),4);
        tb_ranap.setValueAt("",tb_ranap.getSelectedRow(),5);
        tb_ranap.setValueAt("",tb_ranap.getSelectedRow(),6);
        tb_ranap.setValueAt("",tb_ranap.getSelectedRow(),7);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editActionPerformed
        // TODO add your handling code here:
        updateDataRawatInap();
    }//GEN-LAST:event_editActionPerformed

    private void editdokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editdokterActionPerformed
        // TODO add your handling code here:
        editDokter();
    }//GEN-LAST:event_editdokterActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        loadData();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tb_ranapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tb_ranapMouseClicked
        // TODO add your handling code here:
        
        
    }//GEN-LAST:event_tb_ranapMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        tambahDokter();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        deleteDataRanap();
    }//GEN-LAST:event_jButton9ActionPerformed

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
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menu().setVisible(true);
            }
        });
}



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bersih;
    private javax.swing.JTextField biayakamar;
    private javax.swing.JComboBox<String> cbdokter;
    private javax.swing.JComboBox<String> dok;
    private javax.swing.JComboBox<String> dokterr;
    private javax.swing.JComboBox<String> durasii;
    private javax.swing.JButton edit;
    private javax.swing.JButton editdokter;
    private javax.swing.JTextField hadok;
    private javax.swing.JTextField hadu;
    private javax.swing.JTextField hatus;
    private javax.swing.JTextField id2;
    private javax.swing.JTextField id_dokter;
    private javax.swing.JTextField idpas;
    private javax.swing.JTextField idpasien;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JPanel jpframe;
    private javax.swing.JPanel jpheader;
    private javax.swing.JPanel jpisi;
    private javax.swing.JPanel jpmenu;
    private javax.swing.JComboBox<String> kamar;
    private javax.swing.JTextField nama_dokter;
    private javax.swing.JTextField namapasien;
    private javax.swing.JTextField napas;
    private javax.swing.JTextField pemeriksaan;
    private javax.swing.JComboBox<String> perawatan;
    private javax.swing.JTextField periksa;
    private javax.swing.JPanel profdok;
    private javax.swing.JButton profil;
    private javax.swing.JButton raja;
    private javax.swing.JButton ranap;
    private javax.swing.JPanel rawatinap;
    private javax.swing.JPanel rawatjalan;
    private javax.swing.JButton ruang;
    private javax.swing.JPanel ruangan;
    private javax.swing.JTextField spesialis;
    private javax.swing.JComboBox<String> stapas;
    private javax.swing.JComboBox<String> status;
    private javax.swing.JButton tambah;
    private javax.swing.JButton tambahdok;
    private javax.swing.JTable tb_dokter;
    private javax.swing.JTable tb_ranap;
    private javax.swing.JTable tb_rawatjalan;
    private javax.swing.JTextField tlp;
    private javax.swing.JTextField total;
    private javax.swing.JTextField total2;
    private javax.swing.JButton trans;
    private javax.swing.JPanel transaksi;
    // End of variables declaration//GEN-END:variables

    private static class model {

        private static void addRow(Object[] object) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public model() {
        }
    }

    private static class ResultSet {

        public ResultSet() {
        }

        private boolean next() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private Object getString(String nama) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void close() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private int getInt(String id_dokter) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}



