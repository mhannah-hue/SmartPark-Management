/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartParkManagement;

/**
 *
 * @author USER
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Shows who is parked right now.
 * NetBeans: right-click package SmartParkManagement > New > Java Class > ParkedVehicles
 * Click inside the file, press Ctrl+A, then paste this whole file.
 */
public class ParkedVehicles extends JFrame {

    public ParkedVehicles() {
        setTitle("Parked Vehicles");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel title = new JLabel("Currently Parked Vehicles", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 18));

        JLabel summary = new JLabel(
                "Cars: " + ParkingData.getOccupied("Car") + " / " + ParkingData.TOTAL_CAR_SLOTS
                + "     Motorcycles: " + ParkingData.getOccupied("Motorcycle") + " / " + ParkingData.TOTAL_MOTOR_SLOTS,
                SwingConstants.CENTER);
        summary.setFont(new Font("Cambria", Font.BOLD, 12));

        JPanel top = new JPanel(new java.awt.GridLayout(2, 1, 0, 6));
        top.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        top.add(title);
        top.add(summary);

        String[] columns = {"Slot", "Owner", "Plate Number", "Vehicle Type", "Date", "Time-in"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;                     // the table is view-only
            }
        };

        List<ParkingData.VehicleRecord> list = ParkingData.getParkedList();
        list.sort(Comparator.comparing((ParkingData.VehicleRecord r) -> r.type)
                .thenComparingInt(r -> r.slot));

        for (ParkingData.VehicleRecord r : list) {
            model.addRow(new Object[]{
                r.getSlotText(), r.owner, r.plate, r.type, r.getDateText(), r.getTimeInText()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(new Font("Cambria", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 12));

        JButton btnBack = new JButton("BACK");
        btnBack.setBackground(new Color(255, 0, 0));
        btnBack.setFont(new Font("Arial Black", Font.BOLD, 12));
        btnBack.addActionListener(e -> {
            new MainMenu().setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel();
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        bottom.add(btnBack);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        setSize(650, 450);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new ParkedVehicles().setVisible(true));
    }
}
