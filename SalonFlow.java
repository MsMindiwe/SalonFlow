import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// ==========================================
// 1. ABSTRACTION (Abstract Class)
// ==========================================
abstract class Person {
    // 2. ENCAPSULATION (Private attributes)
    private String id;
    private String name;
    private String phone;

    public Person(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }

    // Abstract method to be overridden (Polymorphism)
    public abstract String getRoleDetails();
}

// ==========================================
// 3. INHERITANCE (Subclasses extending Person)
// ==========================================
class Customer extends Person {
    private int visitsCount;

    public Customer(String id, String name, String phone) {
        super(id, name, phone);
        this.visitsCount = 1;
    }

    public int getVisitsCount() { return visitsCount; }
    public void incrementVisits() { this.visitsCount++; }

    // 4. POLYMORPHISM (Method Overriding)
    @Override
    public String getRoleDetails() {
        return "Customer [Visits: " + visitsCount + "]";
    }
}

class Stylist extends Person {
    private String specialty;
    private boolean isAvailable;

    public Stylist(String id, String name, String phone, String specialty) {
        super(id, name, phone);
        this.specialty = specialty;
        this.isAvailable = true; // Default available
    }

    public String getSpecialty() { return specialty; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    // 4. POLYMORPHISM (Method Overriding)
    @Override
    public String getRoleDetails() {
        return "Stylist [Specialty: " + specialty + ", Status: " + (isAvailable ? "Available" : "Busy") + "]";
    }
}

// ==========================================
// 5. APPOINTMENT CLASS
// ==========================================
class Appointment {
    private String appointmentId;
    private Customer customer;
    private Stylist stylist;
    private String service;
    private String timeSlot;
    private String status; // Scheduled, Serving, Completed, Cancelled

    public Appointment(String appointmentId, Customer customer, Stylist stylist, String service, String timeSlot) {
        this.appointmentId = appointmentId;
        this.customer = customer;
        this.stylist = stylist;
        this.service = service;
        this.timeSlot = timeSlot;
        this.status = "Scheduled";
    }

    public String getAppointmentId() { return appointmentId; }
    public Customer getCustomer() { return customer; }
    public Stylist getStylist() { return stylist; }
    public String getService() { return service; }
    public String getTimeSlot() { return timeSlot; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

// ==========================================
// 6. QUEUE MANAGER CLASS (FIFO Queue Operations)
// ==========================================
class QueueManager {
    private Queue<Customer> waitingQueue;

    public QueueManager() {
        this.waitingQueue = new LinkedList<>();
    }

    public void addToQueue(Customer customer) {
        waitingQueue.add(customer);
    }

    public Customer serveNext() {
        return waitingQueue.poll();
    }

    public Queue<Customer> getWaitingQueue() {
        return waitingQueue;
    }

    public int getQueueSize() {
        return waitingQueue.size();
    }
}

// ==========================================
// 7. GUI PRESENTATION LAYER (SalonFlow Swing UI)
// ==========================================
public class SalonFlow extends JFrame {
    private List<Customer> customers = new ArrayList<>();
    private List<Stylist> stylists = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private QueueManager queueManager = new QueueManager();

    // GUI Components
    private DefaultTableModel customerModel, stylistModel, queueModel, appointmentModel;
    private JComboBox<String> customerCombo, stylistCombo, queueCustomerCombo;

    public SalonFlow() {
        setTitle("SalonFlow - Salon Appointment & Queue Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Seed Initial Sample Data
        seedData();

        // Setup UI Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabbedPane.addTab("Walk-in Queue", createQueuePanel());
        tabbedPane.addTab("Appointments", createAppointmentPanel());
        tabbedPane.addTab("Customers", createCustomerPanel());
        tabbedPane.addTab("Stylists", createStylistPanel());

        add(tabbedPane);
    }

    private void seedData() {
        Stylist s1 = new Stylist("ST01", "Amina Zulu", "0977112233", "Hair Braiding");
        Stylist s2 = new Stylist("ST02", "Chipo Banda", "0966445566", "Nails & Makeup");
        stylists.add(s1);
        stylists.add(s2);

        Customer c1 = new Customer("CU01", "Bwalya Phiri", "0955889900");
        Customer c2 = new Customer("CU02", "Natasha Kasonde", "0971234567");
        customers.add(c1);
        customers.add(c2);

        queueManager.addToQueue(c1);
    }

    // --- Queue Management Panel ---
    private JPanel createQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Add Customer to Walk-in Queue"));

        queueCustomerCombo = new JComboBox<>();
        refreshCustomerCombo();
        JButton addToQueueBtn = new JButton("Add to Queue");
        JButton serveNextBtn = new JButton("Serve Next Customer");
        serveNextBtn.setBackground(new Color(46, 139, 87));
        serveNextBtn.setForeground(Color.WHITE);

        topPanel.add(new JLabel("Select Customer:"));
        topPanel.add(queueCustomerCombo);
        topPanel.add(addToQueueBtn);
        topPanel.add(serveNextBtn);

        // Queue Table
        String[] columns = {"Queue Pos", "Customer ID", "Name", "Phone", "Status"};
        queueModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(queueModel);
        refreshQueueTable();

        addToQueueBtn.addActionListener(e -> {
            int selectedIdx = queueCustomerCombo.getSelectedIndex();
            if (selectedIdx >= 0) {
                Customer c = customers.get(selectedIdx);
                queueManager.addToQueue(c);
                refreshQueueTable();
                JOptionPane.showMessageDialog(this, c.getName() + " added to waiting queue!");
            }
        });

        serveNextBtn.addActionListener(e -> {
            Customer next = queueManager.serveNext();
            if (next != null) {
                next.incrementVisits();
                refreshQueueTable();
                refreshCustomerTable();
                JOptionPane.showMessageDialog(this, "Now Serving: " + next.getName() + "\nRole Info: " + next.getRoleDetails());
            } else {
                JOptionPane.showMessageDialog(this, "The queue is currently empty!");
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // --- Appointments Panel ---
    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Book New Appointment"));

        customerCombo = new JComboBox<>();
        stylistCombo = new JComboBox<>();
        refreshCustomerCombo();
        refreshStylistCombo();

        JTextField serviceTxt = new JTextField("Haircut & Styling");
        JTextField timeTxt = new JTextField("14:00 PM");
        JButton bookBtn = new JButton("Book Appointment");

        formPanel.add(new JLabel("Customer:"));
        formPanel.add(customerCombo);
        formPanel.add(new JLabel("Stylist:"));
        formPanel.add(stylistCombo);
        formPanel.add(new JLabel("Service:"));
        formPanel.add(serviceTxt);
        formPanel.add(new JLabel("Time Slot:"));
        formPanel.add(timeTxt);
        formPanel.add(new JLabel(""));
        formPanel.add(bookBtn);

        String[] cols = {"ID", "Customer", "Stylist", "Service", "Time", "Status"};
        appointmentModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(appointmentModel);

        bookBtn.addActionListener(e -> {
            if (customerCombo.getSelectedIndex() >= 0 && stylistCombo.getSelectedIndex() >= 0) {
                Customer c = customers.get(customerCombo.getSelectedIndex());
                Stylist s = stylists.get(stylistCombo.getSelectedIndex());
                
                String appID = "APP" + (appointments.size() + 1);
                Appointment app = new Appointment(appID, c, s, serviceTxt.getText(), timeTxt.getText());
                appointments.add(app);

                appointmentModel.addRow(new Object[]{appID, c.getName(), s.getName(), app.getService(), app.getTimeSlot(), app.getStatus()});
                JOptionPane.showMessageDialog(this, "Appointment Booked Successfully!");
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // --- Customer Management Panel ---
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Register New Customer"));

        JTextField nameTxt = new JTextField(12);
        JTextField phoneTxt = new JTextField(10);
        JButton addBtn = new JButton("Register Customer");

        form.add(new JLabel("Name:"));
        form.add(nameTxt);
        form.add(new JLabel("Phone:"));
        form.add(phoneTxt);
        form.add(addBtn);

        String[] cols = {"Customer ID", "Name", "Phone", "Total Visits", "Polymorphic Info"};
        customerModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(customerModel);
        refreshCustomerTable();

        addBtn.addActionListener(e -> {
            if (!nameTxt.getText().trim().isEmpty()) {
                String id = "CU0" + (customers.size() + 1);
                Customer c = new Customer(id, nameTxt.getText().trim(), phoneTxt.getText().trim());
                customers.add(c);
                refreshCustomerTable();
                refreshCustomerCombo();
                nameTxt.setText("");
                phoneTxt.setText("");
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // --- Stylist Management Panel ---
    private JPanel createStylistPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Register New Stylist"));

        JTextField nameTxt = new JTextField(10);
        JTextField phoneTxt = new JTextField(10);
        JTextField specTxt = new JTextField(10);
        JButton addBtn = new JButton("Add Stylist");

        form.add(new JLabel("Name:"));
        form.add(nameTxt);
        form.add(new JLabel("Phone:"));
        form.add(phoneTxt);
        form.add(new JLabel("Specialty:"));
        form.add(specTxt);
        form.add(addBtn);

        String[] cols = {"Stylist ID", "Name", "Specialty", "Availability", "Polymorphic Details"};
        stylistModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(stylistModel);
        refreshStylistTable();

        addBtn.addActionListener(e -> {
            if (!nameTxt.getText().trim().isEmpty()) {
                String id = "ST0" + (stylists.size() + 1);
                Stylist s = new Stylist(id, nameTxt.getText().trim(), phoneTxt.getText().trim(), specTxt.getText().trim());
                stylists.add(s);
                refreshStylistTable();
                refreshStylistCombo();
                nameTxt.setText("");
                phoneTxt.setText("");
                specTxt.setText("");
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // Helper Refresh Methods
    private void refreshQueueTable() {
        queueModel.setRowCount(0);
        int pos = 1;
        for (Customer c : queueManager.getWaitingQueue()) {
            queueModel.addRow(new Object[]{pos++, c.getId(), c.getName(), c.getPhone(), "Waiting"});
        }
    }

    private void refreshCustomerTable() {
        customerModel.setRowCount(0);
        for (Customer c : customers) {
            customerModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getVisitsCount(), c.getRoleDetails()});
        }
    }

    private void refreshStylistTable() {
        stylistModel.setRowCount(0);
        for (Stylist s : stylists) {
            stylistModel.addRow(new Object[]{s.getId(), s.getName(), s.getSpecialty(), s.isAvailable() ? "Available" : "Busy", s.getRoleDetails()});
        }
    }

    private void refreshCustomerCombo() {
        if (customerCombo != null) {
            customerCombo.removeAllItems();
            queueCustomerCombo.removeAllItems();
            for (Customer c : customers) {
                customerCombo.addItem(c.getName() + " (" + c.getId() + ")");
                queueCustomerCombo.addItem(c.getName() + " (" + c.getId() + ")");
            }
        }
    }

    private void refreshStylistCombo() {
        if (stylistCombo != null) {
            stylistCombo.removeAllItems();
            for (Stylist s : stylists) {
                stylistCombo.addItem(s.getName() + " - " + s.getSpecialty());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SalonFlow().setVisible(true);
        });
    }
}
