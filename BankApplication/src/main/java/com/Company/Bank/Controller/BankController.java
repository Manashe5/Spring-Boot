package com.Company.Bank.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.Company.Bank.entity.Account;
import com.Company.Bank.entity.Transaction;
import com.Company.Bank.entity.userLogin;
import com.Company.Bank.service.AccountService;
import com.Company.Bank.service.TransactionService;
import com.Company.Bank.service.UserService;

@Controller
public class BankController {

    @Autowired
    private UserService service;

    @Autowired
    private AccountService accservice;
    
    @Autowired
    private TransactionService transactionService;
    

    // Users methods--------------------------------------------------------------------------------------------------------------------------------
    
    @GetMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }
    
    //userSignup--------------------------------------------------------------
    @PostMapping("/register")
    public String addUser(@ModelAttribute userLogin ul, Model model) {
        if (service.emailExists(ul.getEmail())) {
            model.addAttribute("errorMessage", "Email is already registered.");
            return "signup"; // Show the signup page again with error message
        }
        model.addAttribute("success", "Registered Successfully");
        service.registerUser(ul);
        return "signup";
    }
    
    //userlogin---------------------------------------------------------------
    @GetMapping("/Home")
    public ModelAndView homePage() {
        ModelAndView mav = new ModelAndView("Home");
        mav.addObject("user", new userLogin());
        return mav;
    }
    @PostMapping("/Home")
    public String login(@ModelAttribute("user") userLogin user, Model model) {
        userLogin authUser = service.login(user.getEmail(), user.getPassword());

        if (Objects.nonNull(authUser)) {
            return "Home";
        } 
        else {
            model.addAttribute("error", "!Credentials are mismatched.");
            return "signin";
        }
    }

    // Accounts methods---------------------------------------------------------------------------------------------------------------------------------
    @GetMapping("/CreateAccount")
    public String create() {
        return "CreateAccount";
    }
    
    //create Account
    @PostMapping("/create")
    public String accountCreate(@ModelAttribute Account acc, Model model) {
        if (accservice.accountExists(acc.getAccountNo())) {
            model.addAttribute("errorMessage", "! Account Number assigned to another account. Assign another account number.");
            return "CreateAccount"; // Show the signup page again with error message
        }
        accservice.createAccount(acc);
        model.addAttribute("success", "Account Created Successfully");
        // Save the transaction
        String accountNo = acc.getAccountNo();
        double amount = acc.getBalance();
        Transaction transaction = new Transaction(accountNo, amount, "Initial Deposit", LocalDateTime.now(), amount);
        transactionService.saveTransaction(transaction);
        
        return "CreateAccount";
    }
    
    //list all accounts--------------------------------------------------------------------
    @GetMapping("/accounts")
    public String allAccounts(Model model) {
        List<Account> listaccount = accservice.listAll();
        model.addAttribute("listaccount", listaccount);
        return "AccountDetails";
    }
    @GetMapping("/searchAccount")
    public String searchAccount(@RequestParam("accountNo") String accountNo, Model model) {
          Account account = accservice.getAccount(accountNo);
          if (account != null) {
              model.addAttribute("listaccount", Collections.singletonList(account));
          } else {
              model.addAttribute("listaccount", new ArrayList<>());
          }
          return "AccountDetails"; // The name of your Thymeleaf template
      }    
    
    
    //deposit----------------------------------------------------------------------------------
    @GetMapping("/deposit")
    public String deposit() {
        return "Deposit";
    }
    //show details
    @PostMapping("/show")
    public String showAccountDetails(@RequestParam("accountNo") String accountNo,Model model) {
        Account account = accservice.getAccount(accountNo);

        if (account != null) {
        	
            model.addAttribute("account", account);
            return "ConfirmDeposit";
        }
        model.addAttribute("error", "Account not found");
        return "deposit";
    }
    //confirm deposit
    @PostMapping("/confirmDeposit")
    public String confirmDeposit(@RequestParam("accountNo") String accountNo, 
                                 @RequestParam("amount") double amount, Model model) {
        Account account = accservice.getAccount(accountNo);
        account.setBalance(account.getBalance() + amount);
        accservice.updateAccount(account);
        
        double Balance = account.getBalance();
        // Save the transaction
        Transaction transaction = new Transaction(accountNo, amount, "deposit", LocalDateTime.now(), Balance);
        transactionService.saveTransaction(transaction);
        
        model.addAttribute("account", account);
        model.addAttribute("success", "Deposit successful");
        return "ConfirmDeposit";
    }
    
    //withdraw---------------------------------------------------------------------------
    @GetMapping("/withdraw")
    public String withdraw() {
        return "withdraw";
    }
    //show
    @PostMapping("/withdraw-show")
    public String showwithdraw(@RequestParam("accountNo") String accountNo,Model model) {
        Account account = accservice.getAccount(accountNo);

        if (account != null) {
            model.addAttribute("account", account);
            return "ConfirmWithdraw";
        }
        model.addAttribute("error", "Account not found");
        return "withdraw";
    }
    
    //confirmwithdraw
    @PostMapping("/confirmWithdraw")
    public String confirmWithdraw(@RequestParam("accountNo") String accountNo, 
                                  @RequestParam("amount") double amount, Model model) {
        Account account = accservice.getAccount(accountNo);
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            accservice.updateAccount(account);
            double Balance = account.getBalance();

            // Save the transaction
            Transaction transaction = new Transaction(accountNo, amount, "withdraw", LocalDateTime.now(), Balance );
            transactionService.saveTransaction(transaction);
            
            model.addAttribute("account", account); // Add account to model for Thymeleaf
            model.addAttribute("success", "Withdraw successful");
            return "ConfirmWithdraw";
        }       
        model.addAttribute("account", account); // Add account to model for Thymeleaf
        model.addAttribute("Nosufficient", "Insufficient funds");
        return "ConfirmWithdraw";
    }

    
    //transfer----------------------------------------------------------------------------
    @GetMapping("/transfer")
    public String Transfer() {
        return "transfer";
    }
    //show
    @PostMapping("/transfer-show")
    public String showTransfer(@RequestParam("accountNo1") String account1, 
                               @RequestParam("accountNo2") String account2, Model model) {
        Account accountNo1 = accservice.getAccount(account1);
        Account accountNo2 = accservice.getAccount(account2);
        // Check if both accounts are found
        if (accountNo1 != null && accountNo2 != null) {
            model.addAttribute("accountNo1", accountNo1);
            model.addAttribute("accountNo2", accountNo2);
            return "ConfirmTransfer";
        }
        // Handle the case where the first account is not found
        if (accountNo1 == null) {
            model.addAttribute("acc1Error", "First account not found.");
        }
        // Handle the case where the second account is not found
        if (accountNo2 == null) {
            model.addAttribute("acc2Error", "Second account not found.");
        }
        // Return to the transfer page with appropriate error messages
        return "transfer";
    }

    //confirmTransfer
    @PostMapping("/ConfirmTransfer")
    public String showtransfer(@RequestParam("accountNo1") String account1, @RequestParam("accountNo2") String account2, @RequestParam("amount") double amount, Model model) {
    	Account accountNo1 = accservice.getAccount(account1);
        Account accountNo2 = accservice.getAccount(account2);
        if(amount<accountNo1.getBalance()) {
        	accountNo1.setBalance(accountNo1.getBalance() - amount);
        	accountNo2.setBalance(accountNo2.getBalance() + amount);
       		accservice.updateAccount(accountNo1);
       		accservice.updateAccount(accountNo2);
       		
            double Balance1 = accountNo1.getBalance();
            double Balance2 = accountNo2.getBalance();

            // Save the transactions
            Transaction transaction1 = new Transaction(account1, amount, "transfer out", LocalDateTime.now(), Balance1);
            Transaction transaction2 = new Transaction(account2, amount, "transfer in", LocalDateTime.now(), Balance2);
            transactionService.saveTransaction(transaction1);
            transactionService.saveTransaction(transaction2);
            
       		model.addAttribute("accountNo1", accountNo1);
       		model.addAttribute("accountNo2", accountNo2);
       		model.addAttribute("success", "Transfer successful");
       		return "ConfirmTransfer";
       	}
   		model.addAttribute("accountNo1", accountNo1);
   		model.addAttribute("accountNo2", accountNo2);
     	model.addAttribute("Nosufficient", "No sufficient funds");
       	return "ConfirmTransfer";
        }
    
    //fetch details-----------------------------------------------------------------------
    @GetMapping("/fetchDetails")
    public String fetch() {
    	return "FetchDetails";
    }
    //Fetch balance
    @PostMapping("/findBalance")
    public String fetchBalance(@RequestParam("accountNo") String accountNo, Model model) {
        Account account = accservice.getAccount(accountNo);
        if (account != null) {
            model.addAttribute("account", account);
            model.addAttribute("success", "Account details found successfully");
        } else {
            model.addAttribute("error", "Account not found");
        }
        return "FindBalance";
    }

    //fetch account
    @PostMapping("/findAccount")
    public String fetchAccounts(@RequestParam("accountNo") String accountNo, Model model) {
        Account account = accservice.getAccount(accountNo);
        if (account != null) {
            model.addAttribute("account", account);
            return "findAccount";
        }
        model.addAttribute("error", "Account not found");
        return "findAccount";
    }
    //fetch Account Number using name, number
    @PostMapping("/findAccountNumber")
    public String fetchAccountNumber(@RequestParam("first_name") String first,@RequestParam("last_name") String last,@RequestParam("PhoneNo") String phoneNo, Model model) {
        Account account = accservice.getAccountNumber(first,last,phoneNo);
        if (account != null) {
            model.addAttribute("account", account);
            return "findAccountNumber";
        }
        model.addAttribute("error", "Account not found");
        return "findAccountNumber";
    }
    
    //edit account details-------------------------------------------------------
    @GetMapping("/EditAccounts")
    public String editAccount(Model model) {
        List<Account> listaccount = accservice.listAll();
        model.addAttribute("listaccount", listaccount);
    	return "EditAccounts";
    }

    @GetMapping("/edit/{accNo}")
    public String editAccount(@PathVariable("accNo") String accNo, Model model) {
        Account account = accservice.getAccount(accNo);
        model.addAttribute("account", account);
        return "EditSingle";
    }

    @GetMapping("/delete/{id}")
    public String deleteAcc(@PathVariable(name = "id") long id) {
    	accservice.delete(id);
    	return "redirect:/EditAccounts";
    }
    
    @PostMapping("/updateAccount")
    public String updateAccount(@ModelAttribute("account") Account account) {
        accservice.updateAccount(account);
        return "redirect:/EditAccounts";
    }

     @GetMapping("/searchAccount2")
      public String searchAccount2(@RequestParam("accountNo") String accountNo, Model model) {
            Account account = accservice.getAccount(accountNo);
            if (account != null) {
                model.addAttribute("listaccount", Collections.singletonList(account));
            } else {
                model.addAttribute("listaccount", new ArrayList<>());
            }
            return "EditAccounts"; // The name of your Thymeleaf template
        }    
     
     //Transaction History
     @GetMapping("/THistory")
     public String History() {
    	 return "TransactionHistory";
     }
     @PostMapping("/THistory")
     public String getTransactionHistory(@RequestParam("accountNo") String accountNo, Model model) {
         List<Transaction> transactions = transactionService.getTransactionsByAccountNo(accountNo);
         model.addAttribute("transactions", transactions);
         return "TransactionHistory";
     }
}
