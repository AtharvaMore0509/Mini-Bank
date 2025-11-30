<h1>🏦 MiniBank — Banking System (Java)</h1>

<p>
  MiniBank is a simple Java-based console application that simulates basic banking operations.
  It allows users to create accounts, deposit and withdraw money, transfer funds, and view balances.
  All data is stored locally in CSV files, making the project lightweight and beginner-friendly.
</p>

<h2>✨ Features</h2>
<ul>
  <li>🆕 <strong>Create Account</strong> with auto-generated account number</li>
  <li>➕ <strong>Deposit</strong> money into an account</li>
  <li>➖ <strong>Withdraw</strong> funds (requires PIN)</li>
  <li>🔁 <strong>Transfer</strong> money between accounts</li>
  <li>💳 <strong>View Account Balance</strong></li>
  <li>📜 <strong>View Transaction History</strong> (most recent first)</li>
  <li>💾 <strong>CSV-based Data Storage</strong> (accounts & transactions)</li>
</ul>

<h2>🧠 How the Project Works</h2>
<p>
  MiniBank is a console-based banking simulation written in Java. Users can create accounts,
  deposit and withdraw money, transfer funds, and view account balances or transaction history.
  All data is stored locally using CSV files, which act as a lightweight database.
</p>

<ul>
  <li>📄 <strong>accounts.csv</strong> stores account details: account number, name, PIN, and balance.</li>
  <li>📜 <strong>transactions.csv</strong> stores every deposit, withdrawal, and transfer.</li>
  <li>🔢 Account numbers are auto-generated starting from a base value.</li>
  <li>🔐 Each account uses a 4-digit PIN for authentication during sensitive operations.</li>
  <li>💾 Every operation updates the CSV files instantly on exit with a “Save & Exit” option.</li>
</ul>


<h2>🧰 Technologies Used</h2>
<ul>
  <li>☕ <strong>Java</strong> — Core programming language</li>
  <li>📁 <strong>CSV File Handling</strong> — For storing account and transaction data</li>
  <li>🔤 <strong>Java I/O</strong> — Reading/writing files</li>
  <li>🧮 <strong>OOP Concepts</strong> — Classes, objects, methods, encapsulation</li>
</ul>

<h2>🖼️ Screenshots (Text Examples)</h2>

<h3>📷 Main Menu</h3>
<pre>
=== MiniBank Console ===
Select an option:
1. Create Account
2. Deposit
3. Withdraw
4. Transfer
5. View Balance
6. View Transaction History
7. Save & Exit
>
</pre>


<h3>📷 Deposit Example</h3>
<pre>
Enter account number to deposit to: 1001001001
Enter amount to deposit: 5000
Deposited ₹5,000.00 to 1001001001
New balance: ₹12,300.00
</pre>


<h3>📷 Withdraw Example</h3>
<pre>
Enter account number to withdraw from: 1001001001
Enter PIN: 1234
Enter amount to withdraw: 1500
Withdrew ₹1,500.00
New balance: ₹10,800.00
</pre>


<h3>📷 Transaction History Example</h3>
<pre>
Transactions for 1001001001:
ID | Type          | Amount     | Date                | Description
-----------------------------------------------------------------------
3  | WITHDRAW      | ₹1,500.00  | 2025-02-12 14:22:10 | Withdrawal
2  | DEPOSIT       | ₹5,000.00  | 2025-02-12 13:55:42 | Deposit
1  | DEPOSIT       | ₹8,800.00  | 2025-02-11 09:41:27 | Initial Deposit
</pre>

<h2>🔭 Future Improvements</h2>
<ul>
  <li>🔒 Secure PIN encryption (hashing)</li>
  <li>📄 Monthly bank statements</li>
  <li>🔎 Search accounts by name</li>
  <li>🖥️ GUI version using JavaFX or Swing</li>
  <li>🌐 Web version using Spring Boot</li>
</ul>
