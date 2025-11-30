import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * MiniBank - single-file console banking system
 * Save as: MiniBank.java
 *
 * Compile:
 *   javac MiniBank.java
 * Run:
 *   java MiniBank
 *
 * Data files (auto-created):
 *   accounts.csv       -> accNumber,name,pin,balance
 *   transactions.csv   -> id,accNumber,type,amount,date,description
 */
public class MiniBank {

    private static final String ACC_FILE = "accounts.csv";
    private static final String TX_FILE  = "transactions.csv";
    private static final Map<Long, Account> accounts = new HashMap<>();
    private static final List<Transaction> transactions = new ArrayList<>();
    private static long nextAccNumber = 1001001000L; // starting account number
    private static long nextTxId = 1L;
    private static final Scanner sc = new Scanner(System.in);
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public static void main(String[] args) {
        loadData();
        System.out.println("=== MiniBank Console ===");
        while (true) {
            printMenu();
            String cmd = sc.nextLine().trim();
            switch (cmd) {
                case "1": createAccount(); break;
                case "2": deposit(); break;
                case "3": withdraw(); break;
                case "4": transfer(); break;
                case "5": viewBalance(); break;
                case "6": viewTransactions(); break;
                case "7": saveData(); System.out.println("Data saved. Exiting..."); return;
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nSelect an option:");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. View Balance");
        System.out.println("6. View Transaction History");
        System.out.println("7. Save & Exit");
        System.out.print("> ");
    }

    // ---------- Account & Transaction classes ----------
    static class Account {
        long accNumber;
        String name;
        String pin; // simple 4-digit PIN stored as string (NOT secure for production)
        double balance;

        Account(long accNumber, String name, String pin, double balance) {
            this.accNumber = accNumber;
            this.name = name;
            this.pin = pin;
            this.balance = balance;
        }
    }

    static class Transaction {
        long id;
        long accNumber;
        String type; // DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT
        double amount;
        String date;
        String description;

        Transaction(long id, long accNumber, String type, double amount, String date, String description) {
            this.id = id;
            this.accNumber = accNumber;
            this.type = type;
            this.amount = amount;
            this.date = date;
            this.description = description;
        }
    }

    // ---------- Core operations ----------
    private static void createAccount() {
        System.out.print("Enter full name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        String pin;
        while (true) {
            System.out.print("Choose 4-digit PIN: ");
            pin = sc.nextLine().trim();
            if (pin.matches("\\d{4}")) break;
            System.out.println("PIN must be exactly 4 digits.");
        }
        long acc = nextAccNumber++;
        Account a = new Account(acc, name, pin, 0.0);
        accounts.put(acc, a);
        System.out.println("Account created! Account Number: " + acc);
        // initial transaction not needed; save later
    }

    private static void deposit() {
        Account a = promptAccount("Enter account number to deposit to: ");
        if (a == null) return;
        Double amt = promptAmount("Enter amount to deposit: ");
        if (amt == null) return;
        a.balance += amt;
        addTransaction(a.accNumber, "DEPOSIT", amt, "Deposit");
        System.out.println("Deposited ₹" + df.format(amt) + " to " + a.accNumber + ". New balance: ₹" + df.format(a.balance));
    }

    private static void withdraw() {
        Account a = promptAccountWithPin("Enter account number to withdraw from: ");
        if (a == null) return;
        Double amt = promptAmount("Enter amount to withdraw: ");
        if (amt == null) return;
        if (amt > a.balance) {
            System.out.println("Insufficient funds. Current balance: ₹" + df.format(a.balance));
            return;
        }
        a.balance -= amt;
        addTransaction(a.accNumber, "WITHDRAW", amt, "Withdrawal");
        System.out.println("Withdrew ₹" + df.format(amt) + ". New balance: ₹" + df.format(a.balance));
    }

    private static void transfer() {
        Account from = promptAccountWithPin("Enter your account number (from): ");
        if (from == null) return;
        System.out.print("Enter recipient account number (to): ");
        String sTo = sc.nextLine().trim();
        long toAcc;
        try { toAcc = Long.parseLong(sTo); } catch (Exception e) { System.out.println("Invalid account number."); return; }
        Account to = accounts.get(toAcc);
        if (to == null) { System.out.println("Recipient account not found."); return; }
        Double amt = promptAmount("Enter amount to transfer: ");
        if (amt == null) return;
        if (amt > from.balance) {
            System.out.println("Insufficient funds. Current balance: ₹" + df.format(from.balance));
            return;
        }
        from.balance -= amt;
        to.balance += amt;
        addTransaction(from.accNumber, "TRANSFER_OUT", amt, "Transfer to " + to.accNumber);
        addTransaction(to.accNumber, "TRANSFER_IN", amt, "Transfer from " + from.accNumber);
        System.out.println("Transferred ₹" + df.format(amt) + " to " + to.accNumber);
        System.out.println("Your new balance: ₹" + df.format(from.balance));
    }

    private static void viewBalance() {
        Account a = promptAccountWithPin("Enter account number to view balance: ");
        if (a == null) return;
        System.out.println("Account: " + a.accNumber + " | Name: " + a.name + " | Balance: ₹" + df.format(a.balance));
    }

    private static void viewTransactions() {
        Account a = promptAccountWithPin("Enter account number to view transactions: ");
        if (a == null) return;
        System.out.println("\nTransactions for " + a.accNumber + " (most recent first):");
        System.out.println("ID | Type         | Amount     | Date                | Description");
        System.out.println("------------------------------------------------------------------");
        transactions.stream()
                .filter(tx -> tx.accNumber == a.accNumber)
                .sorted(Comparator.comparingLong((Transaction t) -> t.id).reversed())
                .forEach(tx -> System.out.printf("%d | %-12s | %-10s | %-19s | %s\n",
                        tx.id, tx.type, "₹" + df.format(tx.amount), tx.date, tx.description));
    }

    // ---------- Helpers ----------
    private static Account promptAccount(String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        long acc;
        try { acc = Long.parseLong(s); } catch (Exception e) { System.out.println("Invalid account number."); return null; }
        Account a = accounts.get(acc);
        if (a == null) System.out.println("Account not found.");
        return a;
    }

    private static Account promptAccountWithPin(String prompt) {
        Account a = promptAccount(prompt);
        if (a == null) return null;
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine().trim();
        if (!pin.equals(a.pin)) {
            System.out.println("Incorrect PIN.");
            return null;
        }
        return a;
    }

    private static Double promptAmount(String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        double amt;
        try {
            amt = Double.parseDouble(s);
            if (amt <= 0) { System.out.println("Amount must be positive."); return null; }
        } catch (Exception e) {
            System.out.println("Invalid amount.");
            return null;
        }
        return amt;
    }

    private static void addTransaction(long accNumber, String type, double amount, String desc) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Transaction tx = new Transaction(nextTxId++, accNumber, type, amount, date, desc);
        transactions.add(tx);
    }

    // ---------- Persistence ----------
    private static void loadData() {
        // accounts
        File fAcc = new File(ACC_FILE);
        if (fAcc.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fAcc))) {
                String line;
                br.readLine(); // header
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",", -1);
                    if (p.length < 4) continue;
                    long acc = Long.parseLong(p[0]);
                    String name = p[1];
                    String pin = p[2];
                    double bal = Double.parseDouble(p[3]);
                    accounts.put(acc, new Account(acc, name, pin, bal));
                    nextAccNumber = Math.max(nextAccNumber, acc + 1);
                }
                System.out.println("Loaded accounts.");
            } catch (Exception e) { System.out.println("Failed to load accounts: " + e.getMessage()); }
        } else {
            // create file with header
            try (PrintWriter pw = new PrintWriter(new FileWriter(fAcc, false))) {
                pw.println("accNumber,name,pin,balance");
            } catch (IOException ignored) {}
        }

        // transactions
        File fTx = new File(TX_FILE);
        if (fTx.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fTx))) {
                String line;
                br.readLine(); // header
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",", -1);
                    if (p.length < 6) continue;
                    long id = Long.parseLong(p[0]);
                    long acc = Long.parseLong(p[1]);
                    String type = p[2];
                    double amt = Double.parseDouble(p[3]);
                    String date = p[4];
                    String desc = p[5];
                    transactions.add(new Transaction(id, acc, type, amt, date, desc));
                    nextTxId = Math.max(nextTxId, id + 1);
                }
                System.out.println("Loaded transactions.");
            } catch (Exception e) { System.out.println("Failed to load transactions: " + e.getMessage()); }
        } else {
            try (PrintWriter pw = new PrintWriter(new FileWriter(fTx, false))) {
                pw.println("id,accNumber,type,amount,date,description");
            } catch (IOException ignored) {}
        }
    }

    private static void saveData() {
        // save accounts
        try (PrintWriter pw = new PrintWriter(new FileWriter(ACC_FILE, false))) {
            pw.println("accNumber,name,pin,balance");
            for (Account a : accounts.values()) {
                // simple CSV escape: replace commas in name/desc (very basic)
                String name = a.name.replace(",", " ");
                pw.println(a.accNumber + "," + name + "," + a.pin + "," + a.balance);
            }
        } catch (IOException e) { System.out.println("Failed to save accounts: " + e.getMessage()); }

        // save transactions
        try (PrintWriter pw = new PrintWriter(new FileWriter(TX_FILE, false))) {
            pw.println("id,accNumber,type,amount,date,description");
            for (Transaction t : transactions) {
                String desc = t.description.replace(",", " ");
                pw.println(t.id + "," + t.accNumber + "," + t.type + "," + t.amount + "," + t.date + "," + desc);
            }
        } catch (IOException e) { System.out.println("Failed to save transactions: " + e.getMessage()); }
    }
}
